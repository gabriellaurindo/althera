package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public record KnockbackEffect(UltimateTarget target, EntityFilter filter, double horizontal, double vertical,
                              boolean scaleWithDistance) implements UltimateEffect {

    public static final MapCodec<KnockbackEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UltimateTarget.CODEC.fieldOf("target").forGetter(KnockbackEffect::target),
            EntityFilter.CODEC.optionalFieldOf("filter", EntityFilter.ANY).forGetter(KnockbackEffect::filter),
            Codec.DOUBLE.optionalFieldOf("horizontal", 0.0).forGetter(KnockbackEffect::horizontal),
            Codec.DOUBLE.optionalFieldOf("vertical", 0.0).forGetter(KnockbackEffect::vertical),
            Codec.BOOL.optionalFieldOf("scale_with_distance", true).forGetter(KnockbackEffect::scaleWithDistance)
    ).apply(instance, KnockbackEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.KNOCKBACK;
    }

    @Override
    public void apply(UltimateContext context) {
        for (LivingEntity entity : filter.resolve(target, context)) {
            Vec3 away = new Vec3(entity.getX() - context.caster().getX(), 0, entity.getZ() - context.caster().getZ());
            if (!scaleWithDistance) {
                away = away.lengthSqr() > 1.0E-4 ? away.normalize() : context.direction();
            }
            entity.push(away.x * horizontal, vertical, away.z * horizontal);
        }
    }
}
