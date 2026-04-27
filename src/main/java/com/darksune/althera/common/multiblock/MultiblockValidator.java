package com.darksune.althera.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class MultiblockValidator {

    // Estrutura completa
    private static final Map<BlockPos, Block> PATTERN = Map.ofEntries(

        // Cantos (esmeralda)
        Map.entry(new BlockPos(-1, 0, -1), Blocks.EMERALD_BLOCK),
        Map.entry(new BlockPos(1, 0, -1), Blocks.EMERALD_BLOCK),
        Map.entry(new BlockPos(-1, 0, 1), Blocks.EMERALD_BLOCK),
        Map.entry(new BlockPos(1, 0, 1), Blocks.EMERALD_BLOCK),

        // Cruz (feno)
        Map.entry(new BlockPos(0, 0, -1), Blocks.HAY_BLOCK),
        Map.entry(new BlockPos(0, 0, 1), Blocks.HAY_BLOCK),
        Map.entry(new BlockPos(-1, 0, 0), Blocks.HAY_BLOCK),
        Map.entry(new BlockPos(1, 0, 0), Blocks.HAY_BLOCK)
    );

    public static boolean isValid(Level level, BlockPos center) {

        for (var entry : PATTERN.entrySet()) {
            BlockPos checkPos = center.offset(entry.getKey());
            Block expected = entry.getValue();

            if (!level.getBlockState(checkPos).is(expected)) {
                return false;
            }
        }

        return true;
    }

//    public static boolean isValid(Level level, BlockPos center) {
//        for (Direction dir : Direction.Plane.HORIZONTAL) {
//            if (matchesRotation(level, center, dir)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private static boolean matchesRotation(Level level, BlockPos center, Direction dir) {
//        for (var entry : PATTERN.entrySet()) {
//
//            BlockPos rotated = rotate(entry.getKey(), dir);
//            BlockPos checkPos = center.offset(rotated);
//
//            if (!level.getBlockState(checkPos).is(entry.getValue())) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private static BlockPos rotate(BlockPos pos, Direction dir) {
//        return switch (dir) {
//            case NORTH -> pos;
//            case SOUTH -> new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
//            case WEST  -> new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
//            case EAST  -> new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
//            default -> pos;
//        };
//    }
}