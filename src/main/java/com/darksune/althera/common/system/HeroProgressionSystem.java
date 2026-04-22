package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class HeroProgressionSystem {

    public static void addXp(final Player player, final LivingDeathEvent event) {
        final LivingEntity killed = event.getEntity();
        final HeroData heroData = HeroData.get(player);
        final float maxHealthKilled = killed.getMaxHealth();
        final long amount = getAmountXp(maxHealthKilled);
        heroData.setXp(heroData.getXp() + amount);

        while (heroData.getXp() >= getXpToNextLevel(heroData)) {
            heroData.setXp(heroData.getXp() - getXpToNextLevel(heroData));
            heroData.setLevel(heroData.getLevel() + 1);
            HeroStatsSystem.applyAttributes((HeroEntity) event.getSource().getEntity(), player);
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal(
                            "§aSeu summon subiu para o nível " + heroData.getLevel() + "!"
                    )
            );
        }

        heroData.sync(player);
    }

    public static long getXpToNextLevel(final HeroData heroData) {
        int level = heroData.getLevel();

        return Math.round(20 + Math.pow(level, 1.5) * 5);
    }

    public static long getAmountXp(final float maxHealthKilled) {
        return Math.max(1, (long) Math.ceil(maxHealthKilled * 0.1f));
    }
}