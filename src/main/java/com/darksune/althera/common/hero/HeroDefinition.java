package com.darksune.althera.common.hero;

import net.minecraft.resources.ResourceLocation;

public class HeroDefinition {

    private final ResourceLocation id;

    private final String name;
    private final String description;

    private final HeroClass heroClass;
    private final HeroRank rank;
    private final HeroNature nature;

    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animations;

    private final String personality;

    public HeroDefinition(
            ResourceLocation id,
            String name,
            String description,
            HeroClass heroClass,
            HeroRank rank,
            HeroNature nature,
            ResourceLocation model,
            ResourceLocation texture,
            ResourceLocation animations,
            String personality
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.heroClass = heroClass;
        this.rank = rank;
        this.nature = nature;
        this.model = model;
        this.texture = texture;
        this.animations = animations;
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

    public HeroRank getRank() {
        return rank;
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

    public ResourceLocation getAnimations() {
        return animations;
    }

    public String getPersonality() {
        return personality;
    }
}