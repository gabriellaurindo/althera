package com.darksune.althera.client.screen;

import com.darksune.althera.network.packet.HeroStatsPacket;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public final class HeroLiveStats {

    private static final int STALE_TICKS = 40;

    @Nullable
    private static HeroStatsPacket latest;

    private static long receivedAt;

    private HeroLiveStats() {
    }

    public static void update(HeroStatsPacket stats) {
        latest = stats;
        receivedAt = gameTime();
    }

    @Nullable
    public static HeroStatsPacket current() {
        if (latest == null || gameTime() - receivedAt > STALE_TICKS) {
            return null;
        }

        return latest;
    }

    private static long gameTime() {
        var level = Minecraft.getInstance().level;
        return level == null ? 0 : level.getGameTime();
    }
}
