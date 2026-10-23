package com.darksune.althera.client.renderer.entity;

import com.darksune.althera.client.model.entity.EnemyMageModel;
import com.darksune.althera.common.enemy.EnemyMageDefinition;
import com.darksune.althera.common.entity.EnemyMageEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EnemyMageRenderer extends HumanoidMobRenderer<EnemyMageEntity, EnemyMageModel> {

    private static final ResourceLocation FALLBACK = EnemyMageDefinition.DEFAULT_TIERS.getFirst().texture();

    public EnemyMageRenderer(EntityRendererProvider.Context context) {
        super(context, new EnemyMageModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(EnemyMageEntity entity) {
        final EnemyMageDefinition definition = entity.getDefinition();
        if (definition == null) return FALLBACK;

        return definition.tierFor(entity.getServantLevel()).texture();
    }
}
