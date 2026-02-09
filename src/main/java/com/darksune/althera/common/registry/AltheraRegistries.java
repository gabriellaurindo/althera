package com.darksune.althera.common.registry;

import com.darksune.althera.Althera;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AltheraRegistries {

    private AltheraRegistries() {
        // util class
    }

    // =========================
    // ITEMS
    // =========================
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, Althera.MOD_ID);

    // =========================
    // ENTITIES
    // =========================
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Althera.MOD_ID);

    // =========================
    // EFFECTS (buffs/debuffs)
    // =========================
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Althera.MOD_ID);

    // =========================
    // INIT
    // =========================
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        EFFECTS.register(modEventBus);
    }
}
