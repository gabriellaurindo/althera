package com.darksune.althera.common.registry;

import com.darksune.althera.common.item.AltheraItemId;
import com.darksune.althera.common.item.SummonSealItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.darksune.althera.Althera.MOD_ID;


public final class AltheraItemRegistries {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<SummonSealItem> SUMMON_SEAL = (DeferredItem<SummonSealItem>) register(AltheraItemId.SUMMON_SEAL, SummonSealItem::create);

    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }

    private static DeferredItem<? extends Item> register(final ResourceLocation id, final Supplier<Item> supplier) {
        return ITEMS.register(id.getPath(), supplier);
    }

}

