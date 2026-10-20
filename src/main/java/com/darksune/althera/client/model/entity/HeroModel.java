package com.darksune.althera.client.model.entity;

import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
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

    private static final ResourceLocation DISGUISE_MODEL =
            ResourceLocation.fromNamespaceAndPath(
                    "althera",
                    "geo/entity/hero/disguise.geo.json"
            );

    private static final ResourceLocation DISGUISE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "althera",
                    "textures/entity/hero/disguise.png"
            );

    private static final ResourceLocation DISGUISE_ANIMATION =
            ResourceLocation.fromNamespaceAndPath(
                    "althera",
                    "animations/entity/hero/disguise.animation.json"
            );

    private static final String ULTIMATE_EFFECT_BONE = "ultimate_effect";

    @Override
    public void setCustomAnimations(HeroEntity animatable, long instanceId, AnimationState<HeroEntity> animationState) {
        getBone(ULTIMATE_EFFECT_BONE).ifPresent(bone -> bone.setHidden(!animatable.isPlayingUltimate()));
    }

    @Override
    public ResourceLocation getModelResource(HeroEntity animatable) {

        if (animatable.isDisguised()) {
            return DISGUISE_MODEL;
        }

        final HeroDefinition definition =
                animatable.getHeroDefinition();

        if (definition == null || definition.getModel() == null) {

            return DEFAULT_MODEL;
        }

        return definition.getModel();
    }

    @Override
    public ResourceLocation getTextureResource(HeroEntity animatable) {

        if (animatable.isDisguised()) {
            return DISGUISE_TEXTURE;
        }

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

        if (animatable.isDisguised()) {
            return DISGUISE_ANIMATION;
        }

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