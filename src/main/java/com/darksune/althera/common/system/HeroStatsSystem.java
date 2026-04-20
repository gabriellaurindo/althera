package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class HeroStatsSystem {

    public static void applyAttributes(final HeroEntity hero, final Player player) {
        final HeroData heroData = HeroData.get(player);

        var maxHealthAttr = hero.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.setBaseValue(getMaxHealth(heroData.getLevel()));
        }

        var attackAttr = hero.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.setBaseValue(getAttack(heroData.getLevel()));
        }

        hero.setHealth((float) Math.min(heroData.getHealth(), getMaxHealth(heroData.getLevel())));
        hero.setOwner(player.getUUID());
    }

    public static double getMaxHealth(final int level) {
        return 10 + level - 1;
    }

    public static double getAttack(final int level) {
        return 5 + (0.25 * level) - 0.25;
    }
}