package com.darksune.althera.common.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class ProtectOwnerGoal <E extends Mob & OwnableEntity> extends TargetGoal {

    private final E entity;
    private LivingEntity target;
    private int timestamp;

    public ProtectOwnerGoal(final E entity) {
        super(entity, false);
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;

        LivingEntity attacker = owner.getLastHurtByMob();

        if (attacker == null) return false;
        if (attacker == entity) return false;
        if (owner.getLastHurtByMobTimestamp() == timestamp) return false;

        this.target = attacker;
        return canAttack(attacker, TargetingConditions.DEFAULT);
    }

    @Override
    public void start() {
        entity.setTarget(target);
        LivingEntity owner = entity.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}
