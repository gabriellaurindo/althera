package com.darksune.althera.common.spell;

public final class SpellProficiency {

    public static final int MIN_LEVEL = 1;

    public static final int MAX_LEVEL = 10;

    public static final int SPELL_BASE_XP = 1500;

    public static final int ELEMENT_BASE_XP = 3000;

    public static final double GROWTH = 1.5D;

    public static final float ELEMENT_DAMAGE_PER_LEVEL = 0.05F;

    private SpellProficiency() {
    }

    public static int xpForLevel(final int level, final int baseXp) {

        if (level <= MIN_LEVEL) {
            return 0;
        }

        return (int) Math.round(baseXp * Math.pow(GROWTH, level - 2));
    }

    public static int totalXpFor(final int level, final int baseXp) {

        int total = 0;

        for (int current = MIN_LEVEL + 1; current <= Math.min(level, MAX_LEVEL); current++) {
            total += xpForLevel(current, baseXp);
        }

        return total;
    }

    public static int maxXp(final int baseXp) {
        return totalXpFor(MAX_LEVEL, baseXp);
    }

    public static int levelFor(final int xp, final int baseXp) {

        int level = MIN_LEVEL;
        int remaining = xp;

        while (level < MAX_LEVEL && remaining >= xpForLevel(level + 1, baseXp)) {
            remaining -= xpForLevel(level + 1, baseXp);
            level++;
        }

        return level;
    }

    public static float progressInLevel(final int xp, final int baseXp) {

        final int level = levelFor(xp, baseXp);

        if (level >= MAX_LEVEL) {
            return 1.0F;
        }

        final int into = xp - totalXpFor(level, baseXp);

        return (float) into / xpForLevel(level + 1, baseXp);
    }

    public static float elementDamageMultiplier(final int elementLevel) {
        return 1.0F + ELEMENT_DAMAGE_PER_LEVEL * (elementLevel - MIN_LEVEL);
    }
}
