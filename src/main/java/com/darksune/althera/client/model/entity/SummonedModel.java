package com.darksune.althera.client.model.entity;

import com.darksune.althera.common.entity.SummonedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SummonedModel extends GeoModel<SummonedEntity> {

    @Override
    public ResourceLocation getModelResource(SummonedEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("althera", "geo/summoned.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SummonedEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("althera", "textures/entity/summoned.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SummonedEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("althera", "animations/summoned.animation.json");
    }
}