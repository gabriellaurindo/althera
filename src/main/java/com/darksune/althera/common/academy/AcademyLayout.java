package com.darksune.althera.common.academy;

import java.util.List;

public final class AcademyLayout {

    public static final int LAYOUT_VERSION = 3;

    public static final int CENTER_X = 0;

    public static final int CENTER_Z = 0;

    public static final int SURFACE_Y = 100;

    public static final int ISLAND_RADIUS = 40;

    public static final int ISLAND_DEPTH = 24;

    public static final double WARD_INSET = 2.0D;

    public static final int FLOOR_Y = SURFACE_Y - ISLAND_DEPTH - 16;

    public static final int ROOM_HALF = 6;

    public static final int ROOM_GAP = 4;

    public static final int ROOM_SPACING = ROOM_HALF * 2 + 1 + ROOM_GAP;

    public static final int WALL_HEIGHT = 5;

    public static final int DOOR_HALF_WIDTH = 1;

    public static final int DOOR_HEIGHT = 3;

    public static final int YARD_HALF = 5;

    public static final int RETURN_PORTAL_MIN_X = 2;

    public static final int RETURN_PORTAL_MAX_X = 5;

    public static final int RETURN_PORTAL_Z = -ROOM_HALF + 1;

    public static final int RETURN_PORTAL_HEIGHT = 5;

    public enum RoomKind {
        HALL,
        LIBRARY,
        FIRE,
        LIGHTNING,
        ICE
    }

    public record Room(RoomKind kind, int gridX, int gridZ) {

        public int centerX() {
            return CENTER_X + gridX * ROOM_SPACING;
        }

        public int centerZ() {
            return CENTER_Z + gridZ * ROOM_SPACING;
        }

        public boolean isHall() {
            return gridX == 0 && gridZ == 0;
        }
    }

    public static final List<Room> ROOMS = List.of(
            new Room(RoomKind.HALL, 0, 0),
            new Room(RoomKind.LIBRARY, 0, -1),
            new Room(RoomKind.FIRE, -1, 0),
            new Room(RoomKind.LIGHTNING, 1, 0),
            new Room(RoomKind.ICE, 0, 1)
    );

    public static final int YARD_GRID_X = 1;

    public static final int YARD_GRID_Z = 1;

    private AcademyLayout() {
    }

    public static double islandEdge(double angle) {
        return ISLAND_RADIUS + 2.5D * Math.sin(angle * 3.0D) + 1.5D * Math.cos(angle * 7.0D);
    }

    public static double wardRadius(double angle) {
        return islandEdge(angle) - WARD_INSET;
    }
}
