package com.darksune.althera.common.spell;

import com.darksune.althera.Althera;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.stream.Stream;

public final class SpellAdvancements {

    public static final int ADEPT_LEVEL = 5;

    private static final String CRITERION = "unlock";

    private static final ResourceLocation FIRST_SPELL = id("spells/first_spell");

    private static final ResourceLocation ADEPT = id("spells/adept");

    private static final ResourceLocation ARCHMAGE = id("spells/archmage");

    private SpellAdvancements() {
    }

    public static void check(final ServerPlayer player) {

        final PlayerSpellData data = PlayerSpellData.get(player);
        final List<ResourceLocation> spells = SpellNodeRegistry.getTrainableIds();

        if (spells.stream().anyMatch(data::isLearned)) {
            award(player, FIRST_SPELL);
        }

        if (spells.stream().anyMatch(id -> data.isLearned(id) && data.getSpellLevel(id) >= ADEPT_LEVEL)) {
            award(player, ADEPT);
        }

        if (Stream.of(SpellElement.FIRE, SpellElement.LIGHTNING, SpellElement.ICE)
                .allMatch(element -> data.getElementLevel(element) >= SpellProficiency.MAX_LEVEL)) {
            award(player, ARCHMAGE);
        }
    }

    private static void award(final ServerPlayer player, final ResourceLocation id) {

        final AdvancementHolder advancement = player.server.getAdvancements().get(id);

        if (advancement != null && !player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            player.getAdvancements().award(advancement, CRITERION);
        }
    }

    private static ResourceLocation id(final String path) {
        return ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, path);
    }
}
