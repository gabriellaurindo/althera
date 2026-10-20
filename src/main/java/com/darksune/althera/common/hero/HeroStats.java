package com.darksune.althera.common.hero;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record HeroStats(float health, float healthPerLevel, float attack, float attackPerLevel, float armor,
                        float armorPerLevel, float speed) {

    public static final HeroStats DEFAULT = new HeroStats(19.0f, 1.0f, 1.125f, 0.125f, 2.0f, 0.15f, 0.25f);

    public static final Codec<HeroStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("health", DEFAULT.health).forGetter(HeroStats::health),
            Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("health_per_level", DEFAULT.healthPerLevel).forGetter(HeroStats::healthPerLevel),
            Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("attack", DEFAULT.attack).forGetter(HeroStats::attack),
            Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("attack_per_level", DEFAULT.attackPerLevel).forGetter(HeroStats::attackPerLevel),
            Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("armor", DEFAULT.armor).forGetter(HeroStats::armor),
            Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("armor_per_level", DEFAULT.armorPerLevel).forGetter(HeroStats::armorPerLevel),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed", DEFAULT.speed).forGetter(HeroStats::speed)
    ).apply(instance, HeroStats::new));
}
