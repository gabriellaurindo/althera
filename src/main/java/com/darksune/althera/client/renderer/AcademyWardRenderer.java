package com.darksune.althera.client.renderer;

import com.darksune.althera.Althera;
import com.darksune.althera.common.academy.Academy;
import com.darksune.althera.common.academy.AcademyLayout;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class AcademyWardRenderer {

    private static final int SEGMENTS = 128;

    private static final int WALL_RINGS = 4;

    private static final int DOME_RINGS = 12;

    private static final float WALL_BOTTOM = AcademyLayout.SURFACE_Y - 12.0F;

    private static final float WALL_TOP = AcademyLayout.SURFACE_Y + 8.0F;

    private static final float DOME_HEIGHT = 46.0F;

    private static final float RED = 0.75F;

    private static final float GREEN = 0.55F;

    private static final float BLUE = 1.0F;

    private static final float BASE_ALPHA = 0.035F;

    private static final float STRIPE_ALPHA = 0.04F;

    private static final float NEAR_ALPHA = 0.45F;

    private static final float NEAR_DISTANCE = 7.0F;

    private AcademyWardRenderer() {
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {

        Minecraft minecraft = Minecraft.getInstance();

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || minecraft.level == null
                || minecraft.player == null || !Academy.isIn(minecraft.level)) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        float time = (minecraft.level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false)) / 20.0F;

        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        VertexConsumer consumer = buffers.getBuffer(RenderType.lightning());
        Matrix4f matrix = pose.last().pose();

        int rings = WALL_RINGS + DOME_RINGS;

        for (int ring = 0; ring < rings; ring++) {
            for (int segment = 0; segment < SEGMENTS; segment++) {

                Vec3 a = point(segment, ring);
                Vec3 b = point(segment + 1, ring);
                Vec3 c = point(segment + 1, ring + 1);
                Vec3 d = point(segment, ring + 1);

                quad(consumer, matrix, minecraft.player, time, segment, a, b, c, d);
            }
        }

        pose.popPose();
        buffers.endBatch(RenderType.lightning());
    }

    private static Vec3 point(int segment, int ring) {

        double angle = Mth.TWO_PI * segment / SEGMENTS;
        double radius = AcademyLayout.wardRadius(angle);
        double y;

        if (ring <= WALL_RINGS) {
            y = WALL_BOTTOM + (WALL_TOP - WALL_BOTTOM) * ring / WALL_RINGS;
        } else {
            double latitude = Mth.HALF_PI * (ring - WALL_RINGS) / DOME_RINGS;
            radius *= Math.cos(latitude);
            y = WALL_TOP + DOME_HEIGHT * Math.sin(latitude);
        }

        return new Vec3(AcademyLayout.CENTER_X + 0.5D + Math.cos(angle) * radius, y,
                AcademyLayout.CENTER_Z + 0.5D + Math.sin(angle) * radius);
    }

    private static void quad(VertexConsumer consumer, Matrix4f matrix, Player player, float time, int segment,
                             Vec3 a, Vec3 b, Vec3 c, Vec3 d) {

        float stripe = STRIPE_ALPHA * (0.5F + 0.5F * Mth.sin(segment * 0.6F + time * 1.5F));

        vertex(consumer, matrix, player, a, stripe);
        vertex(consumer, matrix, player, b, stripe);
        vertex(consumer, matrix, player, c, stripe);
        vertex(consumer, matrix, player, d, stripe);

        vertex(consumer, matrix, player, d, stripe);
        vertex(consumer, matrix, player, c, stripe);
        vertex(consumer, matrix, player, b, stripe);
        vertex(consumer, matrix, player, a, stripe);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Player player, Vec3 point, float stripe) {

        double distance = point.distanceTo(player.position().add(0.0D, 1.0D, 0.0D));
        float near = (float) Math.max(0.0D, 1.0D - distance / NEAR_DISTANCE);
        float alpha = BASE_ALPHA + stripe + NEAR_ALPHA * near * near;

        consumer.addVertex(matrix, (float) point.x, (float) point.y, (float) point.z).setColor(RED, GREEN, BLUE, alpha);
    }
}
