package com.darksune.althera.client.hud;

import com.darksune.althera.Althera;
import com.darksune.althera.client.AltheraClientConfig;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class SkillTimerHudRenderer {

    private static final int SIZE = 18;
    private static final int GAP = 3;
    private static final int TEXT_HEIGHT = 9;
    private static final int CROSSHAIR_OFFSET = 14;
    private static final int HERO_HUD_GAP = 4;
    private static final int OVERLAY_Z = 200;

    private static final float ICON_ALPHA = 0.67F;

    private static final int COLOR_BACKGROUND = 0x35000000;
    private static final int COLOR_ACTIVE_BORDER = 0x805BE37A;
    private static final int COLOR_ACTIVE_FILL = 0x355BE37A;
    private static final int COLOR_ACTIVE_TEXT = 0xAA8CF5A0;
    private static final int COLOR_COOLDOWN_BORDER = 0x80E04040;
    private static final int COLOR_COOLDOWN_FILL = 0x40E04040;
    private static final int COLOR_COOLDOWN_TEXT = 0xAAFF7070;

    private static final ItemStack ULTIMATE_ICON = new ItemStack(Items.NETHER_STAR);

    private record Timer(ItemStack icon, SkillSlotState state) {
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui || player.isSpectator()) return;

        HeroData heroData = HeroData.get(player);

        if (heroData.isHiddenHud() || heroData.getHeroDefinition() == null) return;

        List<Timer> timers = runningTimers(player);

        if (timers.isEmpty()) return;

        GuiGraphics gui = event.getGuiGraphics();
        Font font = mc.font;

        if (AltheraClientConfig.SKILL_TIMERS_POSITION.get() == AltheraClientConfig.SkillTimersPosition.CROSSHAIR) {
            int x = mc.getWindow().getGuiScaledWidth() / 2 + CROSSHAIR_OFFSET;
            int y = mc.getWindow().getGuiScaledHeight() / 2 - SIZE / 2;
            renderRow(gui, font, timers, x, y, 1);
            return;
        }

        HeroHudLayout layout = HeroHudLayout.current(mc);
        int y = AltheraClientConfig.HERO_HUD_CORNER.get().isBottom()
                ? layout.portraitY() - HERO_HUD_GAP - SIZE - TEXT_HEIGHT
                : layout.portraitY() + HeroHudLayout.PORTRAIT_SIZE + HERO_HUD_GAP;

        if (layout.mirrored()) {
            renderRow(gui, font, timers, layout.portraitX() + HeroHudLayout.PORTRAIT_SIZE - SIZE, y, -1);
        } else {
            renderRow(gui, font, timers, layout.portraitX(), y, 1);
        }
    }

    private static List<Timer> runningTimers(Player player) {

        List<Timer> timers = new ArrayList<>();

        for (CommandSealSkillType type : CommandSealSkillType.values()) {
            timers.add(new Timer(icon(type), SkillSlotState.seal(player, type)));
        }

        timers.add(new Timer(ULTIMATE_ICON, SkillSlotState.ultimate(player)));
        timers.removeIf(timer -> timer.state().timerTicks() <= 0);
        timers.sort(Comparator.comparing(timer -> !timer.state().active()));

        return timers;
    }

    private static ItemStack icon(CommandSealSkillType type) {
        return switch (type) {
            case OVERDRIVE -> new ItemStack(Items.BLAZE_POWDER);
            case REVIVE -> new ItemStack(Items.TOTEM_OF_UNDYING);
            case HEAL -> new ItemStack(Items.GLISTERING_MELON_SLICE);
        };
    }

    private static void renderRow(GuiGraphics gui, Font font, List<Timer> timers, int x, int y, int direction) {

        for (Timer timer : timers) {
            renderTimer(gui, font, x, y, timer);
            x += direction * (SIZE + GAP);
        }
    }

    private static void renderTimer(GuiGraphics gui, Font font, int x, int y, Timer timer) {

        SkillSlotState state = timer.state();
        boolean active = state.active();
        int inner = SIZE - 2;

        gui.fill(x, y, x + SIZE, y + SIZE, active ? COLOR_ACTIVE_BORDER : COLOR_COOLDOWN_BORDER);
        gui.fill(x + 1, y + 1, x + 1 + inner, y + 1 + inner, COLOR_BACKGROUND);
        HudIcons.renderItem(gui, timer.icon(), x + 1, y + 1, 1.0F, ICON_ALPHA);

        if (state.totalTicks() > 0) {
            int height = Mth.ceil(inner * Math.min(1f, (float) state.timerTicks() / state.totalTicks()));
            gui.fill(x + 1, y + 1 + inner - height, x + 1 + inner, y + 1 + inner, OVERLAY_Z,
                    active ? COLOR_ACTIVE_FILL : COLOR_COOLDOWN_FILL);
        }

        int seconds = (state.timerTicks() + 19) / 20;
        gui.drawCenteredString(font, seconds + "s", x + SIZE / 2, y + SIZE + 1,
                active ? COLOR_ACTIVE_TEXT : COLOR_COOLDOWN_TEXT);
    }
}
