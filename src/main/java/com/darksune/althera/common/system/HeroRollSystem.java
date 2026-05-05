package com.darksune.althera.common.system;

import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRarity;
import com.darksune.althera.common.hero.HeroRegistry;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HeroRollSystem {

    public static HeroDefinition rollHero() {

        // 1. Roll rarity (weighted)
        HeroRarity rarity = rollRarity();

        // 2. Get pool ONLY by rarity (no class bias)
        List<HeroDefinition> pool = HeroRegistry.getAll().stream()
                .filter(h -> h.getRarity() == rarity)
                .collect(Collectors.toList());

        // 3. Fallback safety
        if (pool.isEmpty()) {
            pool = List.copyOf(HeroRegistry.getAll());
        }

        // 4. Pick random hero
        int index = ThreadLocalRandom.current().nextInt(pool.size());
        return pool.get(index);
    }

    private static HeroRarity rollRarity() {
        float roll = ThreadLocalRandom.current().nextFloat() * 100f;

        float cumulative = 0f;

        for (HeroRarity rarity : HeroRarity.values()) {
            cumulative += rarity.getDropChance();

            if (roll <= cumulative) {
                return rarity;
            }
        }

        return HeroRarity.F; // fallback (should never happen)
    }
}