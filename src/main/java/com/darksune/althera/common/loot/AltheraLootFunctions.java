package com.darksune.althera.common.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.darksune.althera.Althera.MOD_ID;

public final class AltheraLootFunctions {

    public static final DeferredRegister<LootItemFunctionType<?>> FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MOD_ID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<ServantCatalystFunction>> SERVANT_CATALYST =
            FUNCTIONS.register("servant_catalyst", () -> new LootItemFunctionType<>(ServantCatalystFunction.CODEC));

    private AltheraLootFunctions() {
    }

    public static void register(final IEventBus bus) {
        FUNCTIONS.register(bus);
    }
}
