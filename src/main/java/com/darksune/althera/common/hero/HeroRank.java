package com.darksune.althera.common.hero;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Locale;

public enum HeroRank {

    F("F", 0.7f, 40f),
    E("E", 0.8f, 25f),
    D("D", 0.9f, 15f),
    C("C", 1.0f, 10f),
    B("B", 1.15f, 5f),
    A("A", 1.3f, 3f),
    S("S", 1.5f, 1.5f),
    EX("EX", 1.75f, 0.5f);

    public static final Codec<HeroRank> CODEC = Codec.STRING.comapFlatMap(HeroRank::parse, HeroRank::name);

    private final String displayName;
    private final float healthMultiplier;

    // Chance in percentage (0–100)
    private final float dropChance;

    HeroRank(String displayName, float healthMultiplier, float dropChance) {
        this.displayName = displayName;
        this.healthMultiplier = healthMultiplier;
        this.dropChance = dropChance;
    }

    private static DataResult<HeroRank> parse(String name) {
        try {
            return DataResult.success(valueOf(name.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return DataResult.error(() -> "Unknown hero rank: " + name);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getHealthMultiplier() {
        return healthMultiplier;
    }

    public float getDropChance() {
        return dropChance;
    }
}