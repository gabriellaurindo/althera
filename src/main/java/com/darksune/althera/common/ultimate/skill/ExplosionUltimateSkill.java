package com.darksune.althera.common.ultimate.skill;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExplosionUltimateSkill
        extends AbstractUltimateSkill {

    private static final double RADIUS = 6.0;

    private static final float DAMAGE = 12.0F;

    private static final int DURATION_TICKS = 20;

    private static final int COOLDOWN_TICKS = 20;

    @Override
    public void execute(Player player, HeroEntity heroEntity) {
        heroEntity.addEffect(new MobEffectInstance(
                MobEffects.GLOWING,
                DURATION_TICKS,
                0,
                false,
                false,
                true
        ));

        heroEntity.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                DURATION_TICKS,
                4,
                false,
                false,
                true
        ));

        heroEntity.level().playSound(
                null,
                heroEntity.blockPosition(),
                SoundEvents.BEACON_POWER_SELECT,
                SoundSource.HOSTILE,
                2.0F,
                0.5F
        );
    }

    @Override
    public void onExpire(Player player, HeroEntity heroEntity) {
        super.onExpire(player, heroEntity);

        List<LivingEntity> entities =
                heroEntity.level().getEntitiesOfClass(
                        LivingEntity.class,
                        heroEntity.getBoundingBox()
                                .inflate(RADIUS)
                );

        for (LivingEntity entity : entities) {

            if (entity == heroEntity) {
                continue;
            }

            if (entity == player) {
                continue;
            }

            double distanceSqr =
                    entity.distanceToSqr(heroEntity);

            if (distanceSqr > RADIUS * RADIUS) {
                continue;
            }

            double dx =
                    entity.getX() - heroEntity.getX();

            double dz =
                    entity.getZ() - heroEntity.getZ();

            entity.push(
                    dx * 0.35,
                    0.6,
                    dz * 0.35
            );

            entity.setRemainingFireTicks(60);

            entity.hurt(
                    heroEntity.damageSources()
                            .mobAttack(heroEntity),
                    DAMAGE
            );
        }

        heroEntity.level().explode(
                heroEntity,
                heroEntity.getX(),
                heroEntity.getY(),
                heroEntity.getZ(),
                0.0F,
                Level.ExplosionInteraction.NONE
        );

        ServerLevel serverLevel =
                (ServerLevel) heroEntity.level();

        serverLevel.sendParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                heroEntity.getX(),
                heroEntity.getY() + 1,
                heroEntity.getZ(),
                1,
                0,
                0,
                0,
                0
        );

        serverLevel.sendParticles(
                ParticleTypes.FLAME,
                heroEntity.getX(),
                heroEntity.getY() + 1,
                heroEntity.getZ(),
                80,
                1.5,
                1.0,
                1.5,
                0.05
        );

        serverLevel.sendParticles(
                ParticleTypes.SMOKE,
                heroEntity.getX(),
                heroEntity.getY() + 1,
                heroEntity.getZ(),
                50,
                1.2,
                0.8,
                1.2,
                0.03
        );

        heroEntity.level().playSound(
                null,
                heroEntity.getX(),
                heroEntity.getY(),
                heroEntity.getZ(),
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.HOSTILE,
                3.0F,
                0.8F
        );
    }

    @Override
    public int getCooldownTicks() {
        return COOLDOWN_TICKS;
    }

    @Override
    public int getManaCost() {
        return 200;
    }

    @Override
    public int getDurationTicks() {
        return DURATION_TICKS;
    }
}
