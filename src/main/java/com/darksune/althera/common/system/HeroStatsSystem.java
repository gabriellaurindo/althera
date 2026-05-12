package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class HeroStatsSystem {

    private final static int BASE_HEALTH = 19;
    private final static double BASE_ATTACK = 1.125;
    private final static double BASE_ARMOR = 2.0;
    //todo centralizar o calculo do hp maximo
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
            armorAttr.setBaseValue(getArmor(heroData));
        }

        // temp name
        if (heroData.getHeroDefinition() != null) {
            hero.setCustomName(Component.literal(heroData.getHeroDefinition().getName()));
        }

        hero.setHealth((float) Math.min(heroData.getHealth(), getMaxHealth(heroData)));
        hero.setOwnerUuid(player.getUUID());
    }

    public static double getMaxHealth(final HeroData data) {
        HeroDefinition def = data.getHeroDefinition();

        double base = BASE_HEALTH + data.getLevel();

        if (def == null) return base;

        double result = base;

        // Class scaling
        result *= def.getHeroClass().getHealthMultiplier();

        // Rank scaling (HP-focused)
        result *= def.getRank().getHealthMultiplier();

        // Nature scaling (global)
        result *= def.getNature().getGlobalMultiplier();

        return result;
    }

    public static double getAttack(final HeroData data) {
        HeroDefinition def = data.getHeroDefinition();

        double base = BASE_ATTACK + (0.125 * data.getLevel());

        if (def == null) return base;

        double result = base;

        // Class scaling
        result *= def.getHeroClass().getAttackMultiplier();

        // Rank scaling (lighter impact than HP)
        result *= getRankAttackScaling(def);

        // Nature scaling
        result *= def.getNature().getGlobalMultiplier();

        return result;
    }

    public static double getArmor(final HeroData data) {
        HeroDefinition def = data.getHeroDefinition();

        double base = BASE_ARMOR;

        if (def == null) return base;

        double result = base;

        // Class scaling
        result *= def.getHeroClass().getArmorMultiplier();

        // Rank scaling (moderate)
        result *= getRankArmorScaling(def);

        // Nature scaling
        result *= def.getNature().getGlobalMultiplier();

        return result;
    }

    public static int getMaxInterventions() {
        return 3;
    }

    // =========================
    // INTERNAL SCALING HELPERS
    // =========================

    private static double getRankAttackScaling(HeroDefinition def) {
        return lerp(def.getRank().getHealthMultiplier(), 0.6f);
    }

    private static double getRankArmorScaling(HeroDefinition def) {
        return lerp(def.getRank().getHealthMultiplier(), 0.5f);
    }

    private static double lerp(double value, double factor) {
        return 1.0 + (value - 1.0) * factor;
    }
}