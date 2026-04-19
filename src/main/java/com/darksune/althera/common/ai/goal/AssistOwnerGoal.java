package com.darksune.althera.common.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

public class AssistOwnerGoal<E extends Mob & OwnableEntity> extends TargetGoal {

    private final E entity;
    private LivingEntity target;

    public AssistOwnerGoal(final E entity) {
        super(entity, false);
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;

        LivingEntity attacked = owner.getLastHurtMob();

        if (attacked == null) return false;

        this.target = attacked;
        return true;
    }

    @Override
    public void start() {
        entity.setTarget(target);
        super.start();
    }
}