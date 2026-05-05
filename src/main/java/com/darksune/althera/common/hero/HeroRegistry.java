package com.darksune.althera.common.hero;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class HeroRegistry {

    private static final Map<ResourceLocation, HeroDefinition> HEROES = new HashMap<>();

    public static void register(HeroDefinition hero) {
        HEROES.put(hero.getId(), hero);
    }

    public static HeroDefinition get(ResourceLocation id) {
        return HEROES.get(id);
    }

    public static Collection<HeroDefinition> getAll() {
        return HEROES.values();
    }

    public static void clear() {
        HEROES.clear();
    }
}