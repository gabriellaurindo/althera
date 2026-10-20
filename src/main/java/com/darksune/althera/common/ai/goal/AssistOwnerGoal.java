package com.darksune.althera.common.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class AssistOwnerGoal<E extends Mob & OwnableEntity> extends TargetGoal {

    private final E entity;
    private LivingEntity target;
    private int timestamp;

    public AssistOwnerGoal(final E entity) {
        super(entity, false);
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;

        LivingEntity attacked = owner.getLastHurtMob();

        if (attacked == null) return false;
        if (attacked == entity) return false;
        if (owner.getLastHurtMobTimestamp() == timestamp) return false;

        this.target = attacked;
        return canAttack(attacked, TargetingConditions.DEFAULT);
    }

    @Override
    public void start() {
        entity.setTarget(target);
        LivingEntity owner = entity.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtMobTimestamp();
        }
        super.start();
    }
}
