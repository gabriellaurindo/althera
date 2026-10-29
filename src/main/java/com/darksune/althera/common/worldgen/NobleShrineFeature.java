package com.darksune.althera.common.worldgen;

import com.darksune.althera.common.block.AltheraBlocks;
import com.darksune.althera.common.block.entity.NobleSigilBlockEntity;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class NobleShrineFeature extends Feature<EnemyShrineConfiguration> {

    private static final int RADIUS = 3;
    private static final int CLEAR_HEIGHT = 5;
    private static final int PILLAR_HEIGHT = 3;
    private static final int MAX_SUPPORT_DEPTH = 8;

    public NobleShrineFeature(Codec<EnemyShrineConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<EnemyShrineConfiguration> context) {
        final WorldGenLevel level = context.level();
        final BlockPos center = context.origin().below();
        if (!isValidGround(level, center)) return false;

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                final BlockPos floor = center.offset(dx, 0, dz);
                place(level, floor, Blocks.QUARTZ_BRICKS.defaultBlockState());
                placeSupport(level, floor.below());
                for (int dy = 1; dy <= CLEAR_HEIGHT; dy++) {
                    place(level, floor.above(dy), Blocks.AIR.defaultBlockState());
                }
            }
        }

        for (int dx = -RADIUS; dx <= RADIUS; dx += RADIUS * 2) {
            for (int dz = -RADIUS; dz <= RADIUS; dz += RADIUS * 2) {
                placePillar(level, center.offset(dx, 1, dz));
            }
        }

        place(level, center, AltheraBlocks.NOBLE_SIGIL.get().defaultBlockState());
        if (level.getBlockEntity(center) instanceof NobleSigilBlockEntity sigil) {
            sigil.setMageDefinition(context.config().mage());
        }
        return true;
    }

    private static boolean isValidGround(final WorldGenLevel level, final BlockPos center) {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                final BlockPos pos = center.offset(dx, 0, dz);
                if (!level.getFluidState(pos).isEmpty() || !level.getFluidState(pos.above()).isEmpty()) return false;
            }
        }
        final BlockState ground = level.getBlockState(center);
        return ground.isSolid() && !ground.is(BlockTags.LEAVES) && !ground.is(BlockTags.LOGS);
    }

    private static void placeSupport(final WorldGenLevel level, final BlockPos top) {
        BlockPos pos = top;
        for (int depth = 0; depth < MAX_SUPPORT_DEPTH && isReplaceable(level, pos); depth++) {
            place(level, pos, Blocks.STONE_BRICKS.defaultBlockState());
            pos = pos.below();
        }
    }

    private static boolean isReplaceable(final WorldGenLevel level, final BlockPos pos) {
        final BlockState state = level.getBlockState(pos);
        return state.canBeReplaced() || !state.getFluidState().isEmpty();
    }

    private static void placePillar(final WorldGenLevel level, final BlockPos base) {
        for (int dy = 0; dy < PILLAR_HEIGHT; dy++) {
            place(level, base.above(dy), Blocks.QUARTZ_PILLAR.defaultBlockState());
        }
        place(level, base.above(PILLAR_HEIGHT), Blocks.GOLD_BLOCK.defaultBlockState());
        place(level, base.above(PILLAR_HEIGHT + 1), Blocks.LANTERN.defaultBlockState());
    }

    private static void place(final WorldGenLevel level, final BlockPos pos, final BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_CLIENTS);
    }
}
