package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.EnemyMageEntity;
import com.darksune.althera.common.spell.SpellDefinition;
import com.darksune.althera.common.ultimate.effect.UltimateContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SpellAttackGoal extends Goal {

    private static final double MIN_DISTANCE_RATIO = 0.5D;
    private static final int RETREAT_DISTANCE = 6;
    private static final int RETREAT_HEIGHT = 3;
    private static final int REPATH_TICKS = 10;

    private final EnemyMageEntity mage;
    private final double speedModifier;

    private int cooldown;
    private int repathDelay;
    private int pendingCastTicks;

    public SpellAttackGoal(EnemyMageEntity mage, double speedModifier) {
        this.mage = mage;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        final LivingEntity target = mage.getTarget();
        return target != null && target.isAlive() && mage.getSpell() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && mage.isWithinRestriction(mage.getTarget().blockPosition());
    }

    @Override
    public void start() {
        mage.setAggressive(true);
    }

    @Override
    public void stop() {
        pendingCastTicks = 0;
        mage.setAggressive(false);
        mage.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        final LivingEntity target = mage.getTarget();
        final SpellDefinition spell = mage.getSpell();
        if (target == null || spell == null) return;

        final double distance = mage.distanceTo(target);
        final boolean canSee = mage.getSensing().hasLineOfSight(target);

        mage.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (!canSee || distance > spell.range()) {
            chase(target);
        } else if (distance < spell.range() * MIN_DISTANCE_RATIO) {
            retreatFrom(target);
        } else {
            mage.getNavigation().stop();
            repathDelay = 0;
        }

        if (pendingCastTicks > 0 && --pendingCastTicks == 0) {
            cast(target, spell);
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (canSee && distance <= spell.range()) {
            mage.startCastPose(spell.castTicks());
            cooldown = spell.cooldownTicks();
            pendingCastTicks = spell.castTicks();
            if (pendingCastTicks == 0) {
                cast(target, spell);
            }
        }
    }

    private void chase(final LivingEntity target) {
        if (--repathDelay > 0) return;

        mage.getNavigation().moveTo(target, speedModifier);
        repathDelay = REPATH_TICKS;
    }

    private void retreatFrom(final LivingEntity target) {
        if (!mage.getNavigation().isDone()) return;

        final Vec3 away = DefaultRandomPos.getPosAway(mage, RETREAT_DISTANCE, RETREAT_HEIGHT, target.position());
        if (away != null) {
            mage.getNavigation().moveTo(away.x, away.y, away.z, speedModifier);
        }
    }

    private void cast(final LivingEntity target, final SpellDefinition spell) {
        spell.cast(UltimateContext.attack(mage, mage, target, null));
    }
}
