package com.darksune.althera.client.renderer.entity;

import com.darksune.althera.client.model.entity.HeroModel;
import com.darksune.althera.common.entity.HeroEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HeroRenderer extends GeoEntityRenderer<HeroEntity> {

    public HeroRenderer(EntityRendererProvider.Context context) {
        super(context, new HeroModel());
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, HeroEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        final float scale = animatable.getModelScale();
        super.scaleModelForRender(widthScale * scale, heightScale * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public RenderType getRenderType(HeroEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        if (animatable.isPlayingUltimate() && !animatable.isInvisible()) {
            return RenderType.entityTranslucent(texture);
        }
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public boolean shouldShowName(HeroEntity animatable) {
        return false;
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
