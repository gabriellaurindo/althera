package com.darksune.althera.common.academy;

import com.darksune.althera.Althera;
import com.darksune.althera.common.AltheraServerConfig;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Althera.MOD_ID)
public final class AcademyEvents {

    private static final double PUSH_STRENGTH = 0.8D;

    private static final double PUSH_UP = 0.25D;

    private static final double FIELD_TOLERANCE = 3.0D;

    private static final double SHOW_DISTANCE = 4.0D;

    private static final int SHOW_INTERVAL_TICKS = 5;

    private static final int SHOW_POINTS = 9;

    private static final double SHOW_SPREAD = 0.12D;

    private static final int WARNING_INTERVAL_TICKS = 40;

    private AcademyEvents() {
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {

        ServerLevel academy = Academy.level(event.getServer());

        if (academy != null) {
            AcademyBuilder.ensureBuilt(academy);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player) || !AcademyData.get(player).handleFirstJoin()) {
            return;
        }

        boolean newPlayer = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) == 0;

        if (newPlayer && AltheraServerConfig.SPAWN_IN_ACADEMY.get() && Academy.sendToAcademy(player)) {
            player.displayClientMessage(Component.literal("§dWelcome to the " + Academy.NAME + "."), false);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AcademyTravel.forget(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player) || !Academy.isIn(player.level()) || player.isSpectator()) {
            return;
        }

        if (player.getY() < AcademyLayout.FLOOR_Y) {
            Academy.sendToAcademy(player);
            player.displayClientMessage(Component.literal("§dThe academy's ward catches you and brings you back."), true);
            return;
        }

        if (player.isCreative()) {
            return;
        }

        double dx = player.getX() - (AcademyLayout.CENTER_X + 0.5D);
        double dz = player.getZ() - (AcademyLayout.CENTER_Z + 0.5D);
        double distance = Math.sqrt(dx * dx + dz * dz);
        double angle = Math.atan2(dz, dx);
        double ward = AcademyLayout.wardRadius(angle);

        if (distance > ward - SHOW_DISTANCE && player.tickCount % SHOW_INTERVAL_TICKS == 0) {
            showWard(player, angle, distance);
        }

        if (distance <= ward) {
            return;
        }

        Vec3 inwards = new Vec3(-dx / distance, 0.0D, -dz / distance);

        if (distance > ward + FIELD_TOLERANCE) {
            double edge = ward - 1.0D;
            player.teleportTo(AcademyLayout.CENTER_X + 0.5D - inwards.x * edge, player.getY(),
                    AcademyLayout.CENTER_Z + 0.5D - inwards.z * edge);
        }

        player.setDeltaMovement(inwards.x * PUSH_STRENGTH, PUSH_UP, inwards.z * PUSH_STRENGTH);
        player.hurtMarked = true;
        player.fallDistance = 0.0F;

        ServerLevel level = player.serverLevel();
        level.sendParticles(ParticleTypes.END_ROD, player.getX() - inwards.x, player.getY() + 1.0D,
                player.getZ() - inwards.z, 12, 0.6D, 0.8D, 0.6D, 0.02D);

        if (player.tickCount % WARNING_INTERVAL_TICKS == 0) {
            level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 1.4F);
            player.displayClientMessage(Component.literal("§dA magic ward surrounds the academy."), true);
        }
    }

    private static void showWard(ServerPlayer player, double angle, double distance) {

        double step = 1.0D / Math.max(distance, 1.0D);

        for (int point = -SHOW_POINTS / 2; point <= SHOW_POINTS / 2; point++) {

            double pointAngle = angle + point * step;
            double radius = AcademyLayout.wardRadius(pointAngle);
            double x = AcademyLayout.CENTER_X + 0.5D + Math.cos(pointAngle) * radius;
            double z = AcademyLayout.CENTER_Z + 0.5D + Math.sin(pointAngle) * radius;

            for (int height = 0; height < 3; height++) {
                player.serverLevel().sendParticles(player, ParticleTypes.END_ROD, true, x, player.getY() + 0.3D + height * 0.8D, z,
                        1, SHOW_SPREAD, SHOW_SPREAD, SHOW_SPREAD, 0.0D);
            }
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (isProtected(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && isProtected(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onUseItemOnBlock(PlayerInteractEvent.RightClickBlock event) {
        if (isProtected(event.getEntity())) {
            event.setUseItem(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        if (isProtected(event.getEntity())
                && (event.getItemStack().getItem() instanceof BucketItem || event.getItemStack().getItem() instanceof SolidBucketItem)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (Academy.isIn(event.getLevel())) {
            event.getAffectedBlocks().clear();
        }
    }

    @SubscribeEvent
    public static void onSetSpawn(PlayerSetSpawnEvent event) {
        if (event.getSpawnLevel() == Academy.LEVEL && event.getNewSpawn() != null) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {

        if (!Academy.isIn(event.getEntity().level())) {
            return;
        }

        Player victim = playerOf(event.getEntity());
        Player attacker = playerOf(event.getSource().getEntity());

        if (victim != null && attacker != null && victim != attacker) {
            event.setCanceled(true);
        }
    }

    @Nullable
    private static Player playerOf(@Nullable Entity entity) {

        if (entity instanceof Player player) {
            return player;
        }

        if (entity instanceof HeroEntity hero && hero.getOwner() instanceof Player owner) {
            return owner;
        }

        return null;
    }

    private static boolean isProtected(@Nullable Player player) {
        return player != null && Academy.isIn(player.level()) && !Academy.canBuild(player);
    }
}
