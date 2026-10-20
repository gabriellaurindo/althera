package com.darksune.althera.common.hero;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class HeroClassRegistry {

    private static final Map<ResourceLocation, HeroClass> CLASSES = new HashMap<>();

    public static void register(ResourceLocation id, HeroClass heroClass) {
        CLASSES.put(id, heroClass.withId(id));
    }

    public static HeroClass get(ResourceLocation id) {
        return CLASSES.getOrDefault(id, HeroClass.NONE);
    }

    public static Map<ResourceLocation, HeroClass> getAll() {
        return Map.copyOf(CLASSES);
    }

    public static void clear() {
        CLASSES.clear();
    }
}
