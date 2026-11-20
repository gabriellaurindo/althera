package com.darksune.althera.common.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ProficiencyRates(float damagePerLevel, float sizePerLevel, float visualPerLevel, float costPerLevel) {

    public static final ProficiencyRates DEFAULT = new ProficiencyRates(0.10F, 0.05F, 0.10F, 0.03F);

    public static final Codec<ProficiencyRates> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0.0F, 10.0F).optionalFieldOf("damage_per_level", DEFAULT.damagePerLevel).forGetter(ProficiencyRates::damagePerLevel),
            Codec.floatRange(0.0F, 10.0F).optionalFieldOf("size_per_level", DEFAULT.sizePerLevel).forGetter(ProficiencyRates::sizePerLevel),
            Codec.floatRange(0.0F, 10.0F).optionalFieldOf("visual_per_level", DEFAULT.visualPerLevel).forGetter(ProficiencyRates::visualPerLevel),
            Codec.floatRange(0.0F, 0.1F).optionalFieldOf("cost_per_level", DEFAULT.costPerLevel).forGetter(ProficiencyRates::costPerLevel)
    ).apply(instance, ProficiencyRates::new));

    public float damage(final int level) {
        return grow(damagePerLevel, level);
    }

    public float size(final int level) {
        return grow(sizePerLevel, level);
    }

    public float visual(final int level) {
        return grow(visualPerLevel, level);
    }

    public int cost(final int manaCost, final int level) {
        final float multiplier = 1.0F - costPerLevel * gained(level);
        return Math.max(1, (int) Math.ceil(manaCost * multiplier - 1.0E-4F));
    }

    private static float grow(final float perLevel, final int level) {
        return 1.0F + perLevel * gained(level);
    }

    private static int gained(final int level) {
        return Math.max(0, level - SpellProficiency.MIN_LEVEL);
    }
}
