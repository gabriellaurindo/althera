package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.MapCodec;

public record UltimateAnimationEffect() implements UltimateEffect {

    public static final MapCodec<UltimateAnimationEffect> CODEC = MapCodec.unit(new UltimateAnimationEffect());

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.ULTIMATE_ANIMATION;
    }

    @Override
    public void apply(UltimateContext context) {
        context.hero().triggerUltimateAnimation();
    }
}
