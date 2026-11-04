package com.darksune.althera.common.block;

import com.darksune.althera.common.block.entity.AltheraBlockEntities;
import com.darksune.althera.common.block.entity.RitualCoreBlockEntity;
import com.darksune.althera.common.system.RitualSystem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RitualCoreBlock extends BaseEntityBlock {

    public static final MapCodec<RitualCoreBlock> CODEC = simpleCodec(RitualCoreBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(1, 0, 1, 15, 2, 15),
            Block.box(2, 2, 2, 14, 4.75, 14),
            Block.box(1.25, 2, 1.25, 3.75, 4.5, 3.75),
            Block.box(12.25, 2, 1.25, 14.75, 4.5, 3.75),
            Block.box(1.25, 2, 12.25, 3.75, 4.5, 14.75),
            Block.box(12.25, 2, 12.25, 14.75, 4.5, 14.75),
            Block.box(3.5, 4.75, 3.5, 12.5, 6, 12.5)
    );

    public RitualCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RitualCoreBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, AltheraBlockEntities.RITUAL_CORE.get(), level.isClientSide
                ? RitualCoreBlockEntity::clientTick
                : RitualCoreBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof RitualCoreBlockEntity core) {
            RitualSystem.tryStart(player, level, pos, core);
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
