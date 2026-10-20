package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import java.util.EnumSet;

public class EngageThreatsGoal extends TargetGoal {

    private static final double SEARCH_RADIUS = 8.0D;

    private final HeroEntity hero;
    private Mob threat;

    public EngageThreatsGoal(final HeroEntity hero) {
        super(hero, false);
        this.hero = hero;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        this.threat = hero.findClosestThreat(SEARCH_RADIUS, mob -> true).orElse(null);
        return threat != null;
    }

    @Override
    public void start() {
        hero.setTarget(threat);
        super.start();
    }
}
