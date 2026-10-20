package com.darksune.althera.client.hud;

import com.darksune.althera.Althera;
import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.ManaData;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class ManaHudRenderer {

    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 6;
    private static final int LOW_MANA = 20;

    private static final int COLOR_SHADOW = 0x66000000;
    private static final int COLOR_BORDER = 0xFF0B1A2E;
    private static final int COLOR_BORDER_LOW = 0xFFB0303A;
    private static final int COLOR_BACKGROUND_TOP = 0xFF16263C;
    private static final int COLOR_BACKGROUND_BOTTOM = 0xFF0C1626;
    private static final int COLOR_FILL_TOP = 0xFF6FDCFF;
    private static final int COLOR_FILL_BOTTOM = 0xFF1A6FD6;
    private static final int COLOR_TRAIL = 0xAA9ADFFF;
    private static final int COLOR_HIGHLIGHT = 0x66FFFFFF;
    private static final int COLOR_SHIMMER = 0x2EFFFFFF;
    private static final int COLOR_TICK = 0x40000000;
    private static final int COLOR_TEXT = 0x9FE6FF;
    private static final int COLOR_TEXT_LOW = 0xFF8A8A;
    private static final int COLOR_EXHAUSTED_MARK = 0xFFFF8A8A;

    private static float displayedRatio = -1;
    private static float trailRatio = -1;
    private static long lastMillis;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) return;

        final ManaData manaData = player.getData(AltheraAttachments.MANA.get());

        boolean infinite = ManaData.hasInfiniteMana(player);
        int mana = manaData.getMana();
        int manaMax = manaData.getMaxMana();
        float ratio = infinite ? 1 : manaMax > 0 ? Mth.clamp((float) mana / manaMax, 0, 1) : 0;

        updateAnimation(ratio);

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int x = (width - BAR_WIDTH) / 2;
        int y = height - 48;

        int filled = Math.round(BAR_WIDTH * displayedRatio);
        int trail = Math.round(BAR_WIDTH * trailRatio);
        boolean exhausted = !infinite && manaData.isExhausted();
        boolean low = !infinite && mana < LOW_MANA || exhausted;
        long now = Util.getMillis();

        GuiGraphics gui = event.getGuiGraphics();

        gui.fill(x - 2, y - 2, x + BAR_WIDTH + 2, y + BAR_HEIGHT + 2, COLOR_SHADOW);
        gui.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, low ? lowBorder(now) : COLOR_BORDER);
        gui.fillGradient(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, COLOR_BACKGROUND_TOP, COLOR_BACKGROUND_BOTTOM);

        if (trail > filled) {
            gui.fill(x + filled, y, x + trail, y + BAR_HEIGHT, COLOR_TRAIL);
        }

        if (filled > 0) {
            gui.fillGradient(x, y, x + filled, y + BAR_HEIGHT, COLOR_FILL_TOP, COLOR_FILL_BOTTOM);
            gui.fill(x, y, x + filled, y + 1, COLOR_HIGHLIGHT);
            drawShimmer(gui, x, y, filled, now);
            gui.fill(x + filled - 1, y, x + filled, y + BAR_HEIGHT, 0xCCFFFFFF);
        }

        for (int i = 1; i < 10; i++) {
            int tickX = x + BAR_WIDTH * i / 10;
            gui.fill(tickX, y + 1, tickX + 1, y + BAR_HEIGHT - 1, COLOR_TICK);
        }

        if (exhausted) {
            int markX = x + BAR_WIDTH / 2;
            gui.fill(markX, y - 2, markX + 1, y + BAR_HEIGHT + 2, COLOR_EXHAUSTED_MARK);
        }

        gui.drawString(
                mc.font,
                infinite ? "∞" : mana + "/" + manaMax,
                x + BAR_WIDTH + 5,
                y + (BAR_HEIGHT - mc.font.lineHeight) / 2 + 1,
                low ? COLOR_TEXT_LOW : COLOR_TEXT
        );
    }

    private static void updateAnimation(float ratio) {
        long now = Util.getMillis();
        float delta = lastMillis == 0 ? 0 : Math.min(0.25F, (now - lastMillis) / 1000F);
        lastMillis = now;

        if (displayedRatio < 0) {
            displayedRatio = ratio;
            trailRatio = ratio;
            return;
        }

        displayedRatio += (ratio - displayedRatio) * Math.min(1, delta * 12);

        if (ratio >= trailRatio) {
            trailRatio = displayedRatio;
        } else {
            trailRatio = Math.max(displayedRatio, trailRatio - delta * 0.4F);
        }
    }

    private static void drawShimmer(GuiGraphics gui, int x, int y, int filled, long now) {
        int band = 14;
        int travel = BAR_WIDTH + band * 2;
        int start = x - band + (int) (now / 12 % travel);
        int left = Math.max(x, start);
        int right = Math.min(x + filled, start + band);

        if (right > left) {
            gui.fill(left, y + 1, right, y + BAR_HEIGHT - 1, COLOR_SHIMMER);
        }
    }

    private static int lowBorder(long now) {
        float pulse = (Mth.sin(now / 180F) + 1) / 2;
        return FastColor.ARGB32.lerp(pulse, COLOR_BORDER, COLOR_BORDER_LOW);
    }
}
