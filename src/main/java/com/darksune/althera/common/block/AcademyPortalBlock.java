package com.darksune.althera.common.block;

import com.darksune.althera.common.academy.AcademyPortalShape;
import com.darksune.althera.common.academy.AcademyTravel;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AcademyPortalBlock extends Block {

    public static final MapCodec<AcademyPortalBlock> CODEC = simpleCodec(AcademyPortalBlock::new);

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private static final VoxelShape X_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);

    private static final VoxelShape Z_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public AcademyPortalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.Z ? Z_SHAPE : X_SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return null;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {

        boolean inPlane = direction.getAxis() == Direction.Axis.Y || direction.getAxis() == state.getValue(AXIS);

        if (inPlane && !neighborState.is(this) && !AcademyPortalShape.isFrame(neighborState)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ServerPlayer player) {
            AcademyTravel.tickInPortal(player);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {

        if (random.nextInt(100) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.5F, 0.8F + random.nextFloat() * 0.4F, false);
        }

        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(random.nextInt(3) == 0 ? ParticleTypes.END_ROD : ParticleTypes.REVERSE_PORTAL,
                    x, y, z, 0.0D, 0.02D, 0.0D);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }
}
