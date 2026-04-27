package com.darksune.althera.common.block;

import com.darksune.althera.common.multiblock.MultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class RitualCoreBlock extends Block {

    public RitualCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {

        if (!level.isClientSide) {
            if (MultiblockValidator.isValid(level, pos)) {
                player.sendSystemMessage(Component.literal("§aRitual válido!"));
            } else {
                player.sendSystemMessage(Component.literal("§cEstrutura inválida."));
            }
        }

        return InteractionResult.SUCCESS;
    }
}