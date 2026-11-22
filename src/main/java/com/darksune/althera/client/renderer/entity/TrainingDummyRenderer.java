package com.darksune.althera.client.renderer.entity;

import com.darksune.althera.Althera;
import com.darksune.althera.common.entity.TrainingDummyEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TrainingDummyRenderer extends HumanoidMobRenderer<TrainingDummyEntity, TrainingDummyRenderer.Model> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "textures/entity/training_dummy.png");

    public TrainingDummyRenderer(EntityRendererProvider.Context context) {
        super(context, new Model(context.bakeLayer(ModelLayers.PLAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(TrainingDummyEntity entity) {
        return TEXTURE;
    }

    public static class Model extends PlayerModel<TrainingDummyEntity> {

        private static final float WOBBLE = 0.25F;

        public Model(ModelPart root) {
            super(root, false);
        }

        @Override
        public void setupAnim(TrainingDummyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                              float netHeadYaw, float headPitch) {

            float wobble = entity.hurtTime > 0 ? Mth.sin(entity.hurtTime * 1.4F) * WOBBLE * entity.hurtTime / 10.0F : 0.0F;

            head.xRot = 0.0F;
            head.yRot = 0.0F;
            head.zRot = wobble * 0.5F;
            body.zRot = wobble;
            rightArm.xRot = 0.0F;
            rightArm.yRot = 0.0F;
            rightArm.zRot = Mth.HALF_PI + wobble;
            leftArm.xRot = 0.0F;
            leftArm.yRot = 0.0F;
            leftArm.zRot = -Mth.HALF_PI + wobble;
            rightLeg.xRot = 0.0F;
            rightLeg.zRot = 0.0F;
            leftLeg.xRot = 0.0F;
            leftLeg.zRot = 0.0F;

            hat.copyFrom(head);
            jacket.copyFrom(body);
            rightSleeve.copyFrom(rightArm);
            leftSleeve.copyFrom(leftArm);
            rightPants.copyFrom(rightLeg);
            leftPants.copyFrom(leftLeg);
        }
    }
}
