package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.commandseal.CommandSealSystem;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.skill.SkillEndReason;
import com.darksune.althera.common.ultimate.UltimateData;
import com.darksune.althera.common.ultimate.UltimateSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.darksune.althera.common.util.LightOrbUtil.desabilitarEspirito;
import static com.darksune.althera.common.util.LightOrbUtil.garantirEspirito;
import static com.darksune.althera.common.util.LightOrbUtil.habilitarEspirito;

public class HeroSummonSystem {

    // beyond this the hero is probably in a non-ticking chunk (the hero teleports by itself from 30 on)
    private static final double RECALL_DISTANCE = 48;
    private static final int SUMMON_MANA_COST = 20;
    private static final int DISGUISE_MANA_DIVISOR = 4;

    public static int getManaCost(final HeroData heroData) {
        return heroData.isDisguised() ? SUMMON_MANA_COST / DISGUISE_MANA_DIVISOR : SUMMON_MANA_COST;
    }

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

    public static void refreshSummon(final Player player) {
        final HeroEntity summon = getSummon(player);
        if (summon == null) return;

        final HeroDefinition definition = HeroData.get(player).getHeroDefinition();
        if (definition != null) {
            summon.setHeroId(definition.getId());
        }
        HeroStatsSystem.applyAttributes(summon, player);
    }

    public static void toggleSummon(final Player player) {
        if (getSummon(player) != null) {
            dismissSummon(player);
            return;
        }
        spawnSummon(player);
    }

    /**
     * Hold-summon key: summons the hero disguised, or swaps a summoned hero between its real form and the disguise.
     */
    public static void toggleDisguise(final Player player) {
        final HeroData heroData = HeroData.get(player);
        final HeroEntity summon = getSummon(player);

        if (summon == null) {
            if (heroData.isSummoned()) return;
            setDisguised(heroData, true);
            final HeroEntity disguised = spawnSummon(player);
            if (disguised != null) {
                disguised.playDisguiseEffects();
                player.sendSystemMessage(Component.literal("§7Your hero answers your call disguised."));
            }
            return;
        }

        if (heroData.isDisguised()) {
            summon.startReveal();
            return;
        }

        if (!UltimateData.get(player).getActiveSkills().isEmpty()) {
            player.sendSystemMessage(Component.literal("§eYour hero can't disguise while channeling its ultimate."));
            return;
        }

        changeDisguise(player, summon, true);
        summon.playDisguiseEffects();
        player.sendSystemMessage(Component.literal("§7Your hero hides its identity."));
    }

    /**
     * Called by the hero once its reveal effect ends.
     */
    public static void finishReveal(final Player player, final HeroEntity summon) {
        if (!HeroData.get(player).isDisguised()) return;

        changeDisguise(player, summon, false);
        player.sendSystemMessage(Component.literal("§7Your hero reveals its true form."));
    }

    private static void changeDisguise(final Player player, final HeroEntity summon, final boolean disguised) {
        final HeroData heroData = HeroData.get(player);
        heroData.setHealth(summon.getHealth());
        setDisguised(heroData, disguised);
        HeroStatsSystem.applyAttributes(summon, player);
        heroData.sync(player);
    }

    /**
     * Keeps the health ratio when the disguise changes the max health.
     */
    private static void setDisguised(final HeroData heroData, final boolean disguised) {
        if (heroData.isDisguised() == disguised) return;

        final double maxHealth = HeroStatsSystem.getMaxHealth(heroData);
        final double ratio = maxHealth > 0 ? heroData.getHealth() / maxHealth : 0;

        heroData.setDisguised(disguised);
        heroData.setHealth(ratio * HeroStatsSystem.getMaxHealth(heroData));
    }

    private static void endDisguise(final Player player) {
        final HeroData heroData = HeroData.get(player);
        if (!heroData.isDisguised()) return;

        setDisguised(heroData, false);
        heroData.sync(player);
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
        final HeroEntity summon = createSummon(player, sendMessage, checkMana);
        if (summon == null && !HeroData.get(player).isSummoned()) {
            endDisguise(player);
        }
        return summon;
    }

    private static HeroEntity createSummon(final Player player, final boolean sendMessage, final boolean checkMana) {
        final HeroData heroData = HeroData.get(player);
        if (HeroSelectionSystem.needsSelection(player)) {
            if (sendMessage) {
                HeroSelectionSystem.requestSelection(player);
            }
            return null;
        }
        if (heroData.getHeroDefinition() == null) {
            final Optional<HeroDefinition> rolled = HeroRollSystem.rollHero();
            if (rolled.isEmpty()) {
                if (sendMessage) {
                    player.sendSystemMessage(Component.literal("§cNo hero can answer your call right now."));
                }
                return null;
            }
            heroData.setHero(rolled.get().getId());
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
        if (checkMana && manaData.isExhausted() && !ManaData.hasInfiniteMana(player)) {
            if (sendMessage) {
                player.sendSystemMessage(Component.literal("§eYour mana is exhausted. Recover half of it ("
                        + manaData.getMaxMana() / 2 + ") to summon your hero again."));
            }
            return null;
        }

        if (checkMana && !manaData.hasEnoughMana(player, getManaCost(heroData))) {
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

        endDisguise(player);
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
        heroData.setDisguised(false);
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
