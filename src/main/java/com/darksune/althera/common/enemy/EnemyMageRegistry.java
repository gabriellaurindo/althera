package com.darksune.althera.common.enemy;

import com.darksune.althera.Althera;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EnemyMageRegistry {

    public static final ResourceLocation DEFAULT_ID = ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "noble");

    private static final Map<ResourceLocation, EnemyMageDefinition> MAGES = new HashMap<>();

    public static void register(ResourceLocation id, EnemyMageDefinition mage) {
        MAGES.put(id, mage);
    }

    @Nullable
    public static EnemyMageDefinition get(ResourceLocation id) {
        return MAGES.get(id);
    }

    @Nullable
    public static EnemyMageDefinition getOrDefault(ResourceLocation id) {
        EnemyMageDefinition mage = MAGES.get(id);
        return mage != null ? mage : MAGES.get(DEFAULT_ID);
    }

    public static Map<ResourceLocation, EnemyMageDefinition> getAll() {
        return Map.copyOf(MAGES);
    }

    public static void clear() {
        MAGES.clear();
    }
}
