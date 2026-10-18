package com.darksune.althera.common.ultimate;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class UltimateRegistry {

    private static final Map<ResourceLocation, UltimateDefinition> ULTIMATES = new HashMap<>();

    public static void register(ResourceLocation id, UltimateDefinition ultimate) {
        ULTIMATES.put(id, ultimate);
    }

    @Nullable
    public static UltimateDefinition get(ResourceLocation id) {
        return ULTIMATES.get(id);
    }

    public static Map<ResourceLocation, UltimateDefinition> getAll() {
        return Map.copyOf(ULTIMATES);
    }

    public static void clear() {
        ULTIMATES.clear();
    }
}
