package com.darksune.althera.common.ultimate.effect;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public record UltimateContext(Player owner, HeroEntity hero, Vec3 origin, Vec3 direction) {

    private static final double[] CLEARANCE_HEIGHTS = {0.25, 0.9};

    private static final double AIM_RANGE = 32.0;

    public static UltimateContext of(Player owner, HeroEntity hero) {
        return new UltimateContext(owner, hero, hero.position(), facing(owner, hero));
    }

    public ServerLevel level() {
        return (ServerLevel) hero.level();
    }

    public Vec3 reach(double distance) {

        double clearDistance = distance;

        for (double clearance : CLEARANCE_HEIGHTS) {

            Vec3 from = origin.add(0, hero.getBbHeight() * clearance, 0);

            BlockHitResult hit = level().clip(new ClipContext(
                    from,
                    from.add(direction.scale(distance)),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    hero
            ));

            if (hit.getType() != HitResult.Type.MISS) {
                clearDistance = Math.min(clearDistance, hit.getLocation().distanceTo(from) - hero.getBbWidth());
            }
        }

        return origin.add(direction.scale(Math.max(0, clearDistance)));
    }

    private static Vec3 facing(Player owner, HeroEntity hero) {

        if (owner.level() == hero.level()) {
            Vec3 toAim = aimPoint(owner, hero).subtract(hero.position()).multiply(1, 0, 1);
            if (toAim.lengthSqr() > 0.25) {
                return toAim.normalize();
            }
        }

        Vec3 look = owner.getLookAngle().multiply(1, 0, 1);

        if (look.lengthSqr() > 1.0E-4) {
            return look.normalize();
        }

        return Vec3.directionFromRotation(0, hero.getYRot());
    }

    private static Vec3 aimPoint(Player owner, HeroEntity hero) {

        Vec3 eye = owner.getEyePosition();

        Vec3 blockEnd = owner.level().clip(new ClipContext(
                eye,
                eye.add(owner.getLookAngle().scale(AIM_RANGE)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                owner
        )).getLocation();

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                owner,
                eye,
                blockEnd,
                new AABB(eye, blockEnd).inflate(1.0),
                entity -> entity instanceof LivingEntity && entity != hero && entity.isPickable() && !entity.isSpectator(),
                eye.distanceToSqr(blockEnd)
        );

        return entityHit != null ? entityHit.getEntity().position() : blockEnd;
    }
}
