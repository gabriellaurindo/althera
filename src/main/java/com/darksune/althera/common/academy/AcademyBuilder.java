package com.darksune.althera.common.academy;

import com.darksune.althera.common.academy.AcademyLayout.Room;
import com.darksune.althera.common.academy.AcademyLayout.RoomKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static com.darksune.althera.common.academy.AcademyLayout.*;

public final class AcademyBuilder {

    private static final int[][] ROOF_LIGHTS = {{-3, -3}, {3, -3}, {-3, 3}, {3, 3}};

    private static final int[][] YARD_TARGETS = {{-3, -3}, {3, -3}, {-3, 3}, {3, 3}};

    private AcademyBuilder() {
    }

    public static void ensureBuilt(ServerLevel level) {

        AcademySavedData data = AcademySavedData.get(level);

        if (data.getBuiltVersion() >= LAYOUT_VERSION) {
            return;
        }

        build(level);
        data.setBuiltVersion(LAYOUT_VERSION);
    }

    public static void build(ServerLevel level) {

        buildIsland(level);

        for (Room room : ROOMS) {
            buildRoom(level, room);

            if (!room.isHall()) {
                buildPath(level, room);
            }
        }

        buildYard(level, CENTER_X + YARD_GRID_X * ROOM_SPACING, CENTER_Z + YARD_GRID_Z * ROOM_SPACING);
        buildReturnPortal(level);
        AcademyDecorator.decorate(level);
    }

    private static void buildReturnPortal(ServerLevel level) {

        int z = CENTER_Z + RETURN_PORTAL_Z;
        List<BlockPos> interior = new ArrayList<>();

        for (int x = RETURN_PORTAL_MIN_X; x <= RETURN_PORTAL_MAX_X; x++) {
            for (int y = 1; y <= RETURN_PORTAL_HEIGHT; y++) {

                boolean frame = x == RETURN_PORTAL_MIN_X || x == RETURN_PORTAL_MAX_X || y == 1 || y == RETURN_PORTAL_HEIGHT;
                BlockPos pos = new BlockPos(CENTER_X + x, SURFACE_Y + y, z);

                if (frame) {
                    set(level, pos.getX(), pos.getY(), pos.getZ(), Blocks.AMETHYST_BLOCK.defaultBlockState());
                } else {
                    set(level, pos.getX(), pos.getY(), pos.getZ(), Blocks.AIR.defaultBlockState());
                    interior.add(pos);
                }
            }
        }

        AcademyTravel.open(level, new AcademyPortalShape(Direction.Axis.X, interior));
    }

    private static void buildIsland(ServerLevel level) {

        for (int dx = -ISLAND_RADIUS - 4; dx <= ISLAND_RADIUS + 4; dx++) {
            for (int dz = -ISLAND_RADIUS - 4; dz <= ISLAND_RADIUS + 4; dz++) {

                double angle = Math.atan2(dz, dx);
                double edge = islandEdge(angle);
                double distance = Math.sqrt(dx * dx + dz * dz);

                if (distance > edge) {
                    continue;
                }

                double t = distance / edge;
                int depth = Math.max(1, (int) Math.round(ISLAND_DEPTH * Math.pow(1.0D - t * t, 0.8D) + jitter(dx, dz)));

                for (int down = 0; down < depth; down++) {
                    BlockState state = down == 0 ? Blocks.GRASS_BLOCK.defaultBlockState()
                            : down <= 3 ? Blocks.DIRT.defaultBlockState()
                            : Blocks.STONE.defaultBlockState();
                    set(level, CENTER_X + dx, SURFACE_Y - down, CENTER_Z + dz, state);
                }
            }
        }
    }

    private static double jitter(int x, int z) {
        long hash = x * 3129871L ^ z * 116129781L;
        hash = hash * hash * 42317861L + hash * 11L;
        return ((hash >> 16) & 15L) / 15.0D * 3.0D - 1.5D;
    }

    private static void buildRoom(ServerLevel level, Room room) {

        int cx = room.centerX();
        int cz = room.centerZ();
        int roofY = SURFACE_Y + WALL_HEIGHT + 1;

        for (int dx = -ROOM_HALF; dx <= ROOM_HALF; dx++) {
            for (int dz = -ROOM_HALF; dz <= ROOM_HALF; dz++) {

                boolean wall = Math.abs(dx) == ROOM_HALF || Math.abs(dz) == ROOM_HALF;
                boolean corner = Math.abs(dx) == ROOM_HALF && Math.abs(dz) == ROOM_HALF;

                set(level, cx + dx, SURFACE_Y, cz + dz, floor(room.kind(), dx, dz));

                for (int y = 1; y <= WALL_HEIGHT; y++) {
                    BlockState state;

                    if (!wall) {
                        state = Blocks.AIR.defaultBlockState();
                    } else if (isDoor(room, dx, dz, y)) {
                        state = Blocks.AIR.defaultBlockState();
                    } else if (corner) {
                        state = Blocks.POLISHED_ANDESITE.defaultBlockState();
                    } else if ((y == 2 || y == 3) && Math.floorMod(Math.abs(dx) == ROOM_HALF ? dz : dx, 3) == 0) {
                        state = Blocks.GLASS.defaultBlockState();
                    } else {
                        state = Blocks.STONE_BRICKS.defaultBlockState();
                    }

                    set(level, cx + dx, SURFACE_Y + y, cz + dz, state);
                }

                set(level, cx + dx, roofY, cz + dz, wall ? Blocks.STONE_BRICKS.defaultBlockState()
                        : Blocks.STONE_BRICK_SLAB.defaultBlockState());
            }
        }

        for (int[] light : ROOF_LIGHTS) {
            set(level, cx + light[0], roofY, cz + light[1], Blocks.SEA_LANTERN.defaultBlockState());
        }

        if (room.kind() == RoomKind.LIBRARY) {
            furnishLibrary(level, room);
        }
    }

