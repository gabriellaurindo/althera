package com.darksune.althera.common.ultimate.effect;

import com.darksune.althera.common.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;

public record HealEffect(UltimateTarget target, EntityFilter filter, float amount,
                         float maxHealthMultiplier) implements UltimateEffect {

    public static final MapCodec<HealEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UltimateTarget.CODEC.fieldOf("target").forGetter(HealEffect::target),
            EntityFilter.CODEC.optionalFieldOf("filter", EntityFilter.ANY).forGetter(HealEffect::filter),
            Codec.FLOAT.optionalFieldOf("amount", 0.0F).forGetter(HealEffect::amount),
            CodecUtil.optionalFloatWithLegacyName("max_health_multiplier", "max_health_ratio", 0.0F)
                    .forGetter(HealEffect::maxHealthMultiplier)
    ).apply(instance, HealEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.HEAL;
    }

    @Override
    public void apply(UltimateContext context) {
        for (LivingEntity entity : filter.resolve(target, context)) {
            entity.heal(amount + maxHealthMultiplier * entity.getMaxHealth());
        }
    }
}
