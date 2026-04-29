package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class HeroStatsSystem {

    public static void applyAttributes(final HeroEntity hero, final Player player) {
        final HeroData heroData = HeroData.get(player);

        var maxHealthAttr = hero.getAttribute(Attributes.MAX_HEALTH);
        var attackAttr = hero.getAttribute(Attributes.ATTACK_DAMAGE);
        var armorAttr = hero.getAttribute(Attributes.ARMOR);
        if (maxHealthAttr != null) {
            maxHealthAttr.setBaseValue(getMaxHealth(heroData));
        }
        if (attackAttr != null) {
            attackAttr.setBaseValue(getAttack(heroData));
        }
        if (armorAttr != null) {
            armorAttr.setBaseValue(getArmor());
        }
        //todo temp
        if (heroData.getHeroClass() != null) {
            hero.setCustomName(Component.literal(heroData.getHeroClass().getDisplayName()));
        }

        hero.setHealth((float) Math.min(heroData.getHealth(), getMaxHealth(heroData)));
        hero.setOwner(player.getUUID());
    }

    public static double getMaxHealth(final HeroData data) {
        double base = 20 + data.getLevel() - 1;

        if (data.getHeroClass() != null) {
            base *= data.getHeroClass().getHealthMultiplier();
        }

        return base;
    }

    public static double getAttack(final HeroData data) {
        double base = 1.5 + (0.125 * data.getLevel()) - 0.125;

        if (data.getHeroClass() != null) {
            base *= data.getHeroClass().getAttackMultiplier();
        }

        return base;
    }

    public static double getArmor() {
        return 0;
    }

    public static int getMaxInterventions() {
        return 3;
    }
}