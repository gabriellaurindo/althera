package com.darksune.althera.client.renderer.entity;

import com.darksune.althera.client.model.entity.NobleMageModel;
import com.darksune.althera.common.entity.NobleMageEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class NobleMageRenderer extends HumanoidMobRenderer<NobleMageEntity, NobleMageModel> {

    private static final ResourceLocation GREEN =
            ResourceLocation.fromNamespaceAndPath("althera", "textures/entity/noble_mage_green.png");
    private static final ResourceLocation YELLOW =
            ResourceLocation.fromNamespaceAndPath("althera", "textures/entity/noble_mage_yellow.png");
    private static final ResourceLocation RED =
            ResourceLocation.fromNamespaceAndPath("althera", "textures/entity/noble_mage_red.png");

    public NobleMageRenderer(EntityRendererProvider.Context context) {
        super(context, new NobleMageModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(NobleMageEntity entity) {
        final List<Integer> levels = NobleMageEntity.SERVANT_LEVELS;
        final int level = entity.getServantLevel();
        if (level >= levels.get(2)) return RED;
        if (level >= levels.get(1)) return YELLOW;
        return GREEN;
    }
}
