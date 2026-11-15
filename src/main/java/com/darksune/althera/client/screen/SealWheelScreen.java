package com.darksune.althera.client.screen;

import com.darksune.althera.client.hud.SkillSlotState;
import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.client.input.KeyActions;
import com.darksune.althera.common.commandseal.CommandSealData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.darksune.althera.common.skill.SkillLimits;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class SealWheelScreen extends RadialWheelScreen {

    private static final int COLOR_CHARGE = 0xFFE03050;
    private static final int COLOR_CHARGE_EMPTY = 0xFF4A2A30;

    private static final String CHARGE_GLYPH = "◆";

    public SealWheelScreen() {
        super(Component.literal("Command Seals"));
    }

    @Override
    protected KeyMapping key() {
        return AltheraKeybinds.SEAL;
    }

    @Override
    protected List<Entry> entries(Player player) {

        List<Entry> entries = new ArrayList<>();

        for (CommandSealSkillType type : CommandSealSkillType.values()) {
            int manaCost = type.getSkill().getManaCost();
            entries.add(new Entry(
                    Component.translatable("althera.commandseal." + type.getId()),
                    sealIcon(type),
                    manaCost > 0 ? Component.literal(manaCost + " mana") : null,
                    null,
                    SkillSlotState.seal(player, type),
                    false,
                    () -> KeyActions.handleCommandSealSkill(type)));
        }
        return entries;
    }

    @Override
    protected int centreExtraLines(Player player) {
        return SkillLimits.isUnlimited(player) ? 0 : 1;
    }

    @Override
    protected void renderCentreExtra(GuiGraphics gui, Player player, int centerX, int y) {

        int charges = CommandSealData.get(player).getCommandSealCharges();
        int glyphWidth = font.width(CHARGE_GLYPH) + 1;
        int x = centerX - CommandSealData.MAX_CHARGES * glyphWidth / 2;

        for (int i = 0; i < CommandSealData.MAX_CHARGES; i++) {
            gui.drawString(font, CHARGE_GLYPH, x, y, i < charges ? COLOR_CHARGE : COLOR_CHARGE_EMPTY);
            x += glyphWidth;
        }
    }

    private static ItemStack sealIcon(CommandSealSkillType type) {
        return new ItemStack(switch (type) {
            case OVERDRIVE -> Items.BLAZE_POWDER;
            case REVIVE -> Items.TOTEM_OF_UNDYING;
            case HEAL -> Items.GLISTERING_MELON_SLICE;
        });
    }
}
