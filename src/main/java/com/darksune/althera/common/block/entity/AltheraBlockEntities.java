package com.darksune.althera.common.block.entity;

import com.darksune.althera.Althera;
import com.darksune.althera.common.block.AltheraBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AltheraBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Althera.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NobleSigilBlockEntity>> NOBLE_SIGIL =
            BLOCK_ENTITIES.register("noble_sigil",
                    () -> BlockEntityType.Builder
                            .of(NobleSigilBlockEntity::new, AltheraBlocks.NOBLE_SIGIL.get())
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RitualPedestalBlockEntity>> RITUAL_PEDESTAL =
            BLOCK_ENTITIES.register("ritual_pedestal",
                    () -> BlockEntityType.Builder
                            .of(RitualPedestalBlockEntity::new, AltheraBlocks.RITUAL_PEDESTAL.get())
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RitualCoreBlockEntity>> RITUAL_CORE =
            BLOCK_ENTITIES.register("ritual_core",
                    () -> BlockEntityType.Builder
                            .of(RitualCoreBlockEntity::new, AltheraBlocks.RITUAL_CORE.get())
                            .build(null));

    public static void register(final IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
