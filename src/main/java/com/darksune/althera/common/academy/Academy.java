package com.darksune.althera.common.academy;

import com.darksune.althera.Althera;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class Academy {

    public static final String NAME = "Noble Magic Academy";

    public static final ResourceKey<Level> LEVEL =
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "academy"));

    private Academy() {
    }

    public static boolean isIn(Level level) {
        return level.dimension() == LEVEL;
    }

    @Nullable
    public static ServerLevel level(MinecraftServer server) {
        return server.getLevel(LEVEL);
    }

    public static Vec3 arrival() {
        return new Vec3(AcademyLayout.CENTER_X + 0.5D, AcademyLayout.SURFACE_Y + 1, AcademyLayout.CENTER_Z + 0.5D);
    }

    public static boolean canBuild(Player player) {
        return player.isCreative() || player.isSpectator();
    }

    public static boolean sendToAcademy(ServerPlayer player) {

        ServerLevel academy = level(player.server);

        if (academy == null) {
            return false;
        }

        Vec3 arrival = arrival();
        player.teleportTo(academy, arrival.x, arrival.y, arrival.z, 180.0F, 0.0F);
        player.fallDistance = 0.0F;

        return true;
    }

    public static void sendHome(ServerPlayer player) {

        ServerLevel overworld = player.server.overworld();
        var spawn = overworld.getSharedSpawnPos();
        int y = overworld.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn.getX(), spawn.getZ());

        player.teleportTo(overworld, spawn.getX() + 0.5D, y, spawn.getZ() + 0.5D, player.getYRot(), 0.0F);
        player.fallDistance = 0.0F;
    }
}
