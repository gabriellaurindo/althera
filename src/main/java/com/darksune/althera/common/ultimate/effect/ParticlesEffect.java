package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;

public record ParticlesEffect(ParticleOptions particle, int count, Vec3 spread, double speed,
                              double height, double line) implements UltimateEffect {

    private static final double LINE_STEP = 0.5;

    public static final MapCodec<ParticlesEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(ParticlesEffect::particle),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ParticlesEffect::count),
            Vec3.CODEC.optionalFieldOf("spread", Vec3.ZERO).forGetter(ParticlesEffect::spread),
            Codec.DOUBLE.optionalFieldOf("speed", 0.0).forGetter(ParticlesEffect::speed),
            Codec.DOUBLE.optionalFieldOf("height", 0.0).forGetter(ParticlesEffect::height),
            Codec.DOUBLE.optionalFieldOf("line", 0.0).forGetter(ParticlesEffect::line)
    ).apply(instance, ParticlesEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.PARTICLES;
    }

    @Override
    public void apply(UltimateContext context) {

        if (line <= 0) {
            spawn(context, context.hero().position());
            return;
        }

        Vec3 start = context.origin();
        Vec3 end = context.reach(line);
        int steps = Math.max(1, (int) Math.ceil(start.distanceTo(end) / LINE_STEP));

        for (int step = 0; step <= steps; step++) {
            spawn(context, start.lerp(end, (double) step / steps));
        }
    }

    private void spawn(UltimateContext context, Vec3 position) {
        context.level().sendParticles(
                particle,
                position.x,
                position.y + height,
                position.z,
                count,
                spread.x,
                spread.y,
                spread.z,
                speed
        );
    }
}
