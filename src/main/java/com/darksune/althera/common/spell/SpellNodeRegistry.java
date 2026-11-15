package com.darksune.althera.common.spell;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellNodeRegistry {

    private static final Map<ResourceLocation, SpellNode> NODES = new HashMap<>();

    public static void register(ResourceLocation id, SpellNode node) {
        NODES.put(id, node);
    }

    @Nullable
    public static SpellNode get(ResourceLocation id) {
        return NODES.get(id);
    }

    public static Map<ResourceLocation, SpellNode> getAll() {
        return Map.copyOf(NODES);
    }

    public static List<ResourceLocation> getTrainableIds() {
        return NODES.entrySet().stream()
                .filter(entry -> entry.getValue().isTrainable())
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .toList();
    }

    public static void clear() {
        NODES.clear();
    }
}
