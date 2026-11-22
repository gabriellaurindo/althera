package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public record ParticlesEffect(ParticleOptions particle, int count, Vec3 spread, double speed,
                              double height, double line, double directionSpeed, double circle) implements UltimateEffect {

    private static final double LINE_STEP = 0.5;

    public static final MapCodec<ParticlesEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(ParticlesEffect::particle),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ParticlesEffect::count),
            Vec3.CODEC.optionalFieldOf("spread", Vec3.ZERO).forGetter(ParticlesEffect::spread),
            Codec.DOUBLE.optionalFieldOf("speed", 0.0).forGetter(ParticlesEffect::speed),
            Codec.DOUBLE.optionalFieldOf("height", 0.0).forGetter(ParticlesEffect::height),
            Codec.DOUBLE.optionalFieldOf("line", 0.0).forGetter(ParticlesEffect::line),
            Codec.DOUBLE.optionalFieldOf("direction_speed", 0.0).forGetter(ParticlesEffect::directionSpeed),
            Codec.DOUBLE.optionalFieldOf("circle", 0.0).forGetter(ParticlesEffect::circle)
    ).apply(instance, ParticlesEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.PARTICLES;
    }

    @Override
    public void apply(UltimateContext context) {

        if (line <= 0) {
            spawn(context, context.position());
            return;
        }

        Vec3 start = context.origin();
        Vec3 end = context.reach(context.scale().size(line));
        int steps = Math.max(1, (int) Math.ceil(start.distanceTo(end) / LINE_STEP));

        for (int step = 0; step <= steps; step++) {
            spawn(context, start.lerp(end, (double) step / steps));
        }
    }

    private void spawn(UltimateContext context, Vec3 position) {

        if (directionSpeed != 0.0 || circle > 0.0) {
            spawnEach(context, position);
            return;
        }

        context.level().sendParticles(
                particle,
                position.x,
                position.y + height,
                position.z,
                context.scale().count(count),
                context.scale().size(spread.x),
                context.scale().size(spread.y),
                context.scale().size(spread.z),
                speed
        );
    }

    private void spawnEach(UltimateContext context, Vec3 position) {

        RandomSource random = context.level().getRandom();
        int total = context.scale().count(count);
        double radius = context.scale().size(circle);
        Vec3 velocity = context.direction().normalize().scale(directionSpeed);

        for (int i = 0; i < total; i++) {

            double angle = Mth.TWO_PI * i / total;
            double x = position.x + Math.cos(angle) * radius + random.nextGaussian() * context.scale().size(spread.x);
            double y = position.y + height + random.nextGaussian() * context.scale().size(spread.y);
            double z = position.z + Math.sin(angle) * radius + random.nextGaussian() * context.scale().size(spread.z);
            Vec3 jitter = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian()).scale(speed);
            Vec3 motion = velocity.add(jitter);

            context.level().sendParticles(particle, x, y, z, 0, motion.x, motion.y, motion.z, 1.0);
        }
    }
}
