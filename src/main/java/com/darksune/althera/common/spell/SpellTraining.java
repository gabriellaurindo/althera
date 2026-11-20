package com.darksune.althera.common.spell;

public final class SpellTraining {

    public static final int MIN_CASTS = 3;

    public static final int TRAINING_FACTOR = 100;

    public static final int MANA_MULTIPLIER = 2;

    private SpellTraining() {
    }

    public static int requiredCasts(final int manaCost, final int tier, final int maxMana) {
        final long work = (long) manaCost * TRAINING_FACTOR * tier;
        final long mana = Math.max(1, maxMana);
        return (int) Math.max(MIN_CASTS, (work + mana - 1) / mana);
    }

    public static int trainingCost(final int manaCost) {
        return manaCost * MANA_MULTIPLIER;
    }
}
