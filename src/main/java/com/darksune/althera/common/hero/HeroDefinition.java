package com.darksune.althera.common.hero;

import net.minecraft.resources.ResourceLocation;

public class HeroDefinition {

    private final ResourceLocation id;

    private final String name;
    private final String description;

    private final HeroClass heroClass;
    private final HeroRarity rarity;
    private final HeroNature nature;

    private final ResourceLocation model;
    private final ResourceLocation texture;

    private final String personality;

    public HeroDefinition(
            ResourceLocation id,
            String name,
            String description,
            HeroClass heroClass,
            HeroRarity rarity,
            HeroNature nature,
            ResourceLocation model,
            ResourceLocation texture,
            String personality
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.heroClass = heroClass;
        this.rarity = rarity;
        this.nature = nature;
        this.model = model;
        this.texture = texture;
        this.personality = personality;
    }

    public ResourceLocation getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HeroClass getHeroClass() {
        return heroClass;
    }

    public HeroRarity getRarity() {
        return rarity;
    }

    public HeroNature getNature() {
        return nature;
    }

    public ResourceLocation getModel() {
        return model;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public String getPersonality() {
        return personality;
    }
}