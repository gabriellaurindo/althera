package com.darksune.althera.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
}
