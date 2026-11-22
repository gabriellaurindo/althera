package com.darksune.althera.client.renderer.entity;

import com.darksune.althera.Althera;
import com.darksune.althera.common.entity.HeroProjectileEntity;
import com.darksune.althera.common.ultimate.effect.ProjectileVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HeroProjectileRenderer extends EntityRenderer<HeroProjectileEntity> {

    private static final ResourceLocation FALLBACK =
            ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "textures/entity/projectile/orb.png");

    public HeroProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(HeroProjectileEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light) {

        ProjectileVisual visual = entity.getVisual();

        if (visual == null) {
            return;
        }

        RenderType type = visual.glow()
                ? RenderType.entityTranslucentEmissive(visual.texture())
                : RenderType.entityTranslucent(visual.texture());
        VertexConsumer consumer = buffers.getBuffer(type);
        int usedLight = visual.glow() ? LightTexture.FULL_BRIGHT : light;
        float time = entity.tickCount + partialTick;
        float half = visual.size() / 2.0F;

        pose.pushPose();

        if (visual.stretch() > 1.0F) {
            pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
            pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
            pose.mulPose(Axis.XP.rotationDegrees(time * visual.spin()));

            float length = half * visual.stretch();
            quad(consumer, pose, visual, usedLight, length, half, false);
            quad(consumer, pose, visual, usedLight, length, half, true);
        } else {
            pose.mulPose(entityRenderDispatcher.cameraOrientation());
            pose.mulPose(Axis.ZP.rotationDegrees(time * visual.spin()));
            quad(consumer, pose, visual, usedLight, half, half, false);
        }

        pose.popPose();
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }

    private static void quad(VertexConsumer consumer, PoseStack pose, ProjectileVisual visual, int light,
                             float halfLength, float halfWidth, boolean flat) {

        PoseStack.Pose last = pose.last();
        float[][] corners = {{-halfLength, -halfWidth}, {halfLength, -halfWidth}, {halfLength, halfWidth}, {-halfLength, halfWidth}};
        float[][] uv = {{0, 1}, {1, 1}, {1, 0}, {0, 0}};

        for (int i = 0; i < 4; i++) {
            vertex(consumer, last, corners[i][0], corners[i][1], flat, uv[i][0], uv[i][1], visual.color(), light);
        }

        for (int i = 3; i >= 0; i--) {
            vertex(consumer, last, corners[i][0], corners[i][1], flat, uv[i][0], uv[i][1], visual.color(), light);
        }
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, boolean flat, float u,
                               float v, int color, int light) {
        consumer.addVertex(pose, x, flat ? 0.0F : y, flat ? y : 0.0F)
                .setColor(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(HeroProjectileEntity entity) {
        ProjectileVisual visual = entity.getVisual();
        return visual == null ? FALLBACK : visual.texture();
    }
}
