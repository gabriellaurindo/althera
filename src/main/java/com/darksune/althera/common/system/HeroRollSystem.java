package com.darksune.althera.common.system;

import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRank;
import com.darksune.althera.common.hero.HeroRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HeroRollSystem {

    public static Optional<HeroDefinition> rollHero() {
        return rollFrom(HeroRegistry.getRollable());
    }

    public static Optional<HeroDefinition> rollHeroExcept(@Nullable ResourceLocation excludedId) {
        return rollFrom(HeroRegistry.getRollable().stream()
                .filter(hero -> !hero.getId().equals(excludedId))
                .toList());
    }

    private static Optional<HeroDefinition> rollFrom(List<HeroDefinition> rollable) {

        if (rollable.isEmpty()) {
            return Optional.empty();
        }

        // 1. Roll rank (weighted, only ranks that have rollable heroes)
        HeroRank rank = rollRank(rollable);

        // 2. Get pool ONLY by rank (no class bias)
        List<HeroDefinition> pool = rollable.stream()
                .filter(h -> h.getRank() == rank)
                .toList();

        // 3. Pick random hero
        int index = ThreadLocalRandom.current().nextInt(pool.size());
        return Optional.of(pool.get(index));
    }

    private static HeroRank rollRank(List<HeroDefinition> rollable) {

        Set<HeroRank> available = rollable.stream()
                .map(HeroDefinition::getRank)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(HeroRank.class)));

        float total = 0f;

        for (HeroRank rank : available) {
            total += rank.getDropChance();
        }

        float roll = ThreadLocalRandom.current().nextFloat() * total;

        float cumulative = 0f;

        HeroRank last = null;

        for (HeroRank rank : available) {
            cumulative += rank.getDropChance();
            last = rank;

            if (roll < cumulative) {
                return rank;
            }
        }

        return last;
    }
}
