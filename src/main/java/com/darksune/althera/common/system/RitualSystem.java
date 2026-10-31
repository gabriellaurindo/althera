package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.block.entity.RitualCoreBlockEntity;
import com.darksune.althera.common.block.entity.RitualPedestalBlockEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.item.HeroCatalystItem;
import com.darksune.althera.common.item.component.HeroCatalyst;
import com.darksune.althera.common.multiblock.MultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class RitualSystem {

    public static final int RITUAL_MANA_COST = 300;
    public static final int CHANNEL_TICKS = 60;
    public static final double MAX_CHANNEL_DISTANCE = 8.0D;

    private static final int PARTICLE_TRAIL_STEPS = 8;
    private static final int TRAIL_INTERVAL_TICKS = 4;
    private static final int RESONATE_INTERVAL_TICKS = 15;
    private static final int BURST_COLUMN_STEPS = 16;
    private static final double CORE_TOP = 0.5D;

    private static final String INCOMPLETE_MESSAGE =
            "§cPlace a Ritual Pedestal on each of the four points of the circle and keep the circle clear.";
    private static final String INTERRUPTED_MESSAGE = "§cThe ritual was interrupted.";

    public record Offering(List<RitualPedestalBlockEntity> pedestals, HeroDefinition hero) {

        public List<BlockPos> positions() {
            return pedestals.stream().map(RitualPedestalBlockEntity::getBlockPos).toList();
        }
    }

    private RitualSystem() {
    }

    public static void tryStart(final Player player, final Level level, final BlockPos pos,
                                final RitualCoreBlockEntity core) {
        if (core.isChanneling()) {
            player.sendSystemMessage(Component.literal("§eThe ritual is already underway."));
            return;
        }

        final Optional<Offering> offering = prepare(player, level, pos,
                reason -> player.sendSystemMessage(Component.literal(reason)));
        if (offering.isEmpty()) return;

        core.startChannel(player, offering.get().pedestals());
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.2F);
        player.sendSystemMessage(Component.literal("§dThe circle answers. Stay close until the ritual ends."));
    }

    public static Optional<Offering> prepare(final Player player, final Level level, final BlockPos pos,
                                             final Consumer<String> onFailure) {
        if (!MultiblockValidator.isValid(level, pos)) {
            onFailure.accept(INCOMPLETE_MESSAGE);
            return Optional.empty();
        }

        final List<RitualPedestalBlockEntity> pedestals = MultiblockValidator.findPedestals(level, pos);

        final Optional<RitualPedestalBlockEntity> diamondPedestal = pedestals.stream()
                .filter(pedestal -> pedestal.getItem().is(Blocks.DIAMOND_BLOCK.asItem()))
                .findFirst();
        if (diamondPedestal.isEmpty()) {
            onFailure.accept("§cThe ritual needs a diamond block offered on a ritual pedestal.");
            return Optional.empty();
        }

        final List<RitualPedestalBlockEntity> catalystPedestals = pedestals.stream()
                .filter(pedestal -> pedestal.getItem().getItem() instanceof HeroCatalystItem)
                .toList();
        if (catalystPedestals.size() > 1) {
            onFailure.accept("§cThe ritual accepts a single catalyst.");
            return Optional.empty();
        }

        final HeroDefinition current = HeroData.get(player).getHeroDefinition();
        final ResourceLocation currentId = current != null ? current.getId() : null;

        final Optional<HeroDefinition> rolled;
        if (catalystPedestals.isEmpty()) {
            rolled = HeroRollSystem.rollHeroExcept(currentId);
            if (rolled.isEmpty()) {
                onFailure.accept(current != null ? "§cNo other hero answers the ritual." : "§cNo hero answers the ritual.");
                return Optional.empty();
            }
        } else {
            final HeroCatalyst catalyst = HeroCatalystItem.getCatalyst(catalystPedestals.getFirst().getItem());
            if (catalyst == null || !catalyst.isKnownClass()) {
                onFailure.accept("§cThe faded catalyst no longer answers the ritual.");
                return Optional.empty();
            }
            rolled = HeroRollSystem.rollHeroOf(catalyst.classId(), catalyst.rank(), currentId);
            if (rolled.isEmpty()) {
                onFailure.accept("§cNo " + (current != null ? "other " : "")
                        + catalyst.rank().getDisplayName() + " " + catalyst.heroClass().getDisplayName()
                        + " answers the ritual.");
                return Optional.empty();
            }
        }

        final ManaData manaData = ManaData.get(player);
        if (!ManaData.hasInfiniteMana(player)) {
            if (manaData.getMaxMana() < RITUAL_MANA_COST) {
                onFailure.accept("§cYour mana is too weak to sustain the ritual §7(max "
                        + manaData.getMaxMana() + "/" + RITUAL_MANA_COST + ")§c. Regenerate mana to grow it.");
                return Optional.empty();
            }
            if (!manaData.hasEnoughMana(player, RITUAL_MANA_COST)) {
                onFailure.accept("§cNot enough mana for the ritual §7("
                        + manaData.getMana() + "/" + RITUAL_MANA_COST + ")§c.");
                return Optional.empty();
            }
        }

        final List<RitualPedestalBlockEntity> used = new ArrayList<>();
        used.add(diamondPedestal.get());
        used.addAll(catalystPedestals);
        return Optional.of(new Offering(List.copyOf(used), rolled.get()));
    }

    public static void tickChannel(final ServerLevel level, final BlockPos pos, final List<BlockPos> offered,
                                   final int elapsed) {
        level.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D,
                3, 0.6D, 0.4D, 0.6D, 0.4D);

        if (elapsed % TRAIL_INTERVAL_TICKS == 0) {
            for (final BlockPos pedestalPos : offered) {
                if (level.getBlockEntity(pedestalPos) instanceof RitualPedestalBlockEntity pedestal) {
                    sendOfferingTrail(level, pedestal, pos);
                }
            }
        }

        if (elapsed % RESONATE_INTERVAL_TICKS == 0) {
            final float pitch = 0.8F + 0.8F * elapsed / CHANNEL_TICKS;
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, pitch);
        }
    }

    public static void interrupt(final Level level, final BlockPos pos, @Nullable final Player player,
                                 @Nullable final String reason) {
        level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (player == null) return;

        player.sendSystemMessage(Component.literal(INTERRUPTED_MESSAGE));
        if (reason != null) {
            player.sendSystemMessage(Component.literal(reason));
        }
    }

    public static void complete(final Player player, final ServerLevel level, final BlockPos pos,
                                final List<BlockPos> offered) {
        final List<String> failures = new ArrayList<>();
        final Optional<Offering> prepared = prepare(player, level, pos, failures::add);
        if (prepared.isEmpty()) {
            interrupt(level, pos, player, failures.getFirst());
            return;
        }

        final Offering offering = prepared.get();
        if (!offering.positions().equals(offered) || !ManaData.get(player).consumeMana(player, RITUAL_MANA_COST)) {
            interrupt(level, pos, player, null);
            return;
        }

        offering.pedestals().forEach(RitualPedestalBlockEntity::removeItem);
        swapHero(player, offering.hero());
        playBurst(level, pos);
    }

    private static void swapHero(final Player player, final HeroDefinition hero) {
        final HeroData heroData = HeroData.get(player);
        final HeroDefinition current = heroData.getHeroDefinition();

        if (current != null) {
            HeroSummonSystem.dismissSummon(player);
            player.sendSystemMessage(Component.literal(
                    "§7" + current.getName() + " leaves. Your hero level (" + heroData.getLevel() + ") carries over."
            ));
        }

        player.sendSystemMessage(Component.literal(
                "§aYou have formed a contract with §f" + hero.getName()
                        + " §7[" + hero.getHeroClass().getDisplayName() + "] §6(" + hero.getRank().name() + ")"
        ));

        heroData.setHero(hero.getId());
        heroData.setHealth(HeroStatsSystem.getMaxHealth(heroData));
        heroData.sync(player);
    }

    private static void playBurst(final ServerLevel level, final BlockPos pos) {
        final double x = pos.getX() + 0.5D;
        final double y = pos.getY() + CORE_TOP;
        final double z = pos.getZ() + 0.5D;

        for (int step = 0; step < BURST_COLUMN_STEPS; step++) {
            level.sendParticles(ParticleTypes.END_ROD, x, y + step * 0.25D, z, 2, 0.05D, 0.05D, 0.05D, 0.01D);
        }
        level.sendParticles(ParticleTypes.ENCHANT, x, y + 0.5D, z, 60, 0.6D, 0.6D, 0.6D, 0.6D);
        level.sendParticles(ParticleTypes.END_ROD, x, y + 0.3D, z, 30, 0.1D, 0.1D, 0.1D, 0.15D);

        level.playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.BLOCKS, 0.4F, 1.2F);
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void sendOfferingTrail(final ServerLevel level, final RitualPedestalBlockEntity pedestal,
                                          final BlockPos core) {
        if (pedestal.isEmpty()) return;

        final ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, pedestal.getItem());
        final BlockPos from = pedestal.getBlockPos();
        final double startX = from.getX() + 0.5D;
        final double startY = from.getY() + 1.2D;
        final double startZ = from.getZ() + 0.5D;
        final double endX = core.getX() + 0.5D;
        final double endY = core.getY() + CORE_TOP;
        final double endZ = core.getZ() + 0.5D;

        for (int step = 0; step <= PARTICLE_TRAIL_STEPS; step++) {
            final double progress = (double) step / PARTICLE_TRAIL_STEPS;
            level.sendParticles(particle,
                    startX + (endX - startX) * progress,
                    startY + (endY - startY) * progress,
                    startZ + (endZ - startZ) * progress,
                    1, 0.05D, 0.05D, 0.05D, 0.01D);
        }
    }
}
