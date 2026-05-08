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

        if (definition == null || definition.getTexture() == null) {
            return DEFAULT_TEXTURE;
        }

        return definition.getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(HeroEntity animatable) {
        return DEFAULT_ANIMATION;
    }
}
