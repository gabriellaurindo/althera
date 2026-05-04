package com.darksune.althera.common.hero;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class HeroRegistry {

    private static final Map<ResourceLocation, HeroDefinition> HEROES = new HashMap<>();

    public static void register(HeroDefinition hero) {
        HEROES.put(hero.id, hero);
    }

    public static HeroDefinition get(ResourceLocation id) {
        return HEROES.get(id);
    }

    public static Collection<HeroDefinition> getAll() {
        return HEROES.values();
    }

    public static List<HeroDefinition> getByClassAndRarity(ResourceLocation heroClass, ResourceLocation rarity) {
        List<HeroDefinition> result = new ArrayList<>();

        for (HeroDefinition hero : HEROES.values()) {
            if (hero.heroClass.equals(heroClass) && hero.rarity.equals(rarity)) {
                result.add(hero);
            }
        }

        return result;
    }

    public static void clear() {
        HEROES.clear();
    }
}