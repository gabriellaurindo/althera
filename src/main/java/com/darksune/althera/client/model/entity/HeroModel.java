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
                    "animations/hero/hero.animation.json"
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

        // sem definition -> usa texture padrão
        if (definition == null) {
            return DEFAULT_TEXTURE;
        }

        // model custom sem texture custom
        // evita UV quebrado
        if (definition.getModel() != null
                && definition.getTexture() == null) {

            return null;
        }

        // texture custom
        if (definition.getTexture() != null) {
            return definition.getTexture();
        }

        // model padrão -> texture padrão
        return DEFAULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HeroEntity animatable) {

        final HeroDefinition definition =
                animatable.getHeroDefinition();

        // sem definition -> usa default completo
        if (definition == null) {
            return DEFAULT_ANIMATION;
        }

        // modelo custom sem animação custom
        // deixa sem animação pra evitar bone mismatch
        if (definition.getModel() != null
                && definition.getAnimations() == null) {

            return null;
        }

        // animação custom
        if (definition.getAnimations() != null) {
            return definition.getAnimations();
        }

        // modelo padrão -> animação padrão
        return DEFAULT_ANIMATION;
    }
}