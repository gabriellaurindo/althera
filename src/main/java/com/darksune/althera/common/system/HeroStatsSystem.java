package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class HeroStatsSystem {

    public static void applyAttributes(final HeroEntity hero, final Player player) {
        final HeroData heroData = HeroData.get(player);

        var maxHealthAttr = hero.getAttribute(Attributes.MAX_HEALTH);
        var attackAttr = hero.getAttribute(Attributes.ATTACK_DAMAGE);
        var armorAttr = hero.getAttribute(Attributes.ARMOR);
        if (maxHealthAttr != null) {
            maxHealthAttr.setBaseValue(getMaxHealth(heroData.getLevel()));
        }
        if (attackAttr != null) {
            attackAttr.setBaseValue(getAttack(heroData.getLevel()));
        }
        if (armorAttr != null) {
            armorAttr.setBaseValue(getArmor());
        }

        hero.setHealth((float) Math.min(heroData.getHealth(), getMaxHealth(heroData.getLevel())));
        hero.setOwner(player.getUUID());
    }

    public static double getMaxHealth(final int level) {
        return 20 + level - 1;
    }

    public static double getAttack(final int level) {
        return 1.5 + (0.125 * level) - 0.125;
    }

    public static double getArmor() {
        return 0;
    }

    public static int getMaxInterventions() {
        return 3;
    }
}