package com.darksune.althera.common.util;

import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.LightOrbEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
        final Level level = player.level();

        level.getEntitiesOfClass(LightOrbEntity.class, player.getBoundingBox().inflate(50))
                .stream()
                .filter(o -> player.getUUID().equals(o.getOwner().getUUID()))
                .forEach(Entity::discard);
    }

    public static LightOrbEntity getPlayerOrb(Player player, Level level) {
        return level.getEntitiesOfClass(LightOrbEntity.class, player.getBoundingBox().inflate(50))
                .stream()
                .filter(o -> player.getUUID().equals(o.getOwner().getUUID()))
                .findFirst()
                .orElse(null);
    }
}
