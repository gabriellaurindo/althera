package com.darksune.althera.client.renderer.entity;

import com.darksune.althera.client.model.entity.SummonedModel;
import com.darksune.althera.common.entity.SummonedEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SummonedRenderer extends GeoEntityRenderer<SummonedEntity> {

    public SummonedRenderer(EntityRendererProvider.Context context) {
        super(context, new SummonedModel());
    }
}