package com.darksune.althera.common.ultimate.effect;

import com.darksune.althera.common.entity.SpellCaster;
import com.darksune.althera.common.spell.SpellScale;
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
import org.jetbrains.annotations.Nullable;

public record UltimateContext(LivingEntity owner, SpellCaster caster, Vec3 origin, Vec3 direction,
                              @Nullable LivingEntity target, @Nullable Vec3 impact, SpellScale scale) {

    private static final double[] CLEARANCE_HEIGHTS = {0.25, 0.9};

    private static final double AIM_RANGE = 32.0;

    public static UltimateContext of(LivingEntity owner, SpellCaster hero) {
        return new UltimateContext(owner, hero, hero.entity().position(), facing(owner, hero.entity()), null, null, SpellScale.NONE);
    }

    public static UltimateContext attack(LivingEntity owner, SpellCaster hero, @Nullable LivingEntity target, @Nullable Vec3 impact) {
        return attack(owner, hero, target, impact, SpellScale.NONE);
    }

    public static UltimateContext attack(LivingEntity owner, SpellCaster hero, @Nullable LivingEntity target,
                                         @Nullable Vec3 impact, SpellScale scale) {

        LivingEntity entity = hero.entity();

        Vec3 toTarget = target == null ? Vec3.ZERO : target.position().subtract(entity.position()).multiply(1, 0, 1);

        Vec3 direction = toTarget.lengthSqr() > 1.0E-4
                ? toTarget.normalize()
                : Vec3.directionFromRotation(0, entity.getYRot());

        return new UltimateContext(owner, hero, entity.position(), direction, target, impact, scale);
    }

    public static UltimateContext spell(SpellCaster caster, SpellScale scale) {
        LivingEntity entity = caster.entity();
        return new UltimateContext(entity, caster, entity.position(), entity.getLookAngle(), null, null, scale);
    }

    public Vec3 position() {
        return impact != null ? impact : caster.entity().position();
    }

    public boolean canAffect(LivingEntity entity) {
        return entity != caster.entity()
                && entity.isAlive()
                && !caster.isAllyOf(entity)
                && !(entity instanceof Player player && !caster.canHarmPlayer(player));
    }

    public ServerLevel level() {
        return (ServerLevel) caster.entity().level();
    }

    public Vec3 reach(double distance) {

        double clearDistance = distance;

        for (double clearance : CLEARANCE_HEIGHTS) {

            Vec3 from = origin.add(0, caster.entity().getBbHeight() * clearance, 0);

            BlockHitResult hit = level().clip(new ClipContext(
                    from,
                    from.add(direction.scale(distance)),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    caster.entity()
            ));

            if (hit.getType() != HitResult.Type.MISS) {
                clearDistance = Math.min(clearDistance, hit.getLocation().distanceTo(from) - caster.entity().getBbWidth());
            }
        }

        return origin.add(direction.scale(Math.max(0, clearDistance)));
    }

    private static Vec3 facing(LivingEntity owner, LivingEntity hero) {

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

    private static Vec3 aimPoint(LivingEntity owner, LivingEntity hero) {

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
