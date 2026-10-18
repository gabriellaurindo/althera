package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class HeroMeleeAttackGoal extends MeleeAttackGoal {

    private static final double REACH_SEARCH_RADIUS = 2.0D;

    private final HeroEntity hero;

    public HeroMeleeAttackGoal(HeroEntity hero, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(hero, speedModifier, followingTargetEvenIfNotSeen);
        this.hero = hero;
    }

    @Override
    public void tick() {
        switchToThreatInReach();
        super.tick();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target)) {
            hero.triggerAttackAnimation();
        }
        super.checkAndPerformAttack(target);
    }

    private void switchToThreatInReach() {
        LivingEntity target = hero.getTarget();
        if (target == null || !isTimeToAttack() || canReach(target)) {
            return;
        }
        hero.findClosestThreat(REACH_SEARCH_RADIUS, this::canReach).ifPresent(hero::setTarget);
    }

    private boolean canReach(LivingEntity entity) {
        return hero.isWithinMeleeAttackRange(entity) && hero.getSensing().hasLineOfSight(entity);
    }
}
