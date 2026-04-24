package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class HeroSummonSystem {

    public static HeroEntity getSummon(final Player player) {
        final UUID uuid = HeroData.get(player).getSummonUUID();

        if (uuid == null) return null;

        for (ServerLevel level : player.getServer().getAllLevels()) {
            final Entity entity = level.getEntity(uuid);

            if (entity instanceof HeroEntity hero) {
                return hero;
            }
        }

        return null;
    }

    public static HeroEntity spawnSummon(final Player player) {
        final HeroData heroData = HeroData.get(player);

        if (heroData.getSummonUUID() != null) {
            final HeroEntity existing = HeroSummonSystem.getSummon(player);
            if (existing != null) {
                existing.remove();
            }

            // 🔥 ESSENCIAL
            heroData.clearSummon();
        }

        final HeroEntity entity = HeroEntity.create(player);

        entity.moveTo(
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYRot(),
                0
        );

        player.level().addFreshEntity(entity);

        heroData.setSummonUUID(entity.getUUID());
        heroData.sync(player);

        return entity;
    }
}
