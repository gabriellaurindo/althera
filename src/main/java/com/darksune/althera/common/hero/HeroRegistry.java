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

    public static List<HeroDefinition> getRollable() {
        return HEROES.values().stream()
                .filter(HeroDefinition::isRollable)
                .toList();
    }

    public static List<HeroDefinition> getSelectable() {
        return HEROES.values().stream()
                .filter(HeroDefinition::isRollable)
                .sorted(Comparator.comparing(HeroDefinition::getName))
                .toList();
    }

    public static void clear() {
        HEROES.clear();
    }
}