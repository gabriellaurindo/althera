package com.darksune.althera.common.event;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroProgressionSystem;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class CombatEvents {

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof HeroEntity hero)) return;

        final Player owner = hero.getOwner();

        if (owner == null) {
            return;
        }

        HeroProgressionSystem.addXp(owner, event);
    }

    @SubscribeEvent
    public static void onPlayerDamage(final LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getSource().getEntity() == null) {
            return;
        }

        final HeroData heroData = HeroData.get(player);
        if (heroData.getInterventions() >= HeroStatsSystem.getMaxInterventions()) {
            return;
        }

        if (heroData.isSaveDisabled()) {
            return;
        }

        final HeroEntity hero = HeroSummonSystem.spawnOrMove(player);
        if (hero == null) {
            return;
        }

        float damage = event.getAmount();
        if (damage <= 0) {
            return;
        }

        hero.hurt(event.getSource(), damage);

        heroData.incrementInterventions();
        heroData.sync(player);

        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof Mob mob) {

            // Force aggro onto summon
            mob.setTarget(hero);

            // Optional: clear current navigation briefly
            mob.getNavigation().stop();
        }

        // Cancel original damage
        event.setCanceled(true);

        // Small invulnerability window
        player.invulnerableTime = 20;

        // =====================================
        // KNOCKBACK ATTACKER
        // =====================================

        if (attacker instanceof LivingEntity livingAttacker) {

            double dx = livingAttacker.getX() - player.getX();
            double dz = livingAttacker.getZ() - player.getZ();

            double length = Math.sqrt(dx * dx + dz * dz);

            if (length > 0.001D) {

                dx /= length;
                dz /= length;

                // Push attacker backwards
                livingAttacker.push(dx * 1.2D, 0.25D, dz * 1.2D);

                livingAttacker.hurtMarked = true;
            }
        }

        // =====================================
        // SOUND
        // =====================================

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                0.25F,
                1.4F
        );
    }

    @SubscribeEvent
    public static void onHeroDamage(LivingIncomingDamageEvent event) {

        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getEntity() instanceof HeroEntity hero)) return;

        if (hero.isOwnedBy(player)) {
            event.setCanceled(true);
        }
    }
}
