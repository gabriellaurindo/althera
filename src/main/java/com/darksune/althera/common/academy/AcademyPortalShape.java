package com.darksune.althera.common.academy;

import com.darksune.althera.Althera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record AcademyPortalShape(Direction.Axis axis, List<BlockPos> interior) {

    public static final TagKey<Block> FRAME =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "academy_portal_frame"));

    public static final int MIN_WIDTH = 2;

    public static final int MIN_HEIGHT = 3;

    public static final int MAX_SIZE = 21;

    public static boolean isFrame(BlockState state) {
        return state.is(FRAME);
    }

    public static Optional<AcademyPortalShape> find(BlockGetter level, BlockPos start) {

        if (!level.getBlockState(start).isAir()) {
            return Optional.empty();
        }

        for (Direction.Axis axis : List.of(Direction.Axis.X, Direction.Axis.Z)) {
            Optional<AcademyPortalShape> shape = fill(level, start, axis);

            if (shape.isPresent()) {
                return shape;
            }
        }

        return Optional.empty();
    }

    private static Optional<AcademyPortalShape> fill(BlockGetter level, BlockPos start, Direction.Axis axis) {

        Direction side = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        List<Direction> directions = List.of(side, side.getOpposite(), Direction.UP, Direction.DOWN);

        Set<BlockPos> interior = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        interior.add(start);
        queue.add(start);

        int minSide = 0;
        int maxSide = 0;
        int minY = start.getY();
        int maxY = start.getY();

        while (!queue.isEmpty()) {

            BlockPos current = queue.poll();

            for (Direction direction : directions) {

                BlockPos next = current.relative(direction);

                if (interior.contains(next)) {
                    continue;
                }

                BlockState state = level.getBlockState(next);

                if (isFrame(state)) {
                    continue;
                }

                if (!state.isAir()) {
                    return Optional.empty();
                }

                int offset = axis == Direction.Axis.X ? next.getX() - start.getX() : next.getZ() - start.getZ();
                minSide = Math.min(minSide, offset);
                maxSide = Math.max(maxSide, offset);
                minY = Math.min(minY, next.getY());
                maxY = Math.max(maxY, next.getY());

                if (maxSide - minSide + 1 > MAX_SIZE || maxY - minY + 1 > MAX_SIZE) {
                    return Optional.empty();
                }

                interior.add(next);
                queue.add(next);
            }
        }

        if (maxSide - minSide + 1 < MIN_WIDTH || maxY - minY + 1 < MIN_HEIGHT) {
            return Optional.empty();
        }

        return Optional.of(new AcademyPortalShape(axis, List.copyOf(interior)));
    }
}
