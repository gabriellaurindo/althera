package com.darksune.althera.client.hud;

import com.darksune.althera.Althera;
import com.darksune.althera.common.spell.PlayerSpell;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellCastMode;
import com.darksune.althera.common.spell.SpellDefinition;
import com.darksune.althera.common.spell.SpellElement;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.darksune.althera.common.spell.SpellTraining;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class SpellSlots {

    public static final String EMPTY_NAME = "Empty";

    private static final String ICON_FOLDER = "textures/gui/spell/";

    private static final Map<ResourceLocation, Boolean> TEXTURE_EXISTS = new HashMap<>();

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

    public static boolean isBookEmpty(PlayerSpellData data) {

        for (int slot = 0; slot < PlayerSpellData.SLOT_COUNT; slot++) {
            if (data.getSlot(slot) != null) {
                return false;
            }
        }

        return true;
    }

    public static HudIcon icon(@Nullable ResourceLocation id) {

        SpellNode node = id == null ? null : SpellNodeRegistry.get(id);

        if (node == null) {
            return HudIcon.EMPTY;
        }

        ResourceLocation texture = existingTexture(iconTexture(id));

        if (texture == null) {
            texture = node.spell().map(spell -> existingTexture(iconTexture(spell))).orElse(null);
        }

        return texture != null ? HudIcon.of(texture) : elementIcon(node.element());
    }

    public static HudIcon elementIcon(SpellElement element) {
        return HudIcon.of(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID,
                ICON_FOLDER + "element/" + element.getSerializedName() + ".png"));
    }

    private static ResourceLocation iconTexture(ResourceLocation id) {
        return id.withPath(ICON_FOLDER + id.getPath() + ".png");
    }

    @Nullable
    private static ResourceLocation existingTexture(ResourceLocation texture) {
        boolean exists = TEXTURE_EXISTS.computeIfAbsent(texture,
                key -> Minecraft.getInstance().getResourceManager().getResource(key).isPresent());
        return exists ? texture : null;
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
