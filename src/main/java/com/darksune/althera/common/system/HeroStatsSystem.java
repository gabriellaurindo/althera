package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroStats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class HeroStatsSystem {

    private final static double BASE_KNOCKBACK_RESISTANCE = 0.8;
    //todo centralize the max HP calculation
    public static void applyAttributes(final HeroEntity hero, final Player player) {
        final HeroData heroData = HeroData.get(player);

        var maxHealthAttr = hero.getAttribute(Attributes.MAX_HEALTH);
        var attackAttr = hero.getAttribute(Attributes.ATTACK_DAMAGE);
        var armorAttr = hero.getAttribute(Attributes.ARMOR);
        var knockbackResistanceAttr = hero.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        var speedAttr = hero.getAttribute(Attributes.MOVEMENT_SPEED);

        if (maxHealthAttr != null) {
            maxHealthAttr.setBaseValue(getMaxHealth(heroData));
        }

        if (attackAttr != null) {
            attackAttr.setBaseValue(getAttack(heroData));
        }

        if (armorAttr != null) {
            armorAttr.setBaseValue(getArmor(heroData));
        }

        if (knockbackResistanceAttr != null) {
            knockbackResistanceAttr.setBaseValue(getKnockbackResistance(heroData));
        }

        if (speedAttr != null) {
            speedAttr.setBaseValue(getSpeed(heroData));
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

        HeroStats stats = def == null ? HeroStats.DEFAULT : def.getStats();
        double base = stats.health() + stats.healthPerLevel() * data.getLevel();

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

        HeroStats stats = def == null ? HeroStats.DEFAULT : def.getStats();
        double base = stats.attack() + stats.attackPerLevel() * data.getLevel();

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

        double base = def == null ? HeroStats.DEFAULT.armor() : def.getStats().armor();

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

    public static double getSpeed(final HeroData data) {
        HeroDefinition def = data.getHeroDefinition();

        if (def == null) return HeroStats.DEFAULT.speed();

        return def.getStats().speed() * def.getHeroClass().getSpeedMultiplier();
    }

    public static double getKnockbackResistance(final HeroData data) {
        HeroDefinition def = data.getHeroDefinition();

        if (def == null) return BASE_KNOCKBACK_RESISTANCE;

        return def.getHeroClass().getKnockbackResistance();
    }

    public static double getDodgeChance(final HeroData data) {
        HeroDefinition def = data.getHeroDefinition();

        if (def == null) return 0;

        return def.getHeroClass().getDodgeChance();
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