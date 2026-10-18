package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;

public record KnockbackEffect(UltimateTarget target, double horizontal, double vertical) implements UltimateEffect {

    public static final MapCodec<KnockbackEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UltimateTarget.CODEC.fieldOf("target").forGetter(KnockbackEffect::target),
            Codec.DOUBLE.optionalFieldOf("horizontal", 0.0).forGetter(KnockbackEffect::horizontal),
            Codec.DOUBLE.optionalFieldOf("vertical", 0.0).forGetter(KnockbackEffect::vertical)
    ).apply(instance, KnockbackEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.KNOCKBACK;
    }

    @Override
    public void apply(UltimateContext context) {
        for (LivingEntity entity : target.resolve(context)) {
            double dx = entity.getX() - context.hero().getX();
            double dz = entity.getZ() - context.hero().getZ();
            entity.push(dx * horizontal, vertical, dz * horizontal);
        }
    }
}
