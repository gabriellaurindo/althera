package com.darksune.althera.common.system;

import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRank;
import com.darksune.althera.common.hero.HeroRegistry;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HeroRollSystem {

    public static HeroDefinition rollHero() {

        // 1. Roll rank (weighted)
        HeroRank rank = rollRank();

        // 2. Get pool ONLY by rank (no class bias)
        List<HeroDefinition> pool = HeroRegistry.getAll().stream()
                .filter(h -> h.getRank() == rank)
                .collect(Collectors.toList());

        // 3. Fallback safety
        if (pool.isEmpty()) {
            pool = List.copyOf(HeroRegistry.getAll());
        }

        // 4. Pick random hero
        int index = ThreadLocalRandom.current().nextInt(pool.size());
        return pool.get(index);
    }

    private static HeroRank rollRank() {
        float roll = ThreadLocalRandom.current().nextFloat() * 100f;

        float cumulative = 0f;

        for (HeroRank rank : HeroRank.values()) {
            cumulative += rank.getDropChance();

            if (roll <= cumulative) {
                return rank;
            }
        }

        return HeroRank.F; // fallback (should never happen)
    }
}