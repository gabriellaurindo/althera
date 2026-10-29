package com.darksune.althera.common.worldgen;

import com.darksune.althera.Althera;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AltheraFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Althera.MOD_ID);

    public static final DeferredHolder<Feature<?>, NobleShrineFeature> NOBLE_SHRINE =
            FEATURES.register("noble_shrine", () -> new NobleShrineFeature(EnemyShrineConfiguration.CODEC));

    public static void register(final IEventBus bus) {
        FEATURES.register(bus);
    }
}
