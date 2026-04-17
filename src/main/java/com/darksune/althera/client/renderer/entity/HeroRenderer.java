package com.darksune.althera.client.renderer.entity;

import com.darksune.althera.client.model.entity.HeroModel;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HeroRenderer extends GeoEntityRenderer<HeroEntity> {

    public HeroRenderer(EntityRendererProvider.Context context) {
        super(context, new HeroModel());
    }
}