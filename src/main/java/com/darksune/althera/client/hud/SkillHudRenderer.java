package com.darksune.althera.client.hud;

import com.darksune.althera.Althera;
import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.commandseal.CommandSealData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.darksune.althera.common.commandseal.skill.ICommandSealSkill;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.ultimate.UltimateData;
import com.darksune.althera.common.ultimate.UltimateDefinition;
import com.darksune.althera.common.ultimate.UltimateRegistry;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class SkillHudRenderer {

    private static final int SLOT_SIZE = 20;
    private static final int SLOT_GAP = 2;
    private static final int SEAL_SLOTS = 3;

    private static final int HOTBAR_HALF_WIDTH = 91;
    private static final int HOTBAR_MARGIN = 8;
    private static final int OFFHAND_SLOT_WIDTH = 29;
    private static final int ATTACK_INDICATOR_WIDTH = 20;

    private static final int COLOR_BORDER = 0xFF555555;
    private static final int COLOR_ACTIVE_BORDER = 0xFFFFAA00;
    private static final int COLOR_BACKGROUND = 0xA0000000;
    private static final int COLOR_COOLDOWN = 0x80FFFFFF;
    private static final int COLOR_UNAVAILABLE = 0x90000000;

    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int COLOR_TEXT_UNAVAILABLE = 0xFF777777;
    private static final int COLOR_TEXT_ACTIVE = 0xFFFFAA00;
    private static final int COLOR_TEXT_USED = 0xFFFF5555;

    private static final int COLOR_CHARGE = 0xFFE03050;
    private static final int COLOR_CHARGE_EMPTY = 0xFF4A2A30;

    private static final String CHARGE_GLYPH = "◆";
    private static final String USED_GLYPH = "✕";

    private record SlotState(KeyMapping key, int timerTicks, int totalTicks, boolean active, boolean available,
                             boolean usedToday) {
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui || player.isSpectator()) return;

        HeroData heroData = HeroData.get(player);

        HeroDefinition hero = heroData.getHeroDefinition();

        if (heroData.isHiddenHud() || hero == null) return;

        CommandSealData sealData = CommandSealData.get(player);
        UltimateData ultimateData = UltimateData.get(player);
        int mana = ManaData.hasInfiniteMana(player) ? Integer.MAX_VALUE : ManaData.get(player).getMana();

        List<SlotState> slots = List.of(
                sealSlot(AltheraKeybinds.COMMAND_SEAL_SKILL_ONE, CommandSealSkillType.OVERDRIVE, sealData, mana),
                sealSlot(AltheraKeybinds.COMMAND_SEAL_SKILL_TWO, CommandSealSkillType.REVIVE, sealData, mana),
                sealSlot(AltheraKeybinds.COMMAND_SEAL_SKILL_THREE, CommandSealSkillType.HEAL, sealData, mana),
                ultimateSlot(AltheraKeybinds.ULTIMATE_SKILL, hero.getUltimate(), ultimateData, mana, heroData.isDisguised())
        );

        GuiGraphics gui = event.getGuiGraphics();
        Font font = mc.font;

        int x = startX(mc, player);
        int y = mc.getWindow().getGuiScaledHeight() - SLOT_SIZE - 1;

        renderCharges(gui, font, x, y - 10, sealData.getCommandSealCharges());

        for (SlotState slot : slots) {
            renderSlot(gui, font, x, y, slot);
            x += SLOT_SIZE + SLOT_GAP;
        }
    }

    private static SlotState sealSlot(KeyMapping key, CommandSealSkillType type, CommandSealData data, int mana) {

        ICommandSealSkill skill = type.getSkill();
        boolean available = data.hasCharges() && mana >= skill.getManaCost();

        return slot(key,
                data.getActiveSkills().get(type), skill.getDurationTicks(),
                data.getCooldownSkills().get(type), skill.getCooldownTicks(),
                available, false);
    }

    private static SlotState ultimateSlot(KeyMapping key, ResourceLocation ultimateId, UltimateData data, int mana,
                                          boolean disguised) {

        UltimateDefinition ultimate = UltimateRegistry.get(ultimateId);
        boolean usedToday = data.wasUsedToday();

        if (ultimate == null) {
            return slot(key, null, 0, null, 0, false, usedToday);
        }

        boolean available = !usedToday && !disguised && mana >= ultimate.manaCost();

        return slot(key,
                data.getActiveSkills().get(ultimateId), ultimate.channelTicks(),
                data.getCooldownSkills().get(ultimateId), ultimate.cooldownTicks(),
                available, usedToday);
    }

    private static SlotState slot(KeyMapping key,
                                  @Nullable Integer activeTicks, int durationTicks,
                                  @Nullable Integer cooldownTicks, int cooldownTotalTicks,
                                  boolean available, boolean usedToday) {

        if (activeTicks != null) {
            return new SlotState(key, activeTicks, durationTicks, true, available, usedToday);
        }

        if (cooldownTicks != null) {
            return new SlotState(key, cooldownTicks, cooldownTotalTicks, false, available, usedToday);
        }

        return new SlotState(key, 0, 0, false, available, usedToday);
    }

    private static int startX(Minecraft mc, Player player) {

        int x = mc.getWindow().getGuiScaledWidth() / 2 + HOTBAR_HALF_WIDTH + HOTBAR_MARGIN;
        boolean leftHanded = player.getMainArm() == HumanoidArm.LEFT;

        if (leftHanded && !player.getOffhandItem().isEmpty()) {
            x += OFFHAND_SLOT_WIDTH;
        }

        if (!leftHanded && mc.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            x += ATTACK_INDICATOR_WIDTH;
        }

        return x;
    }

    private static void renderCharges(GuiGraphics gui, Font font, int x, int y, int charges) {

        int glyphWidth = font.width(CHARGE_GLYPH) + 1;
        int sealsWidth = SEAL_SLOTS * SLOT_SIZE + (SEAL_SLOTS - 1) * SLOT_GAP;
        int glyphX = x + (sealsWidth - CommandSealData.MAX_CHARGES * glyphWidth) / 2;

        for (int i = 0; i < CommandSealData.MAX_CHARGES; i++) {
            gui.drawString(font, CHARGE_GLYPH, glyphX, y, i < charges ? COLOR_CHARGE : COLOR_CHARGE_EMPTY);
            glyphX += glyphWidth;
        }
    }

    private static void renderSlot(GuiGraphics gui, Font font, int x, int y, SlotState slot) {

        int inner = SLOT_SIZE - 2;
        int centerX = x + SLOT_SIZE / 2;

        gui.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, slot.active() ? COLOR_ACTIVE_BORDER : COLOR_BORDER);
        gui.fill(x + 1, y + 1, x + 1 + inner, y + 1 + inner, COLOR_BACKGROUND);

        if (!slot.active() && slot.timerTicks() > 0 && slot.totalTicks() > 0) {
            int height = Mth.ceil(inner * Math.min(1f, (float) slot.timerTicks() / slot.totalTicks()));
            gui.fill(x + 1, y + 1 + inner - height, x + 1 + inner, y + 1 + inner, COLOR_COOLDOWN);
        }

        if (!slot.available() && !slot.active()) {
            gui.fill(x + 1, y + 1, x + 1 + inner, y + 1 + inner, COLOR_UNAVAILABLE);
        }

        int keyColor = slot.available() || slot.active() ? COLOR_TEXT : COLOR_TEXT_UNAVAILABLE;
        gui.drawCenteredString(font, keyLabel(font, slot.key()), centerX, y + 2, keyColor);

        if (slot.timerTicks() > 0) {
            int seconds = (slot.timerTicks() + 19) / 20;
            gui.drawCenteredString(font, String.valueOf(seconds), centerX, y + 11,
                    slot.active() ? COLOR_TEXT_ACTIVE : COLOR_TEXT);
        } else if (slot.usedToday()) {
            gui.drawCenteredString(font, USED_GLYPH, centerX, y + 11, COLOR_TEXT_USED);
        }
    }

    private static String keyLabel(Font font, KeyMapping key) {

        if (key.isUnbound()) {
            return "-";
        }

        return font.plainSubstrByWidth(key.getTranslatedKeyMessage().getString(), SLOT_SIZE - 4);
    }
}
