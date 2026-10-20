package com.darksune.althera.common.hero;

import net.minecraft.resources.ResourceLocation;

public class HeroDefinition {

    private final ResourceLocation id;

    private final String name;
    private final String description;

    private final ResourceLocation heroClassId;
    private final HeroRank rank;
    private final HeroNature nature;

    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animations;

    private final String personality;
    private final float scale;
    private final ResourceLocation ultimate;
    private final HeroAttack attack;
    private final HeroAnimation animation;
    private final HeroStats stats;

    public HeroDefinition(
            ResourceLocation id,
            String name,
            String description,
            ResourceLocation heroClassId,
            HeroRank rank,
            HeroNature nature,
            ResourceLocation model,
            ResourceLocation texture,
            ResourceLocation animations,
            String personality,
            float scale,
            ResourceLocation ultimate,
            HeroAttack attack,
            HeroAnimation animation,
            HeroStats stats
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.heroClassId = heroClassId;
        this.rank = rank;
        this.nature = nature;
        this.model = model;
        this.texture = texture;
        this.animations = animations;
        this.personality = personality;
        this.scale = scale;
        this.ultimate = ultimate;
        this.attack = attack;
        this.animation = animation;
        this.stats = stats;
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

    public ResourceLocation getHeroClassId() {
        return heroClassId;
    }

    public HeroClass getHeroClass() {
        return HeroClassRegistry.get(heroClassId);
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

    public float getScale() {
        return scale;
    }

    public ResourceLocation getUltimate() {
        return ultimate;
    }

    public HeroAttack getAttack() {
        return attack;
    }

    public HeroAnimation getAnimation() {
        return animation;
    }

    public HeroStats getStats() {
        return stats;
    }

    public boolean isRollable() {
        return model != null && texture != null && animations != null;
    }
}