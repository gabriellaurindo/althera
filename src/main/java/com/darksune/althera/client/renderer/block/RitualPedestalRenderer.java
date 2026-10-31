package com.darksune.althera.client.renderer.block;

import com.darksune.althera.common.block.entity.RitualCoreBlockEntity;
import com.darksune.althera.common.block.entity.RitualPedestalBlockEntity;
import com.darksune.althera.common.multiblock.MultiblockValidator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class RitualPedestalRenderer implements BlockEntityRenderer<RitualPedestalBlockEntity> {

    private static final double FLOAT_HEIGHT = 1.15D;
    private static final double BOB_AMPLITUDE = 0.06D;
    private static final float BOB_SPEED = 0.08F;
    private static final float SPIN_DEGREES_PER_TICK = 1.5F;
    private static final float ITEM_SCALE = 1.25F;
    private static final double CORE_ITEM_HEIGHT = 0.6D;
    private static final double OFFERING_ARC_HEIGHT = 1.0D;
    private static final float OFFERING_SHRINK = 0.85F;
    private static final double RENDER_REACH = 2.0D;

    private final ItemRenderer itemRenderer;

    public RitualPedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(RitualPedestalBlockEntity pedestal, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        final ItemStack stack = pedestal.getItem();
        final Level level = pedestal.getLevel();
        if (stack.isEmpty() || level == null) return;

        final float time = level.getGameTime() + partialTick;
        final RitualCoreBlockEntity core = findChannelingCore(level, pedestal.getBlockPos());
        final float progress = core != null ? core.getChannelProgress(partialTick) : 0.0F;
        final float eased = progress * progress * (3.0F - 2.0F * progress);
        final BlockPos toCore = core != null ? core.getBlockPos().subtract(pedestal.getBlockPos()) : BlockPos.ZERO;
        final double floatY = FLOAT_HEIGHT + Mth.sin(time * BOB_SPEED) * BOB_AMPLITUDE;
        final float scale = ITEM_SCALE * (1.0F - OFFERING_SHRINK * eased);
        final int light = core != null
                ? LightTexture.FULL_BRIGHT
                : LevelRenderer.getLightColor(level, pedestal.getBlockPos().above());

        poseStack.pushPose();
        poseStack.translate(
                0.5D + toCore.getX() * eased,
                Mth.lerp(eased, floatY, toCore.getY() + CORE_ITEM_HEIGHT) + Mth.sin(progress * Mth.PI) * OFFERING_ARC_HEIGHT,
                0.5D + toCore.getZ() * eased);
        poseStack.mulPose(Axis.YP.rotationDegrees(time * SPIN_DEGREES_PER_TICK * (1.0F + 4.0F * progress)));
        poseStack.scale(scale, scale, scale);
        itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, level, (int) pedestal.getBlockPos().asLong());
        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(RitualPedestalBlockEntity pedestal) {
        return new AABB(pedestal.getBlockPos()).inflate(RENDER_REACH);
    }

    @Nullable
    private static RitualCoreBlockEntity findChannelingCore(final Level level, final BlockPos pedestalPos) {
        for (final BlockPos offset : MultiblockValidator.PEDESTAL_OFFSETS) {
            if (level.getBlockEntity(pedestalPos.subtract(offset)) instanceof RitualCoreBlockEntity core
                    && core.isOffered(pedestalPos)) {
                return core;
            }
        }
        return null;
    }
}
