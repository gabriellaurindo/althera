package com.darksune.althera.client.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record HudIcon(ItemStack item, @Nullable ResourceLocation texture) {

    public static final HudIcon EMPTY = new HudIcon(ItemStack.EMPTY, null);

    public static HudIcon of(ItemStack item) {
        return new HudIcon(item, null);
    }

    public static HudIcon of(ResourceLocation texture) {
        return new HudIcon(ItemStack.EMPTY, texture);
    }

    public boolean isEmpty() {
        return texture == null && item.isEmpty();
    }

    public void render(GuiGraphics gui, int x, int y) {
        if (texture != null) {
            HudIcons.renderTexture(gui, texture, x, y, 1.0F, 1.0F);
        } else if (!item.isEmpty()) {
            gui.renderItem(item, x, y);
        }
    }

    public void renderFlat(GuiGraphics gui, int x, int y, float brightness, float alpha) {
        if (texture != null) {
            HudIcons.renderTexture(gui, texture, x, y, brightness, alpha);
        } else if (!item.isEmpty()) {
            HudIcons.renderItem(gui, item, x, y, brightness, alpha);
        }
    }
}
