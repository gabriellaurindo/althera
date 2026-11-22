package com.darksune.althera.common.quest;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuestRegistry {

    private static final Map<ResourceLocation, Quest> QUESTS = new HashMap<>();

    private QuestRegistry() {
    }

    public static void register(ResourceLocation id, Quest quest) {
        QUESTS.put(id, quest);
    }

    @Nullable
    public static Quest get(ResourceLocation id) {
        return QUESTS.get(id);
    }

    public static Map<ResourceLocation, Quest> getAll() {
        return Map.copyOf(QUESTS);
    }

    public static List<ResourceLocation> getOrderedIds() {
        return QUESTS.entrySet().stream()
                .sorted(Comparator.<Map.Entry<ResourceLocation, Quest>>comparingInt(entry -> entry.getValue().order())
                        .thenComparing(entry -> entry.getKey().toString()))
                .map(Map.Entry::getKey)
                .toList();
    }

    public static void clear() {
        QUESTS.clear();
    }
}
