package com.darksune.althera.common.ultimate.effect;

import com.darksune.althera.common.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public record DamageEffect(UltimateTarget target, EntityFilter filter, float amount,
                           float attackMultiplier) implements UltimateEffect {

    public static final MapCodec<DamageEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UltimateTarget.CODEC.fieldOf("target").forGetter(DamageEffect::target),
            EntityFilter.CODEC.optionalFieldOf("filter", EntityFilter.ANY).forGetter(DamageEffect::filter),
            Codec.FLOAT.optionalFieldOf("amount", 0.0F).forGetter(DamageEffect::amount),
            CodecUtil.optionalFloatWithLegacyName("attack_multiplier", "attack_ratio", 0.0F)
                    .forGetter(DamageEffect::attackMultiplier)
    ).apply(instance, DamageEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.DAMAGE;
    }

    @Override
    public void apply(UltimateContext context) {
        float damage = amount + attackMultiplier * (float) context.caster().getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (LivingEntity entity : filter.resolve(target, context)) {
            if (entity == context.target()) {
                entity.invulnerableTime = 0;
            }
            entity.hurt(context.caster().damageSources().mobAttack(context.caster()), damage);
        }
    }
}
