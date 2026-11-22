package com.darksune.althera.common.academy;

import com.darksune.althera.common.academy.AcademyLayout.Room;
import com.darksune.althera.common.academy.AcademyLayout.RoomKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.List;

import static com.darksune.althera.common.academy.AcademyLayout.*;

public final class AcademyDecorator {

    private static final int[] DESK_ROWS = {0, -2};

    private static final int[] DESK_COLUMNS = {-3, -2, 2, 3};

    private static final int ACCENT_U = 4;

    private static final int ACCENT_V = 4;

    private static final int LAMP_STEP = 9;

    private static final int LAMP_SIDE = 2;

    private static final long SEED = 0xA1DE7AL;

    private record Tree(int x, int z, ResourceKey<ConfiguredFeature<?, ?>> feature) {
    }

    private static final List<Tree> TREES = List.of(
            new Tree(-15, -15, TreeFeatures.CHERRY), new Tree(-21, -11, TreeFeatures.BIRCH), new Tree(-11, -22, TreeFeatures.OAK),
            new Tree(15, -15, TreeFeatures.CHERRY), new Tree(22, -10, TreeFeatures.OAK), new Tree(10, -23, TreeFeatures.BIRCH),
            new Tree(-15, 15, TreeFeatures.CHERRY), new Tree(-22, 10, TreeFeatures.BIRCH), new Tree(-10, 23, TreeFeatures.OAK),
            new Tree(30, 7, TreeFeatures.CHERRY), new Tree(-30, -7, TreeFeatures.CHERRY), new Tree(7, 30, TreeFeatures.BIRCH),
            new Tree(-7, -30, TreeFeatures.OAK)
    );

    private static final List<BlockState> FLOWERS = List.of(
            Blocks.POPPY.defaultBlockState(), Blocks.DANDELION.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(),
            Blocks.ALLIUM.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(),
            Blocks.LILY_OF_THE_VALLEY.defaultBlockState()
    );

    private AcademyDecorator() {
    }

    public static void decorate(ServerLevel level) {

        for (Room room : ROOMS) {
            switch (room.kind()) {
                case HALL -> decorateHall(level, room);
                case LIBRARY -> decorateLibrary(level, room);
                case FIRE, LIGHTNING, ICE -> decorateClassroom(level, room);
            }
        }

        for (Room room : ROOMS) {
            if (!room.isHall()) {
                placeLamps(level, room);
            }
        }

        placeTrees(level);
        placeFlowers(level);
    }

    private static Direction doorDirection(Room room) {
        return room.gridX() != 0
                ? (room.gridX() > 0 ? Direction.WEST : Direction.EAST)
                : (room.gridZ() > 0 ? Direction.NORTH : Direction.SOUTH);
    }

    private static BlockPos local(Room room, int u, int v, int y) {

        Direction back = doorDirection(room).getOpposite();
        Direction side = back.getClockWise();

        return new BlockPos(room.centerX() + back.getStepX() * u + side.getStepX() * v, SURFACE_Y + y,
                room.centerZ() + back.getStepZ() * u + side.getStepZ() * v);
    }

