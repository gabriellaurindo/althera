package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HeroProgressionSystem {

    public static final int MAX_LEVEL = 50;
    public static final double OWNER_KILL_XP_SHARE = 0.5;
    private static final float XP_PER_MAX_HEALTH = 0.5f;

    public static void addXp(final Player player, final LivingEntity killed, final double share) {
        final HeroData heroData = HeroData.get(player);
        if (isMaxLevel(heroData)) return;

        final long amount = Math.max(1, Math.round(getAmountXp(killed.getMaxHealth()) * share));
        heroData.setXp(heroData.getXp() + amount);

        while (heroData.getXp() >= getXpToNextLevel(heroData)) {
            heroData.setXp(heroData.getXp() - getXpToNextLevel(heroData));
            heroData.setLevel(heroData.getLevel() + 1);
            HeroSummonSystem.refreshSummon(player);
            player.sendSystemMessage(
                    Component.literal(
                            "§aYour summon reached level " + heroData.getLevel() + "!"
                    )
            );

            if (isMaxLevel(heroData)) {
                heroData.setXp(0);
                player.sendSystemMessage(Component.literal("§6Your summon reached the maximum level!"));
                break;
            }
        }

        heroData.sync(player);
    }

    public static boolean isMaxLevel(final HeroData heroData) {
        return heroData.getLevel() >= MAX_LEVEL;
    }

    public static long getXpToNextLevel(final HeroData heroData) {
        int level = heroData.getLevel();

        return Math.round(20 + Math.pow(level, 1.5) * 5);
    }

    public static long getAmountXp(final float maxHealthKilled) {
        return Math.max(1, (long) Math.ceil(maxHealthKilled * XP_PER_MAX_HEALTH));
    }
}