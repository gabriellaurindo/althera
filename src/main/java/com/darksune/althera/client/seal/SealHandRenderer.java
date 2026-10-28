package com.darksune.althera.client.seal;

import com.darksune.althera.Althera;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class SealHandRenderer {

    private static final float RAISE_HEIGHT = 0.25F;
    private static final float RAISE_PITCH = 25.0F;

    private SealHandRenderer() {}

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        final AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isInvisible() || !holdsRightArm(player, event.getHand())) return;

        final SealTattoos.Tattoo tattoo = SealTattoos.get(player);
        if (tattoo == null) return;

        final float raise = tattoo.raise(event.getPartialTick());
        final boolean emptyMainHand = event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().isEmpty();
        if (raise <= 0.0F && !emptyMainHand) return;

        event.setCanceled(true);

        final PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        if (raise > 0.0F) {
            poseStack.translate(0.0F, raise * RAISE_HEIGHT, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(raise * RAISE_PITCH));
            placeArm(poseStack, 0.0F, emptyMainHand ? 0.0F : 1.0F - raise);
        } else {
            placeArm(poseStack, event.getSwingProgress(), event.getEquipProgress());
        }

        final PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        renderer.renderRightHand(poseStack, event.getMultiBufferSource(), event.getPackedLight(), player);
        SealTattooRenderer.render(poseStack, event.getMultiBufferSource(), event.getPackedLight(),
                renderer.getModel().rightSleeve, tattoo, event.getPartialTick());

        poseStack.popPose();
    }

    private static void placeArm(PoseStack poseStack, float swingProgress, float equipProgress) {
        final float swingRoot = Mth.sqrt(swingProgress);
        poseStack.translate(
                -0.3F * Mth.sin(swingRoot * Mth.PI) + 0.64F,
                0.4F * Mth.sin(swingRoot * Mth.TWO_PI) - 0.6F - equipProgress * 0.6F,
                -0.4F * Mth.sin(swingProgress * Mth.PI) - 0.72F);
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(swingRoot * Mth.PI) * 70.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(swingProgress * swingProgress * Mth.PI) * -20.0F));
        poseStack.translate(-1.0F, 3.6F, 3.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(120.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(200.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-135.0F));
        poseStack.translate(5.6F, 0.0F, 0.0F);
    }

    private static boolean holdsRightArm(AbstractClientPlayer player, InteractionHand hand) {
        final HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        return arm == HumanoidArm.RIGHT;
    }
}
