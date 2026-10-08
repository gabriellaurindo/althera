package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.commandseal.CommandSealSystem;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.skill.SkillEndReason;
import com.darksune.althera.common.ultimate.UltimateSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

import static com.darksune.althera.common.util.LightOrbUtil.desabilitarEspirito;
import static com.darksune.althera.common.util.LightOrbUtil.garantirEspirito;
import static com.darksune.althera.common.util.LightOrbUtil.habilitarEspirito;

public class HeroSummonSystem {

    // beyond this the hero is probably in a non-ticking chunk (the hero teleports by itself from 30 on)
    private static final double RECALL_DISTANCE = 48;

    public static HeroEntity getSummon(final Player player) {
        final UUID uuid = HeroData.get(player).getSummonUUID();

        if (uuid == null) return null;

        for (ServerLevel level : player.getServer().getAllLevels()) {
            final Entity entity = level.getEntity(uuid);

            if (entity instanceof HeroEntity hero) {
                return hero;
            }
        }

        return null;
    }

    public static void toggleSummon(final Player player) {
        if (getSummon(player) != null) {
            dismissSummon(player);
            return;
        }
        spawnSummon(player);
    }

    public static HeroEntity spawnSummon(final Player player) {
        return spawnSummon(player, true);
    }

    public static HeroEntity spawnSummon(final Player player, final boolean sendMessage) {
        return spawnSummon(player, sendMessage, true);
    }

    /**
     * Makes sure the player has a summon in the world. If one already exists, returns it.
     *
     * @param checkMana false when the caller already charged its own cost (e.g. revive skill)
     */
    public static HeroEntity spawnSummon(final Player player, final boolean sendMessage, final boolean checkMana) {
        final HeroData heroData = HeroData.get(player);
        if (heroData.getHeroDefinition() == null) {
            heroData.setHero(HeroRollSystem.rollHero().getId());
            heroData.setHealth(HeroStatsSystem.getMaxHealth(heroData));
            //todo in the future the ritual may be required to get a summon
//            player.sendSystemMessage(
//                    Component.literal("§eYou don't have a summon.")
//            );
//            return null;
        }

        final HeroEntity current = getSummon(player);

        if (current != null) {
            return current;
        }

        if (heroData.getSummonUUID() != null) {
            // reference to a hero that no longer exists: go back to the "not summoned" state
            heroData.clearSummon();
            heroData.sync(player);
            habilitarEspirito(player);
        }

        if (heroData.isDefeated()) {
            if (sendMessage) {
                player.sendSystemMessage(Component.literal("§eYour summon is defeated. Wait until it recovers."));
            }
            return null;
        }

        final ManaData manaData = ManaData.get(player);
        if (checkMana && !manaData.hasEnoughMana(20)) {
            if (sendMessage) {
                player.sendSystemMessage(Component.literal("Not enough mana!"));
            }
            return null;
        }

        final HeroEntity entity = HeroEntity.create(player);

        moveTo(player, entity);

        player.level().addFreshEntity(entity);

        heroData.setSummonUUID(entity.getUUID());
        heroData.sync(player);
        desabilitarEspirito(player);
        return entity;
    }

    /**
     * Dismisses the summon (player, lack of mana...). Active skills end with
     * {@link SkillEndReason#HERO_DISMISSED} and each one applies its own consequence.
     */
    public static void dismissSummon(final Player player) {
        final HeroEntity summon = getSummon(player);

        if (summon != null) {
            summon.remove();
        } else if (HeroData.get(player).isSummoned()) {
            // hero outside loaded chunks: clear the reference; the hero discards itself once loaded
            final HeroData heroData = HeroData.get(player);
            heroData.clearSummon();
            heroData.sync(player);
            habilitarEspirito(player);
        }

        endActiveSkills(player, null, SkillEndReason.HERO_DISMISSED);
    }

    /**
     * Ensures the hero's presence next to the player: the summon nearby if summoned, otherwise an active spirit.
     * Called on events (login, respawn, dimension change) and periodically on the player tick, which covers
     * any teleport (/tp, ender pearl, mods) that leaves the hero or spirit in a chunk that no longer ticks entities.
     */
    public static void restorePresence(final Player player) {
        final HeroData heroData = HeroData.get(player);

        if (heroData.getHeroDefinition() == null) {
            return;
        }

        if (heroData.isSummoned()) {
            final HeroEntity summon = getSummon(player);

            // nearby and in the same dimension: the hero handles following the owner itself
            if (summon != null && summon.level() == player.level() && summon.distanceTo(player) <= RECALL_DISTANCE) {
                return;
            }

            if (spawnOrMove(player) != null) {
                return;
            }
        }

        garantirEspirito(player);
    }

    /**
     * Defeats the summon. If the entity is alive it is killed, and the flow continues
     * through {@link HeroEntity#die} -> {@link #handleDefeat}.
     */
    public static void defeatSummon(final Player player) {
        final HeroEntity summon = getSummon(player);

        if (summon != null && summon.isAlive()) {
            summon.kill();
            return;
        }

        handleDefeat(player, null);
    }

    /**
     * Single defeat entry point: updates the data, enables the spirit and ends active skills.
     */
    public static void handleDefeat(final Player player, @Nullable final HeroEntity hero) {
        final HeroData heroData = HeroData.get(player);
        heroData.clearSummon();
        heroData.setDefeated(true);
        heroData.setHealth(0);
        heroData.sync(player);
        habilitarEspirito(player);

        endActiveSkills(player, hero, SkillEndReason.HERO_DEFEATED);

        player.sendSystemMessage(Component.literal("§cYour summon has been defeated! It will recover over time."));
    }

    /**
     * Unsticks a summon in an inconsistent state: removes every loaded hero owned by the player
     * and applies the defeat (zero health, active spirit, regenerates over time). Level, xp and hero stay intact.
     */
    public static void unstuckSummon(final Player player) {
        for (ServerLevel level : player.getServer().getAllLevels()) {
            for (HeroEntity hero : level.getEntities(AltheraEntities.HERO.get(), hero -> hero.isOwnedBy(player))) {
                hero.discard();
            }
        }

        handleDefeat(player, null);
    }

    public static HeroEntity spawnOrMove(final Player player) {
        final HeroEntity summon = HeroSummonSystem.getSummon(player);
        if (summon == null) {
            return HeroSummonSystem.spawnSummon(player, false);
        }
        return moveTo(player, summon);
    }

    private static void endActiveSkills(final Player player, @Nullable final HeroEntity hero, final SkillEndReason reason) {
        CommandSealSystem.endActiveSkills(player, hero, reason);
        UltimateSystem.endActiveSkills(player, hero, reason);
    }

    private static HeroEntity moveTo(final Player player, final HeroEntity entity) {
        if (entity.level() == player.level() || !(player.level() instanceof ServerLevel targetLevel)) {
            entity.moveTo(
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    player.getYRot(),
                    0
            );
            return entity;
        }

        // another dimension: teleportTo recreates the entity in the target level with the same UUID,
        // keeping health, effects and attributes
        entity.teleportTo(
                targetLevel,
                player.getX(),
                player.getY(),
                player.getZ(),
                Set.of(),
                player.getYRot(),
                0
        );
        return getSummon(player);
    }
}
