package com.darksune.althera.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public final class SpellBookClientExtensions implements IClientItemExtensions {

    private static final float HAND_SIDE = 0.46F;
    private static final float HAND_HEIGHT = -0.36F;
    private static final float HAND_DEPTH = -0.78F;
    private static final float EQUIP_DROP = -0.6F;
    private static final float TILT_DEGREES = -14.0F;
    private static final float TURN_DEGREES = -8.0F;
    private static final float BOB_SPEED = 0.2F;
    private static final float BOB_HEIGHT = 0.015F;

    @Nullable
    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        return isCasting(entity, stack) ? HumanoidModel.ArmPose.BOW_AND_ARROW : null;
    }

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand,
                                           float partialTick, float equipProcess, float swingProcess) {

        if (!player.isUsingItem() || player.getUseItem() != itemInHand) {
            return false;
        }

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float bob = Mth.sin((player.getTicksUsingItem() + partialTick) * BOB_SPEED) * BOB_HEIGHT;

        poseStack.translate(side * HAND_SIDE, HAND_HEIGHT + bob + equipProcess * EQUIP_DROP, HAND_DEPTH);
        poseStack.mulPose(Axis.XP.rotationDegrees(TILT_DEGREES));
        poseStack.mulPose(Axis.YP.rotationDegrees(side * TURN_DEGREES));

        return true;
    }

    private static boolean isCasting(LivingEntity entity, ItemStack stack) {

        if (entity.isUsingItem() && entity.getUseItem() == stack) {
            return true;
        }

        return entity.swinging && entity.getItemInHand(entity.swingingArm) == stack;
    }
}
