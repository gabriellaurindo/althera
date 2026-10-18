package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;

public record DamageEffect(UltimateTarget target, float amount) implements UltimateEffect {

    public static final MapCodec<DamageEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UltimateTarget.CODEC.fieldOf("target").forGetter(DamageEffect::target),
            Codec.FLOAT.fieldOf("amount").forGetter(DamageEffect::amount)
    ).apply(instance, DamageEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.DAMAGE;
    }

    @Override
    public void apply(UltimateContext context) {
        for (LivingEntity entity : target.resolve(context)) {
            entity.hurt(context.hero().damageSources().mobAttack(context.hero()), amount);
        }
    }
}
