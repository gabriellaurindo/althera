package com.darksune.althera.common.system;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class HeroCommandSystem {

    public static final double MARK_RANGE = 32.0D;
    private static final double MARK_RANGE_TOLERANCE = 4.0D;

    private HeroCommandSystem() {}

    public static void markTarget(final ServerPlayer player, final int entityId) {
        final Entity entity = player.level().getEntity(entityId);
        if (!(entity instanceof LivingEntity target) || !target.isAlive()) return;

        final double maxRange = MARK_RANGE + MARK_RANGE_TOLERANCE;
        if (player.distanceToSqr(target) > maxRange * maxRange || !player.hasLineOfSight(target)) return;

        final HeroEntity hero = HeroSummonSystem.getSummon(player);
        if (hero == null || !hero.isAlive() || hero.level() != player.level()) {
            player.sendSystemMessage(Component.literal("§eSummon your hero to mark a target."));
            return;
        }

        if (target == hero || !hero.canAttack(target)) {
            player.sendSystemMessage(Component.literal("§cYour hero can't attack that target."));
            return;
        }

        hero.markTarget(target);
        showMark(player, target);
    }

    private static void showMark(final ServerPlayer player, final LivingEntity target) {
        final ServerLevel level = player.serverLevel();
        final double spread = target.getBbWidth() * 0.5D;

        level.sendParticles(player, ParticleTypes.ENCHANTED_HIT, true,
                target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                20, spread, target.getBbHeight() * 0.4D, spread, 0.1D);
        level.sendParticles(player, ParticleTypes.CRIT, true,
                target.getX(), target.getY() + target.getBbHeight() + 0.3D, target.getZ(),
                8, 0.1D, 0.1D, 0.1D, 0.05D);
        player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.4F);
    }
}
