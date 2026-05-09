package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {

    private final HeroEntity hero;

    private final double speedModifier;

    private final float startDistance;

    private final float stopDistance;

    public FollowOwnerGoal(
            HeroEntity hero,
            double speedModifier,
            float startDistance,
            float stopDistance
    ) {

        this.hero = hero;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;

        this.setFlags(EnumSet.of(
                Flag.MOVE,
                Flag.LOOK
        ));
    }

    @Override
    public boolean canUse() {

        Player owner = hero.getOwner();

        if (owner == null) {
            return false;
        }

        // não segue durante combate
        if (hero.getTarget() != null) {
            return false;
        }

        return hero.distanceTo(owner) > startDistance;
    }

    @Override
    public boolean canContinueToUse() {

        Player owner = hero.getOwner();

        if (owner == null) {
            return false;
        }

        if (hero.getTarget() != null) {
            return false;
        }

        return hero.distanceTo(owner) > stopDistance;
    }

    @Override
    public void tick() {

        Player owner = hero.getOwner();

        if (owner == null) {
            return;
        }

        hero.getLookControl().setLookAt(
                owner,
                30.0F,
                30.0F
        );

        hero.getNavigation().moveTo(
                owner,
                speedModifier
        );
    }
}