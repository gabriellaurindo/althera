package com.darksune.althera.client.seal;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor;

public final class SealTattooRenderer {

    private static final float INK_BRIGHTNESS = 0.8F;
    private static final int INK_ALPHA = 255;

    private SealTattooRenderer() {}

    public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ModelPart sleeve,
                              SealTattoos.Tattoo tattoo, float partialTick) {
        final boolean visible = sleeve.visible;
        sleeve.visible = true;
        renderLayers(poseStack, buffer, packedLight, sleeve, tattoo, partialTick);
        sleeve.visible = visible;
    }

    private static void renderLayers(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ModelPart sleeve,
                                     SealTattoos.Tattoo tattoo, float partialTick) {
        sleeve.render(poseStack, buffer.getBuffer(RenderType.entityTranslucent(tattoo.texture())), packedLight,
                OverlayTexture.NO_OVERLAY, tint(tattoo.color(), INK_BRIGHTNESS, INK_ALPHA));

        final float glow = tattoo.glow(partialTick);
        if (glow <= 0.0F) return;

        sleeve.render(poseStack, buffer.getBuffer(RenderType.eyes(tattoo.texture())), LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, tint(tattoo.color(), glow, 255));
    }

    private static int tint(int color, float brightness, int alpha) {
        return FastColor.ARGB32.color(alpha,
                (int) (FastColor.ARGB32.red(color) * brightness),
                (int) (FastColor.ARGB32.green(color) * brightness),
                (int) (FastColor.ARGB32.blue(color) * brightness));
    }
}
