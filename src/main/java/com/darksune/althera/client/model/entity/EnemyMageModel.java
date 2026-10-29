package com.darksune.althera.client.model.entity;

import com.darksune.althera.common.entity.EnemyMageEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class EnemyMageModel extends PlayerModel<EnemyMageEntity> {

    public EnemyMageModel(ModelPart root) {
        super(root, false);
    }

    @Override
    public void setupAnim(EnemyMageEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (entity.getCastTicks() <= 0) return;

        rightArm.xRot = -Mth.HALF_PI + head.xRot;
        rightArm.yRot = head.yRot - 0.1F;
        rightArm.zRot = Mth.cos(ageInTicks * 0.6F) * 0.05F;
        rightSleeve.copyFrom(rightArm);
    }
}
