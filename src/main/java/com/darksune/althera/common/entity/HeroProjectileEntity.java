package com.darksune.althera.common.entity;

import com.darksune.althera.common.ultimate.effect.UltimateEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import java.util.List;

public class HeroProjectileEntity extends Projectile {

    private static final EntityDataAccessor<ParticleOptions> PARTICLE =
            SynchedEntityData.defineId(
                    HeroProjectileEntity.class,
                    EntityDataSerializers.PARTICLE
            );
    private static final int MAX_LIFE_TICKS = 40;

    private float damage;
    private List<UltimateEffect> onHit = List.of();

    public HeroProjectileEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static Vec3 aimAt(final Vec3 from, final LivingEntity target) {
        return target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(from);
    }

    public static HeroProjectileEntity shoot(
            final CasterMob hero,
            final Vec3 from,
            final Vec3 direction,
            final float damage,
            final float speed,
            final ParticleOptions particle,
            final List<UltimateEffect> onHit
    ) {
        final HeroProjectileEntity projectile = AltheraEntities.HERO_PROJECTILE.get().create(hero.level());
        if (projectile == null) return null;

        projectile.setOwner(hero);
        projectile.damage = damage;
        projectile.onHit = onHit;
        projectile.entityData.set(PARTICLE, particle);
        projectile.moveTo(from.x, from.y, from.z, hero.getYRot(), 0);
        projectile.shoot(direction.x, direction.y, direction.z, speed, 0.0f);
        hero.level().addFreshEntity(projectile);
        return projectile;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PARTICLE, ParticleTypes.FLAME);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (tickCount > MAX_LIFE_TICKS || !(getOwner() instanceof CasterMob)) {
                discard();
                return;
            }

            final HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hit.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hit)) {
                hitTargetOrDeflectSelf(hit);
            }

            if (isRemoved()) return;
        }

        final Vec3 motion = getDeltaMovement();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        updateRotation();

        if (level().isClientSide) {
            spawnTrail(motion);
        }
    }

    private void spawnTrail(final Vec3 motion) {
        final ParticleOptions particle = entityData.get(PARTICLE);
        for (int i = 0; i < 2; i++) {
            final double back = i * 0.5;
            level().addParticle(
                    particle,
                    getX() - motion.x * back,
                    getY() - motion.y * back,
                    getZ() - motion.z * back,
                    (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.02
            );
        }
    }

    @Override
    protected boolean canHitEntity(final Entity target) {
        if (!(target instanceof LivingEntity) || !super.canHitEntity(target)) {
            return false;
        }
        if (!(getOwner() instanceof CasterMob hero)) {
            return true;
        }
        return target != hero
                && !hero.isAlliedTo(target)
                && (!(target instanceof Player player) || hero.canHarmPlayer(player));
    }

    @Override
    protected void onHitEntity(final EntityHitResult result) {
        super.onHitEntity(result);
        final Entity owner = getOwner();
        final LivingEntity attacker = owner instanceof LivingEntity living ? living : null;
        final boolean hurt = result.getEntity().hurt(damageSources().mobProjectile(this, attacker), damage);
        if (hurt && owner instanceof CasterMob hero && result.getEntity() instanceof LivingEntity target) {
            hero.applyEffects(onHit, target, result.getLocation());
        }
    }

    @Override
    protected void onHitBlock(final BlockHitResult result) {
        super.onHitBlock(result);
        if (getOwner() instanceof CasterMob hero) {
            hero.applyEffects(onHit, null, result.getLocation());
        }
    }

    @Override
    protected void onHit(final HitResult result) {
        super.onHit(result);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(entityData.get(PARTICLE), getX(), getY(), getZ(), 8, 0.15, 0.15, 0.15, 0.02);
            discard();
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
