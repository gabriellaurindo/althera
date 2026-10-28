package com.darksune.althera.common.item;

import com.darksune.althera.common.block.AltheraBlocks;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.item.block.RitualCoreItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.darksune.althera.Althera.MOD_ID;


public final class AltheraItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<SummonSealItem> SUMMON_SEAL = register(AltheraItemId.SUMMON_SEAL, SummonSealItem::create);

    public static final DeferredItem<DeferredSpawnEggItem> ENEMY_MAGE_SPAWN_EGG = register(AltheraItemId.ENEMY_MAGE_SPAWN_EGG,
            () -> new DeferredSpawnEggItem(AltheraEntities.ENEMY_MAGE, 0xF2EEE4, 0xD4AF37, new Item.Properties()));

    public static final DeferredItem<Item> EMBLEM = register(AltheraItemId.EMBLEM, () -> new Item(new Item.Properties()));

    // Block items
    public static final DeferredHolder<Item, Item> RITUAL_CORE = register(AltheraItemId.RITUAL_CORE, RitualCoreItem::create);
    public static final DeferredItem<BlockItem> NOBLE_SIGIL = register(AltheraItemId.NOBLE_SIGIL,
            () -> new BlockItem(AltheraBlocks.NOBLE_SIGIL.get(), new Item.Properties()));

    private static <T extends Item> DeferredItem<T> register(ResourceLocation id, Supplier<T> supplier) {
        return ITEMS.register(id.getPath(), supplier);
    }

    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }

}

