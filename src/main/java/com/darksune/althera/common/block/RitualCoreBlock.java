package com.darksune.althera.common.block;

import com.darksune.althera.common.multiblock.MultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RitualCoreBlock extends Block {

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2, 0, 2, 14, 2, 14),  // base
            Block.box(3, 2, 3, 13, 4, 13),  // topo
            Block.box(7, 4, 7, 9, 6, 9)     // núcleo
    );

    public RitualCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {

        if (!level.isClientSide) {
            if (MultiblockValidator.isValid(level, pos)) {
                player.sendSystemMessage(Component.literal("§aRitual válido!"));

                // som
                level.playSound(null, pos,
                        net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        1.0F, 1.0F
                );

                // partículas
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.ENCHANT,
                            pos.getX() + 0.5,
                            pos.getY() + 1.0,
                            pos.getZ() + 0.5,
                            40,          // quantidade
                            0.5, 0.5, 0.5, // espalhamento
                            0.1           // velocidade
                    );
                }

            } else {
                player.sendSystemMessage(Component.literal("§cEstrutura inválida."));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
