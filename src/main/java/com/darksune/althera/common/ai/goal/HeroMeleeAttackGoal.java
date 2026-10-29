package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.Nullable;

public class HeroMeleeAttackGoal extends MeleeAttackGoal {

    private static final double REACH_SEARCH_RADIUS = 2.0D;
    private static final int CHASE_INTERVAL_TICKS = 5;
    private static final int BLINK_AFTER_TICKS = 100;
    private static final double PROGRESS_DISTANCE = 1.0D;

    private final HeroEntity hero;
    private final double speedModifier;

    private int lastAttackTick = Integer.MIN_VALUE / 2;
    @Nullable
    private LivingEntity trackedTarget;
    private double bestDistance;
    private int progressTick;

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
    public void start() {
        super.start();
        trackedTarget = null;
    }

    @Override
    public void tick() {
        switchToThreatInReach();
        super.tick();
        keepChasing();
        blinkIfStuck();
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
        resetProgress();
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

    private void blinkIfStuck() {
        LivingEntity target = hero.getTarget();
        if (target == null) return;

        if (target != trackedTarget) {
            trackedTarget = target;
            resetProgress();
        }

        double distance = hero.distanceTo(target);
        if (canReach(target) || distance < bestDistance - PROGRESS_DISTANCE) {
            bestDistance = distance;
            progressTick = hero.tickCount;
            return;
        }

        if (hero.tickCount - progressTick < BLINK_AFTER_TICKS) return;

        hero.blinkTo(target);
        resetProgress();
    }

    private void resetProgress() {
        bestDistance = Double.MAX_VALUE;
        progressTick = hero.tickCount;
    }

    private boolean canReach(LivingEntity entity) {
        return hero.isWithinMeleeAttackRange(entity) && hero.getSensing().hasLineOfSight(entity);
    }
}
