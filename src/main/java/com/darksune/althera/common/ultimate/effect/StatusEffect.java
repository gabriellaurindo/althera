package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public record StatusEffect(UltimateTarget target, Holder<MobEffect> effect, int duration, int amplifier,
                           boolean showParticles) implements UltimateEffect {

    public static final MapCodec<StatusEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UltimateTarget.CODEC.fieldOf("target").forGetter(StatusEffect::target),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(StatusEffect::effect),
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(StatusEffect::duration),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("amplifier", 0).forGetter(StatusEffect::amplifier),
            Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(StatusEffect::showParticles)
    ).apply(instance, StatusEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.MOB_EFFECT;
    }

    @Override
    public void apply(UltimateContext context) {
        for (LivingEntity entity : target.resolve(context)) {
            entity.addEffect(new MobEffectInstance(effect, duration, amplifier, false, showParticles, true));
        }
    }
}
