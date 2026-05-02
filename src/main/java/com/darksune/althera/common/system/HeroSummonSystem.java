package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

import static com.darksune.althera.common.util.LightOrbUtil.desabilitarEspirito;

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
        return spawnSummon(player, true);
    }

    public static HeroEntity spawnSummon(final Player player, final boolean sendMessage) {
        final HeroData heroData = HeroData.get(player);

        //todo: se eu puder garantir que esse .remove nao seja chamado nao vou precisar do if de espirito dentro do remove, sempre vai chamar
        if (heroData.getSummonUUID() != null) {
            final HeroEntity existing = HeroSummonSystem.getSummon(player);
            if (existing != null) {
                existing.remove(false);
            }
            heroData.clearSummon();
        }

        if (heroData.isDefeated()) {
            if (sendMessage) {
                player.sendSystemMessage(
                        Component.literal("§eYour summon is defeated. Wait until it recovers.")
                );
            }
            return null;
        }
        final HeroEntity entity = HeroEntity.create(player);

        moveTo(player, entity);

        player.level().addFreshEntity(entity);

        heroData.setSummonUUID(entity.getUUID());
        heroData.sync(player);
        desabilitarEspirito(player);
        return entity;
    }

    public static HeroEntity spawnOrMove(final Player player) {
        final HeroEntity summon = HeroSummonSystem.getSummon(player);
        if (summon == null) {
            return HeroSummonSystem.spawnSummon(player, false);
        }
        return moveTo(player, summon);
    }

    private static HeroEntity moveTo(final Player player, final HeroEntity entity) {
        entity.moveTo(
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYRot(),
                0
        );
        return entity;
    }
}
