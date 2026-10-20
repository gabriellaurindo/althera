package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;

public interface UltimateEffect {

    Codec<UltimateEffect> CODEC = UltimateEffectType.CODEC.dispatch("type", UltimateEffect::type, UltimateEffectType::codec);

    UltimateEffectType type();

    void apply(UltimateContext context);
}
