package com.darksune.althera.common.hero;

public enum HeroRank {

//    F("F", 0.7f, 40f),
//    E("E", 0.8f, 25f),
//    D("D", 0.9f, 15f),
//    C("C", 1.0f, 10f),
//    B("B", 1.15f, 5f),
//    A("A", 1.3f, 3f),
//    S("S", 1.5f, 1.5f),
//    EX("EX", 1.75f, 0.5f);
    EX("EX", 1.75f, 100f); //temp

    private final String displayName;
    private final float healthMultiplier;

    // Chance in percentage (0–100)
    private final float dropChance;

    HeroRank(String displayName, float healthMultiplier, float dropChance) {
        this.displayName = displayName;
        this.healthMultiplier = healthMultiplier;
        this.dropChance = dropChance;
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