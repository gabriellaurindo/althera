package com.darksune.althera.client.layer;

import com.darksune.althera.client.seal.SealTattooRenderer;
import com.darksune.althera.client.seal.SealTattoos;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class SealLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public SealLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        final SealTattoos.Tattoo tattoo = SealTattoos.get(player);
        if (tattoo == null || player.isInvisible()) return;

        SealTattooRenderer.render(poseStack, buffer, packedLight, getParentModel().rightSleeve, tattoo, partialTick);
    }
}
