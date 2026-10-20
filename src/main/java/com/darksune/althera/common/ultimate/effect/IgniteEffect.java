package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;

public record IgniteEffect(UltimateTarget target, EntityFilter filter, int ticks) implements UltimateEffect {

    public static final MapCodec<IgniteEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UltimateTarget.CODEC.fieldOf("target").forGetter(IgniteEffect::target),
            EntityFilter.CODEC.optionalFieldOf("filter", EntityFilter.ANY).forGetter(IgniteEffect::filter),
            ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter(IgniteEffect::ticks)
    ).apply(instance, IgniteEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.IGNITE;
    }

    @Override
    public void apply(UltimateContext context) {
        for (LivingEntity entity : filter.resolve(target, context)) {
            entity.setRemainingFireTicks(ticks);
        }
    }
}