    private static boolean isDoor(Room room, int dx, int dz, int y) {

        if (y > DOOR_HEIGHT) {
            return false;
        }

        if (room.isHall()) {
            return Math.abs(dx) == ROOM_HALF && Math.abs(dz) <= DOOR_HALF_WIDTH
                    || Math.abs(dz) == ROOM_HALF && Math.abs(dx) <= DOOR_HALF_WIDTH;
        }

        if (room.gridX() != 0) {
            return dx == -Integer.signum(room.gridX()) * ROOM_HALF && Math.abs(dz) <= DOOR_HALF_WIDTH;
        }

        return dz == -Integer.signum(room.gridZ()) * ROOM_HALF && Math.abs(dx) <= DOOR_HALF_WIDTH;
    }

    private static BlockState floor(RoomKind kind, int dx, int dz) {

        boolean centre = Math.abs(dx) <= 1 && Math.abs(dz) <= 1;
        boolean border = Math.abs(dx) == ROOM_HALF - 1 || Math.abs(dz) == ROOM_HALF - 1;

        return switch (kind) {
            case HALL -> dx == 0 && dz == 0 ? Blocks.CHISELED_STONE_BRICKS.defaultBlockState()
                    : border ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                    : Blocks.POLISHED_DEEPSLATE.defaultBlockState();
            case LIBRARY -> Blocks.OAK_PLANKS.defaultBlockState();
            case FIRE -> centre ? Blocks.RED_GLAZED_TERRACOTTA.defaultBlockState()
                    : border ? Blocks.RED_TERRACOTTA.defaultBlockState()
                    : Blocks.POLISHED_ANDESITE.defaultBlockState();
            case LIGHTNING -> centre ? Blocks.YELLOW_GLAZED_TERRACOTTA.defaultBlockState()
                    : border ? Blocks.YELLOW_TERRACOTTA.defaultBlockState()
                    : Blocks.POLISHED_ANDESITE.defaultBlockState();
            case ICE -> centre ? Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.defaultBlockState()
                    : border ? Blocks.LIGHT_BLUE_TERRACOTTA.defaultBlockState()
                    : Blocks.POLISHED_ANDESITE.defaultBlockState();
        };
    }

    private static void furnishLibrary(ServerLevel level, Room room) {

        int inner = ROOM_HALF - 1;

        for (int dx = -inner; dx <= inner; dx++) {
            for (int dz = -inner; dz <= inner; dz++) {

                boolean alongWall = Math.abs(dx) == inner || Math.abs(dz) == inner;
                boolean facingDoor = dz == -Integer.signum(room.gridZ()) * inner && Math.abs(dx) <= DOOR_HALF_WIDTH;

                if (!alongWall || facingDoor) {
                    continue;
                }

                for (int y = 1; y <= 3; y++) {
                    set(level, room.centerX() + dx, SURFACE_Y + y, room.centerZ() + dz, Blocks.BOOKSHELF.defaultBlockState());
                }
            }
        }

        set(level, room.centerX(), SURFACE_Y + 1, room.centerZ(), Blocks.LECTERN.defaultBlockState());
    }

    private static void buildPath(ServerLevel level, Room room) {

        int from = ROOM_HALF + 1;
        int to = ROOM_SPACING - ROOM_HALF - 1;

        for (int step = from; step <= to; step++) {
            for (int side = -DOOR_HALF_WIDTH; side <= DOOR_HALF_WIDTH; side++) {
                int x = CENTER_X + (room.gridX() != 0 ? room.gridX() * step : side);
                int z = CENTER_Z + (room.gridZ() != 0 ? room.gridZ() * step : side);
                set(level, x, SURFACE_Y, z, Blocks.DIRT_PATH.defaultBlockState());
            }
        }
    }

    private static void buildYard(ServerLevel level, int cx, int cz) {

        for (int dx = -YARD_HALF; dx <= YARD_HALF; dx++) {
            for (int dz = -YARD_HALF; dz <= YARD_HALF; dz++) {
                boolean border = Math.abs(dx) == YARD_HALF || Math.abs(dz) == YARD_HALF;
                set(level, cx + dx, SURFACE_Y, cz + dz, border ? Blocks.STONE_BRICKS.defaultBlockState()
                        : Blocks.SMOOTH_STONE.defaultBlockState());
            }
        }

        for (int[] target : YARD_TARGETS) {
            set(level, cx + target[0], SURFACE_Y + 1, cz + target[1], Blocks.HAY_BLOCK.defaultBlockState());
            set(level, cx + target[0], SURFACE_Y + 2, cz + target[1], Blocks.TARGET.defaultBlockState());
        }
    }

    private static void set(ServerLevel level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, Block.UPDATE_CLIENTS);
    }
}
