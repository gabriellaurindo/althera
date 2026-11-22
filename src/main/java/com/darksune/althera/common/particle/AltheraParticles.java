package com.darksune.althera.common.particle;

import com.darksune.althera.Althera;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AltheraParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Althera.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, TintedParticleType> GLOW =
            PARTICLES.register("glow", () -> new TintedParticleType(0xFFFFFF, 1.0F, 20));

    public static final DeferredHolder<ParticleType<?>, TintedParticleType> EMBER =
            PARTICLES.register("ember", () -> new TintedParticleType(0xFFB040, 1.0F, 24));

    public static final DeferredHolder<ParticleType<?>, TintedParticleType> FROST =
            PARTICLES.register("frost", () -> new TintedParticleType(0xCCEEFF, 1.0F, 30));

    public static final DeferredHolder<ParticleType<?>, TintedParticleType> SPARK =
            PARTICLES.register("spark", () -> new TintedParticleType(0xFFF27A, 1.0F, 8));

    public static final DeferredHolder<ParticleType<?>, TintedParticleType> RING =
            PARTICLES.register("ring", () -> new TintedParticleType(0xFFFFFF, 2.0F, 12));

    public static final DeferredHolder<ParticleType<?>, TintedParticleType> RUNE =
            PARTICLES.register("rune", () -> new TintedParticleType(0xC890FF, 1.5F, 30));

    private AltheraParticles() {
    }

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }
}
