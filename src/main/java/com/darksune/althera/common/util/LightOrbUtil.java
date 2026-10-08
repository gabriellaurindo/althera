package com.darksune.althera.common.util;

import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.LightOrbEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LightOrbUtil {

    // Active orb of each player (server-side). Orbs are not saved with the world,
    // so this map is the source of truth and no area search is needed.
    private static final Map<UUID, LightOrbEntity> ORBS = new HashMap<>();

    // the orb teleports by itself from 30 on; beyond this it is probably in a non-ticking chunk
    private static final double RECALL_DISTANCE = 48;

    public static void habilitarEspirito(final Player player) {
        // ❌ already exists? don't create another
        if (getPlayerOrb(player) != null) {
            return;
        }

        Level level = player.level();
        LightOrbEntity orb = AltheraEntities.LIGHT_ORB.get().create(level);

        if (orb != null) {
            orb.setPos(player.getX(), player.getY() + 1.5, player.getZ());
            orb.setOwnerUuid(player.getUUID());

            level.addFreshEntity(orb);
            ORBS.put(player.getUUID(), orb);
        }
    }

    /**
     * Creates the spirit if missing and brings it back if it was left behind (e.g. non-ticking chunk after a teleport).
     */
    public static void garantirEspirito(final Player player) {
        final LightOrbEntity orb = getPlayerOrb(player);

        if (orb == null) {
            habilitarEspirito(player);
            return;
        }

        if (orb.distanceTo(player) > RECALL_DISTANCE) {
            orb.setPos(player.getX(), player.getY() + 1.5, player.getZ());
        }
    }

    public static void desabilitarEspirito(final Player player) {
        final LightOrbEntity orb = ORBS.remove(player.getUUID());

        if (orb != null) {
            orb.discard();
        }
    }

    public static LightOrbEntity getPlayerOrb(final Player player) {
        final LightOrbEntity orb = ORBS.get(player.getUUID());

        if (orb == null || orb.isRemoved()) {
            ORBS.remove(player.getUUID());
            return null;
        }

        // left in the previous dimension (respawn/dimension change): discard it so it is recreated next to the player
        if (orb.level() != player.level()) {
            desabilitarEspirito(player);
            return null;
        }

        return orb;
    }

    /**
     * Orbs that are not in the map (from old saves or duplicates) must discard themselves.
     */
    public static boolean isActiveOrb(final LightOrbEntity orb) {
        final UUID owner = orb.getOwnerUUID();
        return owner != null && ORBS.get(owner) == orb;
    }
}
