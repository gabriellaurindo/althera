package com.darksune.althera.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface HeroSummoner {

    LivingEntity asEntity();

    boolean isAlly(Entity entity);

    void onServantDefeated(HeroEntity servant);
}
