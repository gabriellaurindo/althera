package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;

public record ParticlesEffect(ParticleOptions particle, int count, Vec3 spread, double speed,
                              double height) implements UltimateEffect {

    public static final MapCodec<ParticlesEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(ParticlesEffect::particle),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ParticlesEffect::count),
            Vec3.CODEC.optionalFieldOf("spread", Vec3.ZERO).forGetter(ParticlesEffect::spread),
            Codec.DOUBLE.optionalFieldOf("speed", 0.0).forGetter(ParticlesEffect::speed),
            Codec.DOUBLE.optionalFieldOf("height", 0.0).forGetter(ParticlesEffect::height)
    ).apply(instance, ParticlesEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.PARTICLES;
    }

    @Override
    public void apply(UltimateContext context) {
        context.level().sendParticles(
                particle,
                context.hero().getX(),
                context.hero().getY() + height,
                context.hero().getZ(),
                count,
                spread.x,
                spread.y,
                spread.z,
                speed
        );
    }
}
