package com.darksune.althera.common.registry;

import com.darksune.althera.Althera;
import com.darksune.althera.common.item.SummonSealItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.darksune.althera.Althera.MOD_ID;


public final class AltheraItemRegistries {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<SummonSealItem> SUMMON_SEAL = (DeferredItem<SummonSealItem>) register("summon_seal", SummonSealItem::create);

    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }

    private static DeferredItem<? extends Item> register(final String name, final Supplier<Item> supplier) {
        return ITEMS.register(Identifier.fromNamespaceAndPath(MOD_ID, name), supplier);
    }

}

