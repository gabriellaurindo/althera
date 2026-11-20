package com.darksune.althera.client.hud;

import com.darksune.althera.common.spell.PlayerSpell;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellCastMode;
import com.darksune.althera.common.spell.SpellDefinition;
import com.darksune.althera.common.spell.SpellElement;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.darksune.althera.common.spell.SpellTraining;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public final class SpellSlots {

    public static final String EMPTY_NAME = "Empty";

    private SpellSlots() {
    }

    @Nullable
    public static SpellNode node(PlayerSpellData data, int slot) {
        ResourceLocation id = data.getSlot(slot);
        return id == null ? null : SpellNodeRegistry.get(id);
    }

    public static Component name(PlayerSpellData data, int slot) {
        SpellNode node = node(data, slot);
        return Component.literal(node == null ? EMPTY_NAME : node.displayName());
    }

    public static ItemStack icon(@Nullable SpellNode node) {

        if (node == null) {
            return ItemStack.EMPTY;
        }

        return elementIcon(node.element());
    }

    public static ItemStack elementIcon(SpellElement element) {

        return new ItemStack(switch (element) {
            case FIRE -> Items.FIRE_CHARGE;
            case LIGHTNING -> Items.LIGHTNING_ROD;
            case ICE -> Items.BLUE_ICE;
            case NONE -> Items.ENCHANTED_BOOK;
        });
    }

    public static int manaCost(PlayerSpellData data, ResourceLocation id, PlayerSpell spell) {
        return data.isTraining(id)
                ? SpellTraining.trainingCost(spell.manaCost())
                : spell.proficiency().cost(spell.manaCost(), data.getSpellLevel(id));
    }

    @Nullable
    public static Component mana(PlayerSpellData data, int slot) {

        ResourceLocation id = data.getSlot(slot);
        SpellNode node = node(data, slot);
        SpellDefinition spell = node == null ? null : node.spellDefinition();

        if (spell == null) {
            return null;
        }

        PlayerSpell playerSpell = spell.player().orElseThrow();
        String unit = playerSpell.castMode() == SpellCastMode.CONTINUOUS ? " mana/s" : " mana";

        return Component.literal(manaCost(data, id, playerSpell) + unit);
    }

    @Nullable
    public static Component note(PlayerSpellData data, int slot) {

        ResourceLocation id = data.getSlot(slot);

        if (id == null || !data.isTraining(id)) {
            return null;
        }

        return Component.literal("Training " + (int) (data.getProgress(id) * 100.0F) + "%");
    }

    @Nullable
    public static Component level(PlayerSpellData data, int slot) {

        ResourceLocation id = data.getSlot(slot);

        if (id == null || !data.isLearned(id)) {
            return null;
        }

        return Component.literal("Level " + data.getSpellLevel(id));
    }

    @Nullable
    public static Component noteOrLevel(PlayerSpellData data, int slot) {

        Component note = note(data, slot);

        return note != null ? note : level(data, slot);
    }
}
