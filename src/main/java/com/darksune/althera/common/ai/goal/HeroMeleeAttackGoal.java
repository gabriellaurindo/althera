package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class HeroMeleeAttackGoal extends MeleeAttackGoal {

    private static final double REACH_SEARCH_RADIUS = 2.0D;
    private static final int CHASE_INTERVAL_TICKS = 5;

    private final HeroEntity hero;
    private final double speedModifier;

    private int lastAttackTick = Integer.MIN_VALUE / 2;

    public HeroMeleeAttackGoal(HeroEntity hero, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(hero, speedModifier, followingTargetEvenIfNotSeen);
        this.hero = hero;
        this.speedModifier = speedModifier;
    }

    @Override
    public boolean canUse() {
        return !hero.getAttack().isRanged() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !hero.getAttack().isRanged() && super.canContinueToUse();
    }

    @Override
    public void tick() {
        switchToThreatInReach();
        super.tick();
        keepChasing();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (!this.canPerformAttack(target)) {
            return;
        }
        hero.triggerAttackAnimation();
        hero.performAttackEffects(target);
        super.checkAndPerformAttack(target);
        lastAttackTick = hero.tickCount;
    }

    @Override
    protected boolean isTimeToAttack() {
        return hero.tickCount - lastAttackTick >= hero.getAttack().cooldownTicks();
    }

    private void switchToThreatInReach() {
        LivingEntity target = hero.getTarget();
        if (target == null || !isTimeToAttack() || canReach(target)) {
            return;
        }
        hero.findClosestThreat(REACH_SEARCH_RADIUS, this::canReach).ifPresent(hero::setTarget);
    }

    private void keepChasing() {
        LivingEntity target = hero.getTarget();
        if (target == null || hero.tickCount % CHASE_INTERVAL_TICKS != 0) return;
        if (!hero.getNavigation().isDone() || canReach(target)) return;

        hero.getNavigation().moveTo(target, speedModifier);
    }

    private boolean canReach(LivingEntity entity) {
        return hero.isWithinMeleeAttackRange(entity) && hero.getSensing().hasLineOfSight(entity);
    }
}
