package com.darksune.althera.common.spell;

import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.entity.PlayerCaster;
import com.darksune.althera.common.skill.SkillLimits;
import com.darksune.althera.common.ultimate.effect.UltimateContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public final class SpellCastSystem {

    private SpellCastSystem() {
    }

    public static boolean cast(final ServerPlayer player, final ResourceLocation nodeId) {

        final SpellNode node = SpellNodeRegistry.get(nodeId);
        final SpellDefinition spell = node == null ? null : node.spellDefinition();

        if (spell == null) {
            return fail(player, "That is not a spell.");
        }

        final PlayerSpell playerSpell = spell.player().orElseThrow();
        final PlayerSpellData data = PlayerSpellData.get(player);
        final SpellNodeState state = data.getState(nodeId);
        final boolean training = state == SpellNodeState.TRAINING;
        final String name = node.displayName();

        if (state == SpellNodeState.LEARNED && !data.isUsable(nodeId)) {
            return fail(player, name + " needs the spells before it learned first.");
        }

        if (!training && state != SpellNodeState.LEARNED) {
            return fail(player, "You are not training " + name + ".");
        }

        if (playerSpell.castMode() != SpellCastMode.INSTANT) {
            return fail(player, name + " must be cast from a Spell Book.");
        }

        final boolean unlimited = SkillLimits.isUnlimited(player);

        if (!unlimited && data.isOnCooldown(nodeId)) {
            return fail(player, name + " is recharging.");
        }

        final int cost = training ? SpellTraining.trainingCost(playerSpell.manaCost()) : playerSpell.manaCost();
        final ManaData mana = ManaData.get(player);

        if (!mana.consumeMana(player, cost)) {
            return fail(player, "Not enough mana (" + cost + ").");
        }

        if (!unlimited) {
            data.startCooldown(nodeId, spell.cooldownTicks());
        }

        player.swing(InteractionHand.MAIN_HAND, true);

        if (training) {
            train(player, data, nodeId, node, playerSpell, mana.getMaxMana());
        } else {
            spell.cast(UltimateContext.spell(new PlayerCaster(player)));
        }

        return true;
    }

    private static void train(
            final ServerPlayer player,
            final PlayerSpellData data,
            final ResourceLocation nodeId,
            final SpellNode node,
            final PlayerSpell spell,
            final int maxMana
    ) {

        playTrainingEffects(player);

        final int required = SpellTraining.requiredCasts(spell.manaCost(), node.tier(), maxMana);

        if (data.addProgress(nodeId, 1.0F / required)) {
            onLearned(player, node);
            return;
        }

        final int percent = (int) (data.getProgress(nodeId) * 100.0F);
        player.displayClientMessage(Component.literal("§bTraining " + node.displayName() + ": " + percent + "%"), true);
    }

    public static void onLearned(final ServerPlayer player, final SpellNode node) {
        player.sendSystemMessage(Component.literal("§aYou learned " + node.displayName() + "!"));
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.2F);
    }

    private static void playTrainingEffects(final ServerPlayer player) {
        final ServerLevel level = player.serverLevel();
        final Vec3 hand = player.getEyePosition().add(player.getLookAngle().scale(0.8D)).subtract(0, 0.3D, 0);
        level.sendParticles(ParticleTypes.POOF, hand.x, hand.y, hand.z, 6, 0.1D, 0.1D, 0.1D, 0.02D);
        level.sendParticles(ParticleTypes.ENCHANT, hand.x, hand.y, hand.z, 12, 0.3D, 0.3D, 0.3D, 0.5D);
        level.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.4F, 1.6F);
    }

    private static boolean fail(final ServerPlayer player, final String message) {
        player.displayClientMessage(Component.literal("§c" + message), true);
        return false;
    }
}
