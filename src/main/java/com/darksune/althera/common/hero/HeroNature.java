package com.darksune.althera.common.hero;

public enum HeroNature {

    COMMON(1.0f),   // 100%
    DIVINE(10.0f);  // 1000%

    private final float globalMultiplier;

    HeroNature(float globalMultiplier) {
        this.globalMultiplier = globalMultiplier;
    }

    public float getGlobalMultiplier() {
        return globalMultiplier;
    }

    public boolean isDivine() {
        return this == DIVINE;
    }

    public boolean isCommon() {
        return this == COMMON;
    }
}