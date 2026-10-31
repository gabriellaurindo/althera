package com.darksune.althera.common.block;

import com.darksune.althera.Althera;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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

    public static final DeferredHolder<Block, Block> NOBLE_SIGIL = BLOCKS.register("noble_sigil",
            () -> new NobleSigilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .sound(SoundType.STONE)
                    .strength(50.0f, 1200.0f)
                    .lightLevel(state -> 7)
                    .noLootTable()
            ));

    public static final DeferredHolder<Block, Block> RITUAL_PEDESTAL = BLOCKS.register("ritual_pedestal",
            () -> new RitualPedestalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .sound(SoundType.STONE)
                    .strength(1.5f, 6.0f)
                    .noOcclusion()
            ));

    public static void register(final IEventBus bus) {
        BLOCKS.register(bus);
    }
}
