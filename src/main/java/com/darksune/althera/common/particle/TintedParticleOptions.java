package com.darksune.althera.common.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public record TintedParticleOptions(TintedParticleType type, int color, float scale, int lifetime) implements ParticleOptions {

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public float red() {
        return (color >> 16 & 0xFF) / 255.0F;
    }

    public float green() {
        return (color >> 8 & 0xFF) / 255.0F;
    }

    public float blue() {
        return (color & 0xFF) / 255.0F;
    }
}
