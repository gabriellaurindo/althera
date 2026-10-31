package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroStats;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class HeroStatsSystem {

    private final static double BASE_KNOCKBACK_RESISTANCE = 0.8;
    public static final double DISGUISE_STAT_MULTIPLIER = 0.5;
    public static final double DISGUISE_SPEED_MULTIPLIER = 0.75;
    public static final String DISGUISED_NAME = "Hooded Figure";
    private static final double SPIRIT_REGEN_BASE = 2.0;
    private static final double SPIRIT_REGEN_PER_LEVEL = 0.1;
    private static final double SUMMONED_REGEN_BASE = 1.0;
    private static final double SUMMONED_REGEN_PER_LEVEL = 0.05;
    //todo centralize the max HP calculation
    public static void applyAttributes(final HeroEntity hero, final Player player) {
        final HeroData heroData = HeroData.get(player);

        hero.setDisguised(heroData.isDisguised());

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
        if (heroData.isDisguised()) {
            hero.setCustomName(Component.literal(DISGUISED_NAME));
        } else if (heroData.getHeroDefinition() != null) {
            hero.setCustomName(Component.literal(heroData.getHeroDefinition().getName()));
        }

        hero.setHealth((float) Math.min(heroData.getHealth(), getMaxHealth(heroData)));
        hero.setOwnerUuid(player.getUUID());
    }

    public static void applyServantAttributes(final HeroEntity hero, final HeroDefinition def, final int level,
                                              final float health) {
        setBase(hero, Attributes.MAX_HEALTH, getMaxHealth(def, level, 1.0));
        setBase(hero, Attributes.ATTACK_DAMAGE, getAttack(def, level, 1.0));
        setBase(hero, Attributes.ARMOR, getArmor(def, level, 1.0));
        setBase(hero, Attributes.KNOCKBACK_RESISTANCE, getKnockbackResistance(def, 1.0));
        setBase(hero, Attributes.MOVEMENT_SPEED, getSpeed(def, 1.0));

        hero.setHeroId(def.getId());
        hero.setCustomName(Component.literal(def.getName()));
        hero.setHealth(Math.min(health, hero.getMaxHealth()));
    }

    private static void setBase(final HeroEntity hero, final Holder<Attribute> attribute, final double value) {
        final AttributeInstance instance = hero.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    public static double getSpiritRegen(final HeroData data) {
        return SPIRIT_REGEN_BASE + SPIRIT_REGEN_PER_LEVEL * (data.getLevel() - 1);
    }

    public static double getSummonedRegen(final HeroData data) {
        return SUMMONED_REGEN_BASE + SUMMONED_REGEN_PER_LEVEL * (data.getLevel() - 1);
    }

    public static double getMaxHealth(final HeroData data) {
        return getMaxHealth(data.getHeroDefinition(), data.getLevel(), getDisguiseMultiplier(data));
    }

    public static double getMaxHealth(@Nullable final HeroDefinition def, final int level, final double disguise) {
        HeroStats stats = def == null ? HeroStats.DEFAULT : def.getStats();
        double base = stats.health() * disguise + stats.healthPerLevel() * level;

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
        return getAttack(data.getHeroDefinition(), data.getLevel(), getDisguiseMultiplier(data));
    }

    public static double getAttack(@Nullable final HeroDefinition def, final int level, final double disguise) {
        HeroStats stats = def == null ? HeroStats.DEFAULT : def.getStats();
        double base = stats.attack() * disguise + stats.attackPerLevel() * level;

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
        return getArmor(data.getHeroDefinition(), data.getLevel(), getDisguiseMultiplier(data));
    }

    public static double getArmor(@Nullable final HeroDefinition def, final int level, final double disguise) {
        HeroStats stats = def == null ? HeroStats.DEFAULT : def.getStats();
        double base = stats.armor() * disguise + stats.armorPerLevel() * level;

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
        return getSpeed(data.getHeroDefinition(), data.isDisguised() ? DISGUISE_SPEED_MULTIPLIER : 1.0);
    }

    public static double getSpeed(@Nullable final HeroDefinition def, final double disguise) {
        if (def == null) return HeroStats.DEFAULT.speed();

        return def.getStats().speed() * def.getHeroClass().getSpeedMultiplier() * disguise;
    }

    public static double getKnockbackResistance(final HeroData data) {
        return getKnockbackResistance(data.getHeroDefinition(), getDisguiseMultiplier(data));
    }

    public static double getKnockbackResistance(@Nullable final HeroDefinition def, final double disguise) {
        if (def == null) return BASE_KNOCKBACK_RESISTANCE;

        return def.getHeroClass().getKnockbackResistance() * disguise;
    }

    public static double getDodgeChance(final HeroData data) {
        HeroDefinition def = data.getHeroDefinition();

        if (def == null) return 0;

        return def.getHeroClass().getDodgeChance() * getDisguiseMultiplier(data);
    }

    public static double getDisguiseMultiplier(final HeroData data) {
        return data.isDisguised() ? DISGUISE_STAT_MULTIPLIER : 1.0;
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