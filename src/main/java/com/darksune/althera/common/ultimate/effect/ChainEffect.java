package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ChainEffect(int jumps, double range, ParticleOptions particle,
                          List<UltimateEffect> onJump) implements UltimateEffect {

    private static final double ARC_STEP = 0.4;

    private static final double ARC_JITTER = 0.15;

    public static final MapCodec<ChainEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("jumps", 3).forGetter(ChainEffect::jumps),
            Codec.DOUBLE.optionalFieldOf("range", 5.0).forGetter(ChainEffect::range),
            ParticleTypes.CODEC.optionalFieldOf("particle", ParticleTypes.ELECTRIC_SPARK).forGetter(ChainEffect::particle),
            Codec.lazyInitialized(() -> UltimateEffect.CODEC.listOf()).optionalFieldOf("on_jump", List.of())
                    .forGetter(ChainEffect::onJump)
    ).apply(instance, ChainEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.CHAIN;
    }

    @Override
    public void apply(UltimateContext context) {

        final double reach = context.scale().size(range);
        final Set<LivingEntity> hit = new HashSet<>();
        Vec3 from = context.target() != null ? center(context.target()) : context.position();

        if (context.target() != null) {
            hit.add(context.target());
        }

        for (int jump = 0; jump < jumps; jump++) {

            final LivingEntity next = nearest(context, from, reach, hit);

            if (next == null) {
                return;
            }

            final Vec3 to = center(next);
            drawArc(context.level(), from, to);
            hit.add(next);

            final UltimateContext jumpContext = new UltimateContext(context.owner(), context.caster(), context.origin(),
                    context.direction(), next, next.position(), context.scale());
            onJump.forEach(effect -> effect.apply(jumpContext));

            from = to;
        }
    }

    private static LivingEntity nearest(UltimateContext context, Vec3 from, double reach, Set<LivingEntity> hit) {
        return context.level().getEntitiesOfClass(
                        LivingEntity.class,
                        AABB.ofSize(from, 0, 0, 0).inflate(reach),
                        entity -> context.canAffect(entity)
                                && !hit.contains(entity)
                                && center(entity).distanceToSqr(from) <= reach * reach)
                .stream()
                .min(Comparator.comparingDouble(entity -> center(entity).distanceToSqr(from)))
                .orElse(null);
    }

    private void drawArc(ServerLevel level, Vec3 from, Vec3 to) {

        final int steps = Math.max(1, (int) Math.ceil(from.distanceTo(to) / ARC_STEP));

        for (int step = 0; step <= steps; step++) {
            final Vec3 point = from.lerp(to, (double) step / steps);
            level.sendParticles(particle, point.x, point.y, point.z, 1, ARC_JITTER, ARC_JITTER, ARC_JITTER, 0.0);
        }
    }

    private static Vec3 center(LivingEntity entity) {
        return entity.getBoundingBox().getCenter();
    }
}
