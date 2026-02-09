package com.darksune.althera.common.registry;

import com.darksune.althera.Althera;
import com.darksune.althera.common.item.ItemBase;
import com.darksune.althera.common.item.SummonSeal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;


public final class AltheraItemRegistries {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Althera.MOD_ID);

    private static final List<ItemBase> DEFINITIONS = List.of(
            new SummonSeal()
    );

    public static void register(IEventBus bus) {
        DEFINITIONS.forEach(def -> def.register(ITEMS));
        ITEMS.register(bus);
    }
}

