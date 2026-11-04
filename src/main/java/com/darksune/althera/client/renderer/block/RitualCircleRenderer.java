package com.darksune.althera.client.renderer.block;

import com.darksune.althera.Althera;
import com.darksune.althera.common.block.entity.RitualCoreBlockEntity;
import com.darksune.althera.common.multiblock.MultiblockValidator;
import com.darksune.althera.common.multiblock.MultiblockValidator.CircleState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;

public class RitualCircleRenderer implements BlockEntityRenderer<RitualCoreBlockEntity> {

    public static final ModelResourceLocation CRYSTAL_MODEL = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "block/ritual_core_crystal"));

    private static final ResourceLocation OUTER_TEXTURE = texture("outer");
    private static final ResourceLocation INNER_TEXTURE = texture("inner");
    private static final ResourceLocation NODE_TEXTURE = texture("node");

    private static final float FLOOR_OFFSET = 0.02F;
    private static final float NODE_LIFT = 0.005F;
    private static final float INNER_LIFT = 0.012F;
    private static final float CIRCLE_HALF_SIZE = 2.5F;
    private static final float NODE_HALF_SIZE = 0.75F;

    private static final float DIM_ALPHA = 0.35F;
    private static final float LIT_ALPHA = 0.9F;
    private static final float PULSE_DEPTH = 0.15F;
    private static final float PULSE_SPEED = 0.08F;
    private static final float CHANNEL_ALPHA = 1.0F;

    private static final float TINT_RED = 0.75F;
    private static final float TINT_GREEN = 0.47F;
    private static final float TINT_BLUE = 1.0F;

    private static final float CRYSTAL_HEIGHT = 0.85F;
    private static final float CRYSTAL_BOB = 0.05F;
    private static final float CRYSTAL_BOB_SPEED = 0.1F;
    private static final float CRYSTAL_SPIN_FACTOR = -1.5F;
    private static final float CRYSTAL_CHANNEL_RISE = 0.3F;
    private static final float CRYSTAL_CHANNEL_GROWTH = 0.3F;
    private static final double CRYSTAL_REACH = 1.0D;

    public RitualCircleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RitualCoreBlockEntity core, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        final Level level = core.getLevel();
        if (level == null) return;

        final CircleState state = core.getCircleState();
        final float time = level.getGameTime() + partialTick;
        final float litAlpha = LIT_ALPHA - PULSE_DEPTH * (1.0F + Mth.sin(time * PULSE_SPEED)) / 2.0F;
        final float channelProgress = core.getChannelProgress(partialTick);
        final float circleAlpha = core.isChanneling()
                ? Mth.lerp(channelProgress, litAlpha, CHANNEL_ALPHA)
                : state.isComplete() ? litAlpha : DIM_ALPHA;
        final float nodeAlpha = core.isChanneling() ? circleAlpha : litAlpha;

        poseStack.pushPose();
        poseStack.translate(0.5F, FLOOR_OFFSET, 0.5F);

        drawFlat(poseStack.last(), buffer.getBuffer(RenderType.entityTranslucent(OUTER_TEXTURE)),
                0.0F, 0.0F, 0.0F, CIRCLE_HALF_SIZE, circleAlpha);

        final VertexConsumer nodes = buffer.getBuffer(RenderType.entityTranslucent(NODE_TEXTURE));
        for (int index = 0; index < MultiblockValidator.PEDESTAL_OFFSETS.size(); index++) {
            final BlockPos offset = MultiblockValidator.PEDESTAL_OFFSETS.get(index);
            drawFlat(poseStack.last(), nodes, offset.getX(), NODE_LIFT, offset.getZ(), NODE_HALF_SIZE,
                    state.hasPedestal(index) ? nodeAlpha : DIM_ALPHA);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(core.getInnerAngle(partialTick)));
        drawFlat(poseStack.last(), buffer.getBuffer(RenderType.entityTranslucent(INNER_TEXTURE)),
                0.0F, INNER_LIFT, 0.0F, CIRCLE_HALF_SIZE, circleAlpha);

        poseStack.popPose();

        renderCrystal(core, state, time, partialTick, channelProgress, poseStack, buffer, packedLight);
    }

    private static void renderCrystal(RitualCoreBlockEntity core, CircleState state, float time, float partialTick,
                                      float channelProgress, PoseStack poseStack, MultiBufferSource buffer,
                                      int packedLight) {
        final BakedModel model = Minecraft.getInstance().getModelManager().getModel(CRYSTAL_MODEL);
        final float height = CRYSTAL_HEIGHT + Mth.sin(time * CRYSTAL_BOB_SPEED) * CRYSTAL_BOB
                + channelProgress * CRYSTAL_CHANNEL_RISE;
        final float scale = 1.0F + channelProgress * CRYSTAL_CHANNEL_GROWTH;
        final int light = state.isComplete() || core.isChanneling() ? LightTexture.FULL_BRIGHT : packedLight;

        poseStack.pushPose();
        poseStack.translate(0.5F, height, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(core.getInnerAngle(partialTick) * CRYSTAL_SPIN_FACTOR));
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),
                buffer.getBuffer(Sheets.cutoutBlockSheet()), null, model, 1.0F, 1.0F, 1.0F, light,
                OverlayTexture.NO_OVERLAY, ModelData.EMPTY, Sheets.cutoutBlockSheet());
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(RitualCoreBlockEntity core) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(RitualCoreBlockEntity core) {
        return new AABB(core.getBlockPos()).inflate(CIRCLE_HALF_SIZE, 0.0D, CIRCLE_HALF_SIZE)
                .expandTowards(0.0D, CRYSTAL_REACH, 0.0D);
    }

    private static void drawFlat(PoseStack.Pose pose, VertexConsumer consumer, float centerX, float y, float centerZ,
                                 float halfSize, float alpha) {
        final float minX = centerX - halfSize;
        final float maxX = centerX + halfSize;
        final float minZ = centerZ - halfSize;
        final float maxZ = centerZ + halfSize;
        vertex(pose, consumer, minX, y, minZ, 0.0F, 0.0F, alpha);
        vertex(pose, consumer, minX, y, maxZ, 0.0F, 1.0F, alpha);
        vertex(pose, consumer, maxX, y, maxZ, 1.0F, 1.0F, alpha);
        vertex(pose, consumer, maxX, y, minZ, 1.0F, 0.0F, alpha);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z,
                               float u, float v, float alpha) {
        consumer.addVertex(pose, x, y, z)
                .setColor(TINT_RED, TINT_GREEN, TINT_BLUE, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "textures/block/ritual_circle/" + name + ".png");
    }
}
