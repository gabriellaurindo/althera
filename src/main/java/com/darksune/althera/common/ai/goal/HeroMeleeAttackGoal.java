package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class HeroMeleeAttackGoal extends MeleeAttackGoal {

    private final HeroEntity hero;

    public HeroMeleeAttackGoal(HeroEntity hero, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(hero, speedModifier, followingTargetEvenIfNotSeen);
        this.hero = hero;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target)) {
            hero.triggerAttackAnimation();
        }
        super.checkAndPerformAttack(target);
    }
}
