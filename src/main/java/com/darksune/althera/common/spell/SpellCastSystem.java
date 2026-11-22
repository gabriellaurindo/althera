package com.darksune.althera.common.spell;

import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.entity.PlayerCaster;
import com.darksune.althera.common.skill.SkillLimits;
import com.darksune.althera.common.ultimate.effect.UltimateContext;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public final class SpellCastSystem {

    public static final int CONTINUOUS_TRAINING_TICKS = 20;

    private static final int TICKS_PER_SECOND = 20;

    private SpellCastSystem() {
    }

    public record Cast(
            ResourceLocation nodeId,
            SpellNode node,
            SpellDefinition spell,
            PlayerSpell playerSpell,
            PlayerSpellData data,
            boolean training,
            int level,
            int elementLevel
    ) {

        public int manaCost() {
            return training
                    ? SpellTraining.trainingCost(playerSpell.manaCost())
                    : playerSpell.proficiency().cost(playerSpell.manaCost(), level);
        }

        public SpellCastMode mode() {
            return playerSpell.castMode();
        }

        public int chargeTicks() {
            return Math.max(1, spell.castTicks());
        }

        public int pulseTicks() {
            return Math.max(1, spell.castTicks());
        }

        public SpellScale scale() {
            return SpellScale.of(playerSpell, level, elementLevel);
        }
    }

    @Nullable
    public static Cast prepare(final Player player, @Nullable final ResourceLocation nodeId) {
        return prepare(player, nodeId, true);
    }

    @Nullable
    private static Cast prepare(final Player player, @Nullable final ResourceLocation nodeId, final boolean explain) {

        if (nodeId == null) {
            return refuse(player, explain, "No spell in this slot.");
        }

        final SpellNode node = SpellNodeRegistry.get(nodeId);
        final SpellDefinition spell = node == null ? null : node.spellDefinition();

        if (spell == null) {
            return refuse(player, explain, "That is not a spell.");
        }

        final PlayerSpellData data = PlayerSpellData.get(player);
        final SpellNodeState state = data.getState(nodeId);
        final String name = node.displayName();

        if (state == SpellNodeState.LEARNED && !data.isUsable(nodeId)) {
            return refuse(player, explain, name + " needs the spells before it learned first.");
        }

        if (state != SpellNodeState.TRAINING && state != SpellNodeState.LEARNED) {
            return refuse(player, explain, "You are not training " + name + ".");
        }

        if (!SkillLimits.isUnlimited(player) && data.isOnCooldown(nodeId)) {
            return refuse(player, explain, name + " is recharging.");
        }

        return new Cast(nodeId, node, spell, spell.player().orElseThrow(), data, state == SpellNodeState.TRAINING,
                data.getSpellLevel(nodeId), data.getElementLevel(node.element()));
    }

    @Nullable
    public static Cast prepareSelected(final Player player) {
        return prepare(player, PlayerSpellData.get(player).getSelectedNode());
    }

    public static boolean cast(final ServerPlayer player, final ResourceLocation nodeId) {

        final Cast cast = prepare(player, nodeId);

        return cast != null && cast(player, cast, InteractionHand.MAIN_HAND);
    }

    public static boolean cast(final ServerPlayer player, final Cast cast, final InteractionHand hand) {

        if (cast.mode().isHeld()) {
            notify(player, cast.node().displayName() + " must be held with a Spell Book.");
            return false;
        }

        if (!pay(player, cast.manaCost())) {
            notify(player, "Not enough mana (" + cast.manaCost() + ").");
            return false;
        }

        startCooldown(player, cast);
        player.swing(hand, true);
        fire(player, cast);
        gainXp(player, cast, cast.manaCost());

        return true;
    }

    public static void tickHold(final ServerPlayer player, final int heldTicks) {

        final Cast cast = prepareSelected(player);

        if (cast == null || !cast.mode().isHeld()) {
            player.stopUsingItem();
            return;
        }

        if (cast.mode() == SpellCastMode.CHARGE) {
            tickCharge(player, cast, heldTicks);
        } else {
            tickContinuous(player, cast, heldTicks);
        }
    }

    public static void releaseHold(final ServerPlayer player, final int heldTicks) {

        final Cast cast = prepareSelected(player);

        if (cast == null || cast.mode() != SpellCastMode.CHARGE) {
            return;
        }

        if (heldTicks < cast.chargeTicks()) {
            notify(player, cast.node().displayName() + " fizzles.");
            player.level().playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.3F, 1.8F);
            return;
        }

        startCooldown(player, cast);
        player.swing(player.getUsedItemHand(), true);
        fire(player, cast);
        gainXp(player, cast, cast.manaCost());
    }

    public static void endHold(final ServerPlayer player) {

        final Cast cast = prepare(player, PlayerSpellData.get(player).getSelectedNode(), false);

        if (cast != null && cast.mode() == SpellCastMode.CONTINUOUS) {
            startCooldown(player, cast);
        }
    }

    private static void tickCharge(final ServerPlayer player, final Cast cast, final int heldTicks) {

        if (heldTicks > cast.chargeTicks()) {
            return;
        }

        if (!pay(player, share(cast.manaCost(), heldTicks, cast.chargeTicks()))) {
            notify(player, "Not enough mana to charge " + cast.node().displayName() + ".");
            player.stopUsingItem();
            return;
        }

        if (!cast.training()) {
            cast.spell().charge(UltimateContext.spell(new PlayerCaster(player), cast.scale()), heldTicks);
        }

        if (heldTicks == cast.chargeTicks()) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.4F);
        }
    }

    private static void tickContinuous(final ServerPlayer player, final Cast cast, final int heldTicks) {

        final int payment = share(cast.manaCost(), heldTicks, TICKS_PER_SECOND);

        if (!pay(player, payment)) {
            notify(player, "Out of mana.");
            player.releaseUsingItem();
            return;
        }

        gainXp(player, cast, payment);

        if (cast.training()) {

            if ((heldTicks - 1) % cast.pulseTicks() == 0) {
                playTrainingEffects(player);
            }

            if (heldTicks % CONTINUOUS_TRAINING_TICKS == 0) {
                train(player, cast);
            }

            return;
        }

        if ((heldTicks - 1) % cast.pulseTicks() == 0) {
            castSpell(player, cast);
        }
    }

    private static int share(final int cost, final int tick, final int period) {
        final long due = ceilDiv((long) cost * tick, period);
        final long paid = ceilDiv((long) cost * (tick - 1), period);
        return (int) (due - paid);
    }

    private static long ceilDiv(final long value, final long divisor) {
        return (value + divisor - 1) / divisor;
    }

    private static boolean pay(final ServerPlayer player, final int amount) {
        return amount <= 0 || ManaData.get(player).consumeMana(player, amount);
    }

    private static void startCooldown(final ServerPlayer player, final Cast cast) {
        final int cooldown = cast.playerSpell().cooldownTicks();

        if (cooldown > 0 && !SkillLimits.isUnlimited(player)) {
            cast.data().startCooldown(cast.nodeId(), cooldown);
        }
    }

    private static void fire(final ServerPlayer player, final Cast cast) {

        if (cast.training()) {
            playTrainingEffects(player);
            train(player, cast);
        } else {
            castSpell(player, cast);
        }
    }

    private static void castSpell(final ServerPlayer player, final Cast cast) {
        final SpellScale scale = cast.scale();
        playCastEffects(player, cast.node().element(), scale.mastered());
        cast.spell().cast(UltimateContext.spell(new PlayerCaster(player), scale));
    }

    private static void playCastEffects(final ServerPlayer player, final SpellElement element, final boolean mastered) {
        final ServerLevel level = player.serverLevel();
        final Vec3 hand = handPosition(player);
        final int rgb = element.color().getColor() == null ? 0xFFFFFF : element.color().getColor();
        final Vector3f color = new Vector3f((rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F);
        level.sendParticles(new DustParticleOptions(color, 0.8F), hand.x, hand.y, hand.z, 8, 0.15D, 0.15D, 0.15D, 0.0D);

        if (mastered) {
            level.sendParticles(ParticleTypes.END_ROD, hand.x, hand.y, hand.z, 6, 0.2D, 0.2D, 0.2D, 0.04D);
        }
    }

    private static Vec3 handPosition(final Player player) {
        return player.getEyePosition().add(player.getLookAngle().scale(0.8D)).subtract(0, 0.3D, 0);
    }

    private static void train(final ServerPlayer player, final Cast cast) {

        final int maxMana = ManaData.get(player).getMaxMana();
        final int required = SpellTraining.requiredCasts(cast.playerSpell().manaCost(), cast.node().tier(), maxMana);

        if (cast.data().addProgress(cast.nodeId(), 1.0F / required)) {
            onLearned(player, cast.node());
            return;
        }

        final int percent = (int) (cast.data().getProgress(cast.nodeId()) * 100.0F);
        player.displayClientMessage(Component.literal("§bTraining " + cast.node().displayName() + ": " + percent + "%"), true);
    }

    private static void gainXp(final ServerPlayer player, final Cast cast, final int mana) {

        if (cast.training() || mana <= 0 || SkillLimits.isUnlimited(player)) {
            return;
        }

        final SpellElement element = cast.node().element();
        final PlayerSpellData.LevelUps levelUps = cast.data().addXp(cast.nodeId(), element, mana);

        if (levelUps.spellLevel() > 0) {
            onLevelUp(player, cast.node().displayName(), levelUps.spellLevel());
        }

        if (levelUps.elementLevel() > 0) {
            onLevelUp(player, element.displayName() + " mastery", levelUps.elementLevel());
        }

        if (levelUps.any()) {
            SpellAdvancements.check(player);
        }
    }

    public static void onLevelUp(final ServerPlayer player, final String name, final int level) {
        player.sendSystemMessage(Component.literal("§6" + name + " reached level " + level + "!"));
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6F, 1.5F);
    }

    public static void onLearned(final ServerPlayer player, final SpellNode node) {
        player.sendSystemMessage(Component.literal("§aYou learned " + node.displayName() + "!"));
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.2F);
        SpellAdvancements.check(player);
    }

    private static void playTrainingEffects(final ServerPlayer player) {
        final ServerLevel level = player.serverLevel();
        final Vec3 hand = handPosition(player);
        level.sendParticles(ParticleTypes.POOF, hand.x, hand.y, hand.z, 6, 0.1D, 0.1D, 0.1D, 0.02D);
        level.sendParticles(ParticleTypes.ENCHANT, hand.x, hand.y, hand.z, 12, 0.3D, 0.3D, 0.3D, 0.5D);
        level.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.4F, 1.6F);
    }

    @Nullable
    private static Cast refuse(final Player player, final boolean explain, final String message) {
        if (explain) {
            notify(player, message);
        }
        return null;
    }

    private static void notify(final Player player, final String message) {
        if (!player.level().isClientSide) {
            player.displayClientMessage(Component.literal("§c" + message), true);
        }
    }
}
