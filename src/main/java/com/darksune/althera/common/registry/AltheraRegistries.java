package com.darksune.althera.common.registry;

import net.neoforged.bus.api.IEventBus;

public final class AltheraRegistries {

    public static void register(final IEventBus modEventBus) {
        AltheraItemRegistries.register(modEventBus);
        AltheraEntityRegistries.register(modEventBus);
    }
}
