package com.darksune.althera.common.hero;

import net.minecraft.resources.ResourceLocation;

public class HeroDefinition {

    public final ResourceLocation id;

    public final String name;
    public final String description;

    public final ResourceLocation heroClass;
    public final ResourceLocation rarity;

    public final ResourceLocation model;
    public final ResourceLocation texture;

    public final String personality;

    public HeroDefinition(
            ResourceLocation id,
            String name,
            String description,
            ResourceLocation heroClass,
            ResourceLocation rarity,
            ResourceLocation model,
            ResourceLocation texture,
            String personality
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.heroClass = heroClass;
        this.rarity = rarity;
        this.model = model;
        this.texture = texture;
        this.personality = personality;
    }
}