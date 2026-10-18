package com.darksune.althera.common.hero;

import java.util.EnumSet;

public enum HeroClass {

    SABER("Saber", 1.15f, 1.15f, 1.15f, 1.0f, 0.8f, 0.05f),
    LANCER("Lancer", 1.0f, 1.25f, 0.85f, 1.25f, 0.75f, 0.05f),
    ARCHER("Archer", 0.85f, 1.35f, 0.75f, 1.1f, 0.6f, 0.05f),
    CASTER("Caster", 0.75f, 1.5f, 0.7f, 1.0f, 0.5f, 0.0f),
    SHIELDER("Shielder", 1.5f, 0.7f, 1.6f, 0.75f, 1.0f, 0.0f),
    ASSASSIN("Assassin", 0.8f, 1.4f, 0.7f, 1.3f, 0.6f, 0.1f);

    private final String displayName;

    private final float healthMultiplier;
    private final float attackMultiplier;
    private final float armorMultiplier;
    private final float speedMultiplier;
    private final float knockbackResistance;
    private final float dodgeChance;

    private EnumSet<HeroClass> strongAgainst;
    private EnumSet<HeroClass> weakAgainst;

    HeroClass(String displayName,
              float healthMultiplier,
              float attackMultiplier,
              float armorMultiplier,
              float speedMultiplier,
              float knockbackResistance,
              float dodgeChance) {

        this.displayName = displayName;
        this.healthMultiplier = healthMultiplier;
        this.attackMultiplier = attackMultiplier;
        this.armorMultiplier = armorMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.knockbackResistance = knockbackResistance;
        this.dodgeChance = dodgeChance;
    }

    static {
        SABER.setRelations(EnumSet.of(LANCER), EnumSet.of(ARCHER));
        LANCER.setRelations(EnumSet.of(ARCHER), EnumSet.of(SABER));
        ARCHER.setRelations(EnumSet.of(SABER), EnumSet.of(LANCER));

        CASTER.setRelations(EnumSet.noneOf(HeroClass.class), EnumSet.of(ASSASSIN));
        SHIELDER.setRelations(EnumSet.of(ASSASSIN), EnumSet.noneOf(HeroClass.class));
        ASSASSIN.setRelations(EnumSet.of(CASTER), EnumSet.of(SHIELDER));
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

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public float getDodgeChance() {
        return dodgeChance;
    }
}