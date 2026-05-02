package com.darksune.althera.common.system;

import java.util.EnumSet;

public enum HeroClass {

    SABER("Saber", 1.15f, 1.15f, 1.15f, 1.0f),
    LANCER("Lancer", 1.0f, 1.25f, 0.85f, 1.25f),
    ARCHER("Archer", 0.85f, 1.35f, 0.75f, 1.1f),
    CASTER("Caster", 0.75f, 1.5f, 0.7f, 1.0f),
    SHIELDER("Shielder", 1.5f, 0.7f, 1.6f, 0.75f);

    private final String displayName;

    private final float healthMultiplier;
    private final float attackMultiplier;
    private final float armorMultiplier;
    private final float speedMultiplier;

    private EnumSet<HeroClass> strongAgainst;
    private EnumSet<HeroClass> weakAgainst;

    HeroClass(String displayName,
              float healthMultiplier,
              float attackMultiplier,
              float armorMultiplier,
              float speedMultiplier) {

        this.displayName = displayName;
        this.healthMultiplier = healthMultiplier;
        this.attackMultiplier = attackMultiplier;
        this.armorMultiplier = armorMultiplier;
        this.speedMultiplier = speedMultiplier;
    }

    static {
        SABER.setRelations(EnumSet.of(LANCER), EnumSet.of(ARCHER));
        LANCER.setRelations(EnumSet.of(ARCHER), EnumSet.of(SABER));
        ARCHER.setRelations(EnumSet.of(SABER), EnumSet.of(LANCER));

        CASTER.setRelations(EnumSet.noneOf(HeroClass.class), EnumSet.noneOf(HeroClass.class));
        SHIELDER.setRelations(EnumSet.noneOf(HeroClass.class), EnumSet.noneOf(HeroClass.class));
    }

    private void setRelations(final EnumSet<HeroClass> strongAgainst, final EnumSet<HeroClass> weakAgainst) {
        this.strongAgainst = strongAgainst;
        this.weakAgainst = weakAgainst;
    }

    public boolean isStrongAgainst(final HeroClass other) {
        return strongAgainst.contains(other);
    }

    public boolean isWeakAgainst(final HeroClass other) {
        return weakAgainst.contains(other);
    }

    public float getMultiplierAgainst(final HeroClass other) {
        if (isStrongAgainst(other)) return 1.25f;
        if (isWeakAgainst(other)) return 0.75f;
        return 1.0f;
    }

    // =========================
    // GETTERS
    // =========================

    public String getDisplayName() {
        return displayName;
    }

    public float getHealthMultiplier() {
        return healthMultiplier;
    }

    public float getAttackMultiplier() {
        return attackMultiplier;
    }

    public float getArmorMultiplier() {
        return armorMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
}