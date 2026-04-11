package com.darksune.althera.client.renderer;

import com.darksune.althera.common.entity.LightOrbEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightOrbRenderer extends EntityRenderer<LightOrbEntity> {

    public LightOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(LightOrbEntity entity) {
        return null; // não usa textura
    }

    @Override
    public boolean shouldRender(LightOrbEntity entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double x, double y, double z) {
        return true; // sempre renderiza (importante pra partículas)
    }
}