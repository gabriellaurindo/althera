package com.darksune.althera.common.block;

import com.darksune.althera.Althera;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AltheraBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK, Althera.MOD_ID);

    public static final DeferredHolder<Block, Block> RITUAL_CORE = BLOCKS.register("ritual_core",
            () -> new RitualCoreBlock(BlockBehaviour.Properties.of()
                    .strength(1.5f)
            ));

    public static void register(final IEventBus bus) {
        BLOCKS.register(bus);
    }
}
