package com.darksune.althera.client.model.entity;

import com.darksune.althera.common.entity.NobleMageEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class NobleMageModel extends PlayerModel<NobleMageEntity> {

    public NobleMageModel(ModelPart root) {
        super(root, false);
    }

    @Override
    public void setupAnim(NobleMageEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (entity.getCastTicks() <= 0) return;

        rightArm.xRot = -Mth.HALF_PI + head.xRot;
        rightArm.yRot = head.yRot - 0.1F;
        rightArm.zRot = Mth.cos(ageInTicks * 0.6F) * 0.05F;
        rightSleeve.copyFrom(rightArm);
    }
}
