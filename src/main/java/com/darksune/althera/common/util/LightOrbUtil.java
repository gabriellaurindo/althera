package com.darksune.althera.common.util;

import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.LightOrbEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public class LightOrbUtil {

    public static void habilitarEspirito(final Player player) {
        Level level = player.level();

        // ❌ já existe? não cria outro
        if (getPlayerOrb(player, level) != null) {
            return;
        }

        LightOrbEntity orb = AltheraEntities.LIGHT_ORB.get().create(level);

        if (orb != null) {
            orb.setPos(player.getX(), player.getY() + 1.5, player.getZ());
            orb.setOwner(player.getUUID());

            level.addFreshEntity(orb);
        }
    }

    public static void desabilitarEspirito(final Player player) {
        final UUID uuid = player.getUUID();

        //TODO temp, usar uuid
        for (ServerLevel level : player.getServer().getAllLevels()) {
            AABB box = new AABB(player.blockPosition()).inflate(10000);

            level.getEntitiesOfClass(LightOrbEntity.class, box)
                    .stream()
                    .filter(e -> uuid.equals(e.getOwnerUUID()))
                    .forEach(Entity::discard);
        }
    }

    public static LightOrbEntity getPlayerOrb(Player player, Level level) {
        return level.getEntitiesOfClass(LightOrbEntity.class, player.getBoundingBox().inflate(50))
                .stream()
                .filter(o -> player.getUUID().equals(o.getOwner().getUUID()))
                .findFirst()
                .orElse(null);
    }
}
