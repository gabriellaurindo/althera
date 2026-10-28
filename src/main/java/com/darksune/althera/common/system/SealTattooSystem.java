package com.darksune.althera.common.system;

import com.darksune.althera.common.commandseal.CommandSealData;
import com.darksune.althera.network.packet.SealTattooPacket;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

public final class SealTattooSystem {

    public static final int TATTOO_COUNT = 1;

    private SealTattooSystem() {}

    public static CommandSealData ensureTattoo(final Player player) {
        final CommandSealData data = CommandSealData.get(player);
        if (!data.hasTattoo()) {
            data.setTattoo(player.getRandom().nextInt(TATTOO_COUNT), randomColor(player.getRandom()));
        } else if (data.getTattoo() >= TATTOO_COUNT) {
            data.setTattoo(player.getRandom().nextInt(TATTOO_COUNT), data.getColor());
        }
        return data;
    }

    public static void rerollColor(final ServerPlayer player) {
        final CommandSealData data = ensureTattoo(player);
        data.setTattoo(data.getTattoo(), randomColor(player.getRandom()));
        glow(player);
    }

    public static void rerollDesign(final ServerPlayer player) {
        final CommandSealData data = ensureTattoo(player);
        final int other = player.getRandom().nextInt(TATTOO_COUNT - 1);
        data.setTattoo(other >= data.getTattoo() ? other + 1 : other, data.getColor());
        glow(player);
    }

    private static int randomColor(final RandomSource random) {
        return Mth.hsvToRgb(random.nextFloat(), 0.55F + random.nextFloat() * 0.45F, 1.0F);
    }

    public static void announce(final ServerPlayer player) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet(player, false));
    }

    public static void showTo(final ServerPlayer player, final ServerPlayer viewer) {
        PacketDistributor.sendToPlayer(viewer, packet(player, false));
    }

    public static void glow(final Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        final SealTattooPacket packet = packet(serverPlayer, true);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, packet);

        final int color = packet.color();
        final DustParticleOptions dust = new DustParticleOptions(new Vector3f(
                FastColor.ARGB32.red(color) / 255.0F,
                FastColor.ARGB32.green(color) / 255.0F,
                FastColor.ARGB32.blue(color) / 255.0F), 1.0F);
        final Vec3 hand = rightHandPosition(serverPlayer);
        serverPlayer.serverLevel().sendParticles(dust, hand.x, hand.y, hand.z, 14, 0.15D, 0.25D, 0.15D, 0.0D);
        serverPlayer.serverLevel().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 1.2F);
    }

    private static SealTattooPacket packet(final ServerPlayer player, final boolean cast) {
        final CommandSealData data = ensureTattoo(player);
        return new SealTattooPacket(player.getUUID(), data.getTattoo(), data.getColor(), cast);
    }

    private static Vec3 rightHandPosition(final Player player) {
        final float yaw = player.yBodyRot * Mth.DEG_TO_RAD;
        return player.position().add(-Mth.cos(yaw) * 0.4D, 1.0D, -Mth.sin(yaw) * 0.4D);
    }
}
