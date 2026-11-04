package com.darksune.althera.common.item.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.darksune.althera.Althera.MOD_ID;

public final class AltheraDataComponents {

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HeroCatalyst>> HERO_CATALYST =
            COMPONENTS.registerComponentType("hero_catalyst", builder -> builder
                    .persistent(HeroCatalyst.CODEC)
                    .networkSynchronized(HeroCatalyst.STREAM_CODEC));

    private AltheraDataComponents() {
    }

    public static void register(final IEventBus bus) {
        COMPONENTS.register(bus);
    }
}
