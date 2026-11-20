package com.darksune.althera.client.screen;

import com.darksune.althera.client.hud.SkillSlotState;
import com.darksune.althera.client.hud.SpellSlots;
import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.client.input.KeyActions;
import com.darksune.althera.common.spell.PlayerSpellData;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SpellWheelScreen extends RadialWheelScreen {

    public SpellWheelScreen() {
        super(Component.literal("Spells"));
    }

    @Override
    protected KeyMapping key() {
        return AltheraKeybinds.SPELL_WHEEL;
    }

    @Override
    protected List<Entry> entries(Player player) {

        PlayerSpellData data = PlayerSpellData.get(player);
        List<Entry> entries = new ArrayList<>();

        for (int slot = 0; slot < PlayerSpellData.SLOT_COUNT; slot++) {
            int index = slot;
            entries.add(new Entry(
                    SpellSlots.name(data, slot),
                    SpellSlots.icon(SpellSlots.node(data, slot)),
                    SpellSlots.mana(data, slot),
                    SpellSlots.noteOrLevel(data, slot),
                    SkillSlotState.spell(player, slot),
                    slot == data.getSelectedSlot(),
                    () -> KeyActions.selectSpellSlot(index)));
        }
        return entries;
    }
}
