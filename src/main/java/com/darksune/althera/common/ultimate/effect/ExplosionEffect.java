package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.Level;

public record ExplosionEffect(float power) implements UltimateEffect {

    public static final MapCodec<ExplosionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("power", 0.0F).forGetter(ExplosionEffect::power)
    ).apply(instance, ExplosionEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.EXPLOSION;
    }

    @Override
    public void apply(UltimateContext context) {
        context.level().explode(
                context.hero(),
                context.hero().getX(),
                context.hero().getY(),
                context.hero().getZ(),
                power,
                Level.ExplosionInteraction.NONE
        );
    }
}
