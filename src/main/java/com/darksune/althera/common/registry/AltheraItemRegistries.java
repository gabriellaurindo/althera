package com.darksune.althera.common.registry;

import com.darksune.althera.Althera;
import com.darksune.althera.common.item.definition.SummonSealDefinition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;


public final class AltheraItemRegistries {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Althera.MOD_ID);

    public static void register(IEventBus bus) {
        registerItems();
        ITEMS.register(bus);
    }

    private static void registerItems() {
        new SummonSealDefinition().register(ITEMS);
    }

}

