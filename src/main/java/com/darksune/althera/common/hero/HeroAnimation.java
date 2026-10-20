package com.darksune.althera.common.hero;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public record HeroAnimation(float attackSpeed, float attackLength, float walkSpeed, float deathLength) {

    public static final HeroAnimation DEFAULT = new HeroAnimation(1.2f, 0.6f, 2.0f, 2.0f);

    public static final Codec<HeroAnimation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("attack_speed", DEFAULT.attackSpeed).forGetter(HeroAnimation::attackSpeed),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("attack_length", DEFAULT.attackLength).forGetter(HeroAnimation::attackLength),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("walk_speed", DEFAULT.walkSpeed).forGetter(HeroAnimation::walkSpeed),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("death_length", DEFAULT.deathLength).forGetter(HeroAnimation::deathLength)
    ).apply(instance, HeroAnimation::new));

    public int attackTicks() {
        return Math.max(1, attackFrameTicks(attackLength));
    }

    public int attackFrameTicks(float seconds) {
        return Mth.ceil(seconds / attackSpeed * 20);
    }

    public int deathTicks() {
        return Math.max(1, Mth.ceil(deathLength * 20));
    }
}
