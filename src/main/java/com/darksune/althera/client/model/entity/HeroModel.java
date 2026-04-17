package com.darksune.althera.client.model.entity;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HeroModel extends GeoModel<HeroEntity> {

    @Override
    public ResourceLocation getModelResource(HeroEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("althera", "geo/summoned.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HeroEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("althera", "textures/entity/summoned.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HeroEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("althera", "animations/summoned.animation.json");
    }
}
