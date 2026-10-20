package com.darksune.althera.common.ai.goal;

import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.entity.HeroProjectileEntity;
import com.darksune.althera.common.hero.HeroAttack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class HeroRangedAttackGoal extends Goal {

    private static final double MIN_DISTANCE_RATIO = 0.5D;
    private static final int RETREAT_DISTANCE = 6;
    private static final int RETREAT_HEIGHT = 3;
    private static final int REPATH_TICKS = 10;

    private final HeroEntity hero;
    private final double speedModifier;

    private int cooldown;
    private int repathDelay;
    private int pendingShotTicks;

    public HeroRangedAttackGoal(HeroEntity hero, double speedModifier) {
        this.hero = hero;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        final LivingEntity target = hero.getTarget();
        return target != null && target.isAlive() && hero.getAttack().isRanged();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && hero.isWithinRestriction(hero.getTarget().blockPosition());
    }

    @Override
    public void start() {
        hero.setAggressive(true);
    }

    @Override
    public void stop() {
        pendingShotTicks = 0;
        hero.setAggressive(false);
        hero.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        final LivingEntity target = hero.getTarget();
        if (target == null) return;

        final HeroAttack attack = hero.getAttack();
        final double distance = hero.distanceTo(target);
        final boolean canSee = hero.getSensing().hasLineOfSight(target);

        hero.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (!canSee || distance > attack.range()) {
            chase(target);
        } else if (distance < attack.range() * MIN_DISTANCE_RATIO) {
            retreatFrom(target);
        } else {
            hero.getNavigation().stop();
            repathDelay = 0;
        }

        if (pendingShotTicks > 0 && --pendingShotTicks == 0) {
            shoot(target, attack);
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (canSee && distance <= attack.range()) {
            hero.triggerAttackAnimation();
            cooldown = attack.cooldownTicks();
            pendingShotTicks = hero.getAnimation().attackFrameTicks(attack.shootFrame());
            if (pendingShotTicks == 0) {
                shoot(target, attack);
            }
        }
    }

    private void chase(final LivingEntity target) {
        if (--repathDelay > 0) return;

        hero.getNavigation().moveTo(target, speedModifier);
        repathDelay = REPATH_TICKS;
    }

    private void retreatFrom(final LivingEntity target) {
        if (!hero.getNavigation().isDone()) return;

        final Vec3 away = DefaultRandomPos.getPosAway(hero, RETREAT_DISTANCE, RETREAT_HEIGHT, target.position());
        if (away != null) {
            hero.getNavigation().moveTo(away.x, away.y, away.z, speedModifier);
        }
    }

    private void shoot(final LivingEntity target, final HeroAttack attack) {
        hero.performAttackEffects(target);
        final Vec3 from = hero.getProjectileOrigin();
        HeroProjectileEntity.shoot(
                hero,
                from,
                HeroProjectileEntity.aimAt(from, target),
                (float) hero.getAttributeValue(Attributes.ATTACK_DAMAGE),
                attack.projectileSpeed(),
                attack.projectileTrail(),
                attack.onHit()
        );
    }
}
