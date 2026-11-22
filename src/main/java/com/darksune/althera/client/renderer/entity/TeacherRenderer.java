package com.darksune.althera.client.renderer.entity;

import com.darksune.althera.Althera;
import com.darksune.althera.common.academy.teacher.TeacherDefinition;
import com.darksune.althera.common.entity.TeacherEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TeacherRenderer extends HumanoidMobRenderer<TeacherEntity, PlayerModel<TeacherEntity>> {

    private static final ResourceLocation FALLBACK =
            ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "textures/entity/teacher/headmaster.png");

    public TeacherRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TeacherEntity entity) {
        TeacherDefinition definition = entity.getDefinition();
        return definition == null ? FALLBACK : definition.texture();
    }
}
