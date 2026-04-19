package com.darksune.althera.common.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

public class ProtectOwnerGoal <E extends Mob & OwnableEntity> extends TargetGoal {

    private final E entity;
    private LivingEntity target;

    public ProtectOwnerGoal(final E entity) {
        super(entity, false);
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;

        LivingEntity attacker = owner.getLastHurtByMob();

        if (attacker == null) return false;
        if (attacker == entity) return false;

        this.target = attacker;
        return true;
    }

    @Override
    public void start() {
        entity.setTarget(target);
        super.start();
    }
}