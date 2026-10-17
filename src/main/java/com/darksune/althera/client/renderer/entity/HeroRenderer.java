package com.darksune.althera.client.renderer.entity;

import com.darksune.althera.client.model.entity.HeroModel;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HeroRenderer extends GeoEntityRenderer<HeroEntity> {

    public HeroRenderer(EntityRendererProvider.Context context) {
        super(context, new HeroModel());
    }

    @Override
    protected float getDeathMaxRotation(HeroEntity animatable) {
        return hasDeathAnimation(animatable) ? 0 : super.getDeathMaxRotation(animatable);
    }

    private boolean hasDeathAnimation(HeroEntity animatable) {
        final ResourceLocation location = getGeoModel().getAnimationResource(animatable);
        if (location == null) return false;
        final BakedAnimations animations = GeckoLibCache.getBakedAnimations().get(location);
        return animations != null && animations.getAnimation("death") != null;
    }
}