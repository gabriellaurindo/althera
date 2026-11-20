package com.darksune.althera.common.spell;

public record SpellScale(float damage, float size, float visual, boolean mastered) {

    public static final SpellScale NONE = new SpellScale(1.0F, 1.0F, 1.0F, false);

    public static SpellScale of(final PlayerSpell spell, final int level, final int elementLevel) {
        final ProficiencyRates rates = spell.proficiency();
        return new SpellScale(
                rates.damage(level) * SpellProficiency.elementDamageMultiplier(elementLevel),
                rates.size(level),
                rates.visual(level),
                level >= SpellProficiency.MAX_LEVEL
        );
    }

    public float damage(final float base) {
        return base * damage;
    }

    public double size(final double base) {
        return base * size;
    }

    public int count(final int base) {
        return Math.max(1, Math.round(base * visual));
    }
}
