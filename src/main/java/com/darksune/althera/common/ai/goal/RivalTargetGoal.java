package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import java.util.EnumSet;

public class RivalTargetGoal extends TargetGoal {

    private static final double RANGE = 32.0D;
    private static final int CHECK_INTERVAL = 10;

    private final HeroEntity hero;
    private LivingEntity rival;

    public RivalTargetGoal(final HeroEntity hero) {
        super(hero, false);
        this.hero = hero;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (hero.tickCount % CHECK_INTERVAL != 0) return false;

        this.rival = hero.findRival(RANGE);
        return rival != null && rival != hero.getTarget();
    }

    @Override
    public void start() {
        hero.setTarget(rival);
        super.start();
    }
}