    private static void decorateHall(ServerLevel level, Room room) {

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 || dz != 0) {
                    set(level, new BlockPos(room.centerX() + dx, SURFACE_Y + 1, room.centerZ() + dz), Blocks.PURPLE_CARPET.defaultBlockState());
                }
            }
        }

        int inner = ROOM_HALF - 1;

        for (int[] corner : new int[][]{{-inner, -inner}, {inner, inner}, {-inner, inner}}) {
            set(level, new BlockPos(room.centerX() + corner[0], SURFACE_Y + 1, room.centerZ() + corner[1]),
                    Blocks.POTTED_FLOWERING_AZALEA.defaultBlockState());
        }

        BlockState bench = Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);

        for (int dx : new int[]{-4, -3, 3, 4}) {
            set(level, new BlockPos(room.centerX() + dx, SURFACE_Y + 1, room.centerZ() + inner), bench);
        }
    }

    private static void decorateLibrary(ServerLevel level, Room room) {

        BlockState table = Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);

        for (int v : new int[]{-3, 3}) {
            set(level, local(room, -2, v, 1), table);
            set(level, local(room, -2, v, 2), candle(Blocks.WHITE_CANDLE));
        }
    }

    private static void decorateClassroom(ServerLevel level, Room room) {

        BlockState desk = Blocks.SPRUCE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);
        BlockState chair = Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, doorDirection(room));
        Block candle = switch (room.kind()) {
            case FIRE -> Blocks.RED_CANDLE;
            case LIGHTNING -> Blocks.YELLOW_CANDLE;
            default -> Blocks.LIGHT_BLUE_CANDLE;
        };

        for (int u : DESK_ROWS) {
            for (int v : DESK_COLUMNS) {
                set(level, local(room, u, v, 1), desk);
                set(level, local(room, u - 1, v, 1), chair);

                if (Math.abs(v) == 2) {
                    set(level, local(room, u, v, 2), candle(candle));
                }
            }
        }

        for (int v : new int[]{-ACCENT_V, ACCENT_V}) {
            switch (room.kind()) {
                case FIRE -> set(level, local(room, ACCENT_U, v, 1),
                        Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, true));
                case LIGHTNING -> {
                    set(level, local(room, ACCENT_U, v, 1), Blocks.WAXED_CUT_COPPER.defaultBlockState());
                    set(level, local(room, ACCENT_U, v, 2), Blocks.LIGHTNING_ROD.defaultBlockState());
                }
                default -> {
                    set(level, local(room, ACCENT_U, v, 1), Blocks.BLUE_ICE.defaultBlockState());
                    set(level, local(room, ACCENT_U, v, 2), Blocks.BLUE_ICE.defaultBlockState());
                }
            }
        }
    }

    private static BlockState candle(Block block) {
        return block.defaultBlockState().setValue(CandleBlock.LIT, true);
    }

    private static void placeLamps(ServerLevel level, Room room) {

        for (int side : new int[]{-LAMP_SIDE, LAMP_SIDE}) {

            int x = CENTER_X + (room.gridX() != 0 ? room.gridX() * LAMP_STEP : side);
            int z = CENTER_Z + (room.gridZ() != 0 ? room.gridZ() * LAMP_STEP : side);

            set(level, new BlockPos(x, SURFACE_Y + 1, z), Blocks.STONE_BRICK_WALL.defaultBlockState());
            set(level, new BlockPos(x, SURFACE_Y + 2, z), Blocks.STONE_BRICK_WALL.defaultBlockState());
            set(level, new BlockPos(x, SURFACE_Y + 3, z), Blocks.LANTERN.defaultBlockState());
        }
    }

    private static void placeTrees(ServerLevel level) {

        RandomSource random = RandomSource.create(SEED);
        var features = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);

        for (Tree tree : TREES) {
            BlockPos pos = new BlockPos(CENTER_X + tree.x(), SURFACE_Y + 1, CENTER_Z + tree.z());
            features.getHolder(tree.feature()).ifPresent(feature ->
                    feature.value().place(level, level.getChunkSource().getGenerator(), random, pos));
        }
    }

    private static void placeFlowers(ServerLevel level) {

        RandomSource random = RandomSource.create(SEED + 1);

        for (int dx = -ISLAND_RADIUS; dx <= ISLAND_RADIUS; dx++) {
            for (int dz = -ISLAND_RADIUS; dz <= ISLAND_RADIUS; dz++) {

                float roll = random.nextFloat();
                BlockState flower = FLOWERS.get(random.nextInt(FLOWERS.size()));

                if (roll > 0.12F || Math.sqrt(dx * dx + dz * dz) > wardRadius(Math.atan2(dz, dx)) - 1.0D) {
                    continue;
                }

                BlockPos ground = new BlockPos(CENTER_X + dx, SURFACE_Y, CENTER_Z + dz);

                if (!level.getBlockState(ground).is(Blocks.GRASS_BLOCK) || !level.getBlockState(ground.above()).isAir()) {
                    continue;
                }

                set(level, ground.above(), roll < 0.035F ? flower : Blocks.SHORT_GRASS.defaultBlockState());
            }
        }
    }

    private static void set(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_CLIENTS);
    }
}
