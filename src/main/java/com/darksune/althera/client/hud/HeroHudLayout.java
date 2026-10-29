package com.darksune.althera.client.hud;

import com.darksune.althera.client.AltheraClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public record HeroHudLayout(int portraitX, int portraitY, int barX, int healthY, int manaY, int interventionsY,
                            boolean mirrored) {

    public static final int PORTRAIT_SIZE = 34;
    public static final int BAR_WIDTH = 90;
    public static final int HEALTH_HEIGHT = 8;

    private static final int GAP = 5;
    private static final int TEXT_WIDTH = 44;
    private static final int WIDTH = PORTRAIT_SIZE + GAP + BAR_WIDTH + GAP + TEXT_WIDTH;
    private static final int HEIGHT = PORTRAIT_SIZE + 6;

    public static HeroHudLayout current(final Minecraft mc) {
        final AltheraClientConfig.HudCorner corner = AltheraClientConfig.HERO_HUD_CORNER.get();
        final int offsetX = AltheraClientConfig.HERO_HUD_OFFSET_X.get();
        final int offsetY = AltheraClientConfig.HERO_HUD_OFFSET_Y.get();
        final int screenWidth = mc.getWindow().getGuiScaledWidth();
        final int screenHeight = mc.getWindow().getGuiScaledHeight();

        final int left = corner.isRight() ? screenWidth - offsetX - WIDTH : offsetX;
        final int top = corner.isBottom() ? screenHeight - offsetY - HEIGHT : offsetY;
        final boolean mirrored = corner.isRight();

        final int portraitX = mirrored ? left + WIDTH - PORTRAIT_SIZE : left;
        final int barX = mirrored ? portraitX - GAP - BAR_WIDTH : left + PORTRAIT_SIZE + GAP;
        final int healthY = top + 4;
        final int manaY = healthY + HEALTH_HEIGHT + 4;

        return new HeroHudLayout(portraitX, top, barX, healthY, manaY, manaY + 11, mirrored);
    }

    public int labelX(final Font font, final String text) {
        return mirrored ? barX - GAP - font.width(text) : barX + BAR_WIDTH + GAP;
    }
}
