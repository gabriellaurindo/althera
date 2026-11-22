package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public record AuraEffect(UltimateTarget target, EntityFilter filter, ParticleOptions particle, int duration, int count,
                         double radius, double height, double bob, int interval, double turn,
                         Optional<Holder<MobEffect>> whileEffect, List<UltimateEffect> onBreak) implements UltimateEffect {

    public static final MapCodec<AuraEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UltimateTarget.CODEC.optionalFieldOf("target", new UltimateTarget.Self()).forGetter(AuraEffect::target),
            EntityFilter.CODEC.optionalFieldOf("filter", EntityFilter.ANY).forGetter(AuraEffect::filter),
            ParticleTypes.CODEC.fieldOf("particle").forGetter(AuraEffect::particle),
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(AuraEffect::duration),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 3).forGetter(AuraEffect::count),
            Codec.DOUBLE.optionalFieldOf("radius", 0.8).forGetter(AuraEffect::radius),
            Codec.DOUBLE.optionalFieldOf("height", 1.0).forGetter(AuraEffect::height),
            Codec.DOUBLE.optionalFieldOf("bob", 0.3).forGetter(AuraEffect::bob),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("interval", 2).forGetter(AuraEffect::interval),
            Codec.DOUBLE.optionalFieldOf("turn", 0.15).forGetter(AuraEffect::turn),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().optionalFieldOf("while_effect").forGetter(AuraEffect::whileEffect),
            Codec.lazyInitialized(() -> UltimateEffect.CODEC.listOf()).optionalFieldOf("on_break", List.of())
                    .forGetter(AuraEffect::onBreak)
    ).apply(instance, AuraEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.AURA;
    }

    @Override
    public void apply(UltimateContext context) {
        for (LivingEntity entity : filter.resolve(target, context)) {
            AuraSystem.start(entity, this, context.scale().size(radius), context.scale().count(count), context);
        }
    }
}
