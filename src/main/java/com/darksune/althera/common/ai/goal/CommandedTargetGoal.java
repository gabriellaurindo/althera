package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CommandedTargetGoal extends Goal {

    private final HeroEntity hero;

    public CommandedTargetGoal(final HeroEntity hero) {
        this.hero = hero;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        return hero.getCommandedTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        keepTarget();
    }

    @Override
    public void tick() {
        keepTarget();
    }

    @Override
    public void stop() {
        hero.setTarget(null);
    }

    private void keepTarget() {
        final LivingEntity target = hero.getCommandedTarget();
        if (target != null && hero.getTarget() != target) {
            hero.setTarget(target);
        }
    }
}
