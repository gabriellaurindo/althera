package com.darksune.althera.common.spell;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SpellRegistry {

    private static final Map<ResourceLocation, SpellDefinition> SPELLS = new HashMap<>();

    public static void register(ResourceLocation id, SpellDefinition spell) {
        SPELLS.put(id, spell);
    }

    @Nullable
    public static SpellDefinition get(ResourceLocation id) {
        return SPELLS.get(id);
    }

    public static void clear() {
        SPELLS.clear();
    }
}
