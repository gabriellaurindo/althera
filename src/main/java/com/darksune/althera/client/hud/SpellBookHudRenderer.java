package com.darksune.althera.client.hud;

import com.darksune.althera.Althera;
import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.client.screen.SpellWheelScreen;
import com.darksune.althera.common.item.SpellBookItem;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellCastMode;
import com.darksune.althera.common.spell.SpellDefinition;
import com.darksune.althera.common.spell.SpellNode;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class SpellBookHudRenderer {

    private static final int SIZE = 18;
    private static final int HOTBAR_HALF_WIDTH = 91;
    private static final int GAP = 6;
    private static final int ATTACK_INDICATOR_WIDTH = 24;
    private static final int OFFHAND_SLOT_WIDTH = 29;
    private static final int BOTTOM_OFFSET = 20;
    private static final int TEXT_GAP = 4;
    private static final int OVERLAY_Z = 200;

    private static final int COLOR_BACKGROUND = 0x60000000;
    private static final int COLOR_READY_BORDER = 0xA0B070FF;
    private static final int COLOR_BLOCKED_BORDER = 0x80606060;
    private static final int COLOR_COOLDOWN_BORDER = 0x80E04040;
    private static final int COLOR_COOLDOWN_FILL = 0x40E04040;
    private static final int COLOR_CHARGE_FILL = 0x60FFC040;

    private static final int COLOR_NAME = 0xFFE0C8FF;
    private static final int COLOR_NAME_BLOCKED = 0xFF909090;
    private static final int COLOR_DETAIL = 0xFFB0B0B0;
    private static final int COLOR_TRAINING = 0xFF6FA8FF;
    private static final int COLOR_BLOCKED = 0xFFFF7070;

    private SpellBookHudRenderer() {
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui || player.isSpectator()) return;
        if (!SpellBookItem.isHeldBy(player) || mc.screen instanceof SpellWheelScreen) return;

        PlayerSpellData data = PlayerSpellData.get(player);
        int slot = data.getSelectedSlot();
        SpellNode node = SpellSlots.node(data, slot);
        SkillSlotState state = SkillSlotState.spell(player, slot);

        GuiGraphics gui = event.getGuiGraphics();
        int x = mc.getWindow().getGuiScaledWidth() / 2 + HOTBAR_HALF_WIDTH + GAP + rightSideOffset(mc, player);
        int y = mc.getWindow().getGuiScaledHeight() - BOTTOM_OFFSET;

        renderBox(gui, player, node, SpellSlots.icon(data.getSlot(slot)), state, x, y);
        renderText(gui, mc.font, data, slot, state, x + SIZE + TEXT_GAP, y);
    }

    private static int rightSideOffset(Minecraft mc, Player player) {

        boolean offhandOnRight = player.getMainArm() == HumanoidArm.LEFT;

        if (offhandOnRight && !player.getOffhandItem().isEmpty()) {
            return OFFHAND_SLOT_WIDTH;
        }

        if (!offhandOnRight && mc.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            return ATTACK_INDICATOR_WIDTH;
        }

        return 0;
    }

    private static void renderBox(GuiGraphics gui, Player player, @Nullable SpellNode node, HudIcon icon, SkillSlotState state,
                                  int x, int y) {

        int inner = SIZE - 2;

        gui.fill(x, y, x + SIZE, y + SIZE, borderColor(state));
        gui.fill(x + 1, y + 1, x + 1 + inner, y + 1 + inner, COLOR_BACKGROUND);

        if (node != null) {
            icon.renderFlat(gui, x + 1, y + 1, 1.0F, state.ready() ? 1.0F : 0.4F);
        }

        float fill = 0F;
        int fillColor = COLOR_COOLDOWN_FILL;

        if (state.onCooldown() && state.totalTicks() > 0) {
            fill = Math.min(1F, (float) state.timerTicks() / state.totalTicks());
        } else if (node != null && SpellBookItem.isCastingWith(player)) {
            fill = chargeProgress(player, node.spellDefinition());
            fillColor = COLOR_CHARGE_FILL;
        }

        if (fill > 0F) {
            int height = Mth.ceil(inner * fill);
            gui.fill(x + 1, y + 1 + inner - height, x + 1 + inner, y + 1 + inner, OVERLAY_Z, fillColor);
        }
    }

    private static float chargeProgress(Player player, @Nullable SpellDefinition spell) {

        if (spell == null || spell.player().orElseThrow().castMode() != SpellCastMode.CHARGE) {
            return 0F;
        }

        return Math.min(1F, (float) player.getTicksUsingItem() / Math.max(1, spell.castTicks()));
    }

    private static int borderColor(SkillSlotState state) {
        if (state.blocker() != null) return COLOR_BLOCKED_BORDER;
        if (state.onCooldown()) return COLOR_COOLDOWN_BORDER;
        return COLOR_READY_BORDER;
    }

    private static void renderText(GuiGraphics gui, Font font, PlayerSpellData data, int slot, SkillSlotState state,
                                   int x, int y) {

        Component name = Component.literal((slot + 1) + " ").append(SpellSlots.name(data, slot));
        gui.drawString(font, name, x, y, state.available() ? COLOR_NAME : COLOR_NAME_BLOCKED);

        Component note = SpellSlots.note(data, slot);
        Component mana = SpellSlots.mana(data, slot);

        if (SpellSlots.EMPTY_NAME.equals(state.blocker())) {
            if (SpellSlots.isBookEmpty(data)) {
                Component hint = data.hasDiscoveredSpell()
                        ? Component.literal("[").append(AltheraKeybinds.SPELL_TREE.getTranslatedKeyMessage())
                        .append("] Spell Tree")
                        : Component.literal("Find a Spell Tome");
                gui.drawString(font, hint, x, y + font.lineHeight, COLOR_TRAINING);
            }
            return;
        }

        if (state.blocker() != null) {
            gui.drawString(font, state.blocker(), x, y + font.lineHeight, COLOR_BLOCKED);
        } else if (state.onCooldown()) {
            gui.drawString(font, (state.timerTicks() + 19) / 20 + "s", x, y + font.lineHeight, COLOR_BLOCKED);
        } else if (note != null) {
            gui.drawString(font, note, x, y + font.lineHeight, COLOR_TRAINING);
        } else if (mana != null) {
            Component level = SpellSlots.level(data, slot);
            Component detail = level == null ? mana : level.copy().append(" · ").append(mana);
            gui.drawString(font, detail, x, y + font.lineHeight, COLOR_DETAIL);
        }
    }
}
