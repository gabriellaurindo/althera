package com.darksune.althera.common.hero;

import com.darksune.althera.Althera;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Locale;

public record HeroClass(
        ResourceLocation id,
        String displayName,
        float healthMultiplier,
        float attackMultiplier,
        float armorMultiplier,
        float speedMultiplier,
        float knockbackResistance,
        float dodgeChance,
        List<ResourceLocation> strongAgainst
) {

    public static final float ADVANTAGE_MULTIPLIER = 1.25f;
    public static final float DISADVANTAGE_MULTIPLIER = 0.75f;

    public static final HeroClass NONE = new HeroClass(
            ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "none"),
            "Unknown",
            1.0f, 1.0f, 1.0f, 1.0f, 0.8f, 0.0f,
            List.of()
    );

    public static final Codec<HeroClass> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name", "Unknown").forGetter(HeroClass::displayName),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("health", 1.0f).forGetter(HeroClass::healthMultiplier),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("attack", 1.0f).forGetter(HeroClass::attackMultiplier),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("armor", 1.0f).forGetter(HeroClass::armorMultiplier),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed", 1.0f).forGetter(HeroClass::speedMultiplier),
            Codec.floatRange(0.0f, 1.0f).optionalFieldOf("knockback_resistance", 0.0f).forGetter(HeroClass::knockbackResistance),
            Codec.floatRange(0.0f, 1.0f).optionalFieldOf("dodge_chance", 0.0f).forGetter(HeroClass::dodgeChance),
            ResourceLocation.CODEC.listOf().optionalFieldOf("strong_against", List.of()).forGetter(HeroClass::strongAgainst)
    ).apply(instance, (name, health, attack, armor, speed, knockback, dodge, strong) ->
            new HeroClass(NONE.id, name, health, attack, armor, speed, knockback, dodge, strong)));

    public static ResourceLocation parseId(String raw) {
        String value = raw.toLowerCase(Locale.ROOT);
        return value.contains(":")
                ? ResourceLocation.parse(value)
                : ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, value);
    }

    public HeroClass withId(ResourceLocation id) {
        return new HeroClass(id, displayName, healthMultiplier, attackMultiplier, armorMultiplier, speedMultiplier,
                knockbackResistance, dodgeChance, strongAgainst);
    }

    public boolean isStrongAgainst(HeroClass other) {
        return strongAgainst.contains(other.id);
    }

    public boolean isWeakAgainst(HeroClass other) {
        return other.strongAgainst.contains(id);
    }

    public float getMultiplierAgainst(HeroClass other) {
        if (isStrongAgainst(other)) return ADVANTAGE_MULTIPLIER;
        if (isWeakAgainst(other)) return DISADVANTAGE_MULTIPLIER;
        return 1.0f;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getHealthMultiplier() {
        return healthMultiplier;
    }

    public float getAttackMultiplier() {
        return attackMultiplier;
    }

    public float getArmorMultiplier() {
        return armorMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public float getDodgeChance() {
        return dodgeChance;
    }
}
