package com.darksune.althera.client.model.entity;

import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HeroModel extends GeoModel<HeroEntity> {

    private static final ResourceLocation DEFAULT_MODEL =
            ResourceLocation.fromNamespaceAndPath(
                    "althera",
                    "geo/entity/hero/hero.geo.json"
            );

    private static final ResourceLocation DEFAULT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "althera",
                    "textures/entity/hero/hero.png"
            );

    private static final ResourceLocation DEFAULT_ANIMATION =
            ResourceLocation.fromNamespaceAndPath(
                    "althera",
                    "animations/entity/hero/hero.animation.json"
            );

    @Override
    public ResourceLocation getModelResource(HeroEntity animatable) {

        final HeroDefinition definition =
                animatable.getHeroDefinition();

        if (definition == null || definition.getModel() == null) {

            return DEFAULT_MODEL;
        }

        return definition.getModel();
    }

    @Override
    public ResourceLocation getTextureResource(HeroEntity animatable) {

        final HeroDefinition definition =
                animatable.getHeroDefinition();

        // no definition -> use default texture
        if (definition == null) {
            return DEFAULT_TEXTURE;
        }

        // custom model without custom texture
        // avoids broken UVs
        if (definition.getModel() != null
                && definition.getTexture() == null) {

            return null;
        }

        // custom texture
        if (definition.getTexture() != null) {
            return definition.getTexture();
        }

        // default model -> default texture
        return DEFAULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HeroEntity animatable) {

        final HeroDefinition definition =
                animatable.getHeroDefinition();

        // no definition -> use full defaults
        if (definition == null) {
            return DEFAULT_ANIMATION;
        }

        // custom model without custom animation
        // leave it without animation to avoid bone mismatch
        if (definition.getModel() != null
                && definition.getAnimations() == null) {

            return null;
        }

        // custom animation
        if (definition.getAnimations() != null) {
            return definition.getAnimations();
        }

        // default model -> default animation
        return DEFAULT_ANIMATION;
    }
}