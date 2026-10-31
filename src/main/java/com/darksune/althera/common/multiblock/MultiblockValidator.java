package com.darksune.althera.common.multiblock;

import com.darksune.althera.common.block.AltheraBlocks;
import com.darksune.althera.common.block.entity.RitualPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class MultiblockValidator {

    private static final char PEDESTAL = 'P';
    private static final char CLEAR = '.';

    private static final String[] LAYOUT = {
            "..P..",
            ".....",
            "P.C.P",
            ".....",
            "..P.."
    };

    private static final int HALF = LAYOUT.length / 2;

    public static final List<BlockPos> PEDESTAL_OFFSETS = offsetsOf(PEDESTAL);

    private static final List<BlockPos> CLEAR_OFFSETS = offsetsOf(CLEAR);

    public record CircleState(boolean clear, int pedestalMask) {

        public static final CircleState EMPTY = new CircleState(false, 0);

        private static final int ALL_PEDESTALS = (1 << PEDESTAL_OFFSETS.size()) - 1;

        public boolean hasPedestal(final int index) {
            return (pedestalMask & (1 << index)) != 0;
        }

        public boolean isComplete() {
            return clear && pedestalMask == ALL_PEDESTALS;
        }
    }

    private static List<BlockPos> offsetsOf(final char symbol) {
        final List<BlockPos> offsets = new ArrayList<>();
        for (int z = 0; z < LAYOUT.length; z++) {
            for (int x = 0; x < LAYOUT.length; x++) {
                if (LAYOUT[z].charAt(x) == symbol) {
                    offsets.add(new BlockPos(x - HALF, 0, z - HALF));
                }
            }
        }
        return List.copyOf(offsets);
    }

    public static CircleState inspect(final Level level, final BlockPos center) {
        final boolean clear = CLEAR_OFFSETS.stream().allMatch(offset -> level.isEmptyBlock(center.offset(offset)));

        int pedestalMask = 0;
        for (int index = 0; index < PEDESTAL_OFFSETS.size(); index++) {
            if (level.getBlockState(center.offset(PEDESTAL_OFFSETS.get(index))).is(AltheraBlocks.RITUAL_PEDESTAL.get())) {
                pedestalMask |= 1 << index;
            }
        }

        return new CircleState(clear, pedestalMask);
    }

    public static boolean isValid(final Level level, final BlockPos center) {
        return inspect(level, center).isComplete();
    }

    public static List<RitualPedestalBlockEntity> findPedestals(final Level level, final BlockPos center) {
        final List<RitualPedestalBlockEntity> pedestals = new ArrayList<>();
        for (final BlockPos offset : PEDESTAL_OFFSETS) {
            if (level.getBlockEntity(center.offset(offset)) instanceof RitualPedestalBlockEntity pedestal) {
                pedestals.add(pedestal);
            }
        }
        return pedestals;
    }
}
