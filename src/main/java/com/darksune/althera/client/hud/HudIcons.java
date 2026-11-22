package com.darksune.althera.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class HudIcons {

    public static final int ITEM_SIZE = 16;

    private HudIcons() {
    }

    public static void renderItem(GuiGraphics gui, ItemStack icon, int x, int y, float brightness, float alpha) {

        TextureAtlasSprite sprite = Minecraft.getInstance().getItemRenderer()
                .getModel(icon, null, null, 0)
                .getParticleIcon();

        gui.blit(x, y, 0, ITEM_SIZE, ITEM_SIZE, sprite, brightness, brightness, brightness, alpha);
    }

    public static void renderTexture(GuiGraphics gui, ResourceLocation texture, int x, int y, float brightness,
                                     float alpha) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        gui.setColor(brightness, brightness, brightness, alpha);
        gui.blit(texture, x, y, 0.0F, 0.0F, ITEM_SIZE, ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
