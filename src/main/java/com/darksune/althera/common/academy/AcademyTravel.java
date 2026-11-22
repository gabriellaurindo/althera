package com.darksune.althera.common.academy;

import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.block.AcademyPortalBlock;
import com.darksune.althera.common.block.AltheraBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class AcademyTravel {

    public static final int PORTAL_MANA_COST = 100;

    public static final int PORTAL_TICKS = 40;

    private static final Map<UUID, Integer> PORTAL_TIME = new HashMap<>();

    private static final Map<UUID, Long> LAST_PORTAL_TICK = new HashMap<>();

    private AcademyTravel() {
    }

    public static InteractionResult tryLight(UseOnContext context) {

        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player == null || !AcademyPortalShape.isFrame(level.getBlockState(context.getClickedPos()))) {
            return InteractionResult.PASS;
        }

        BlockPos start = context.getClickedPos().relative(context.getClickedFace());
        Optional<AcademyPortalShape> shape = AcademyPortalShape.find(level, start);

        if (shape.isEmpty()) {
            return InteractionResult.PASS;
        }

        ManaData mana = ManaData.get(player);

        if (!mana.hasEnoughMana(player, PORTAL_MANA_COST)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("§cOpening the portal takes " + PORTAL_MANA_COST + " mana."), true);
            }
            return InteractionResult.FAIL;
        }

        if (level instanceof ServerLevel serverLevel) {
            mana.consumeMana(player, PORTAL_MANA_COST);
            open(serverLevel, shape.get());
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static void open(ServerLevel level, AcademyPortalShape shape) {

        BlockState portal = AltheraBlocks.ACADEMY_PORTAL.get().defaultBlockState().setValue(AcademyPortalBlock.AXIS, shape.axis());

        for (BlockPos pos : shape.interior()) {
            level.setBlock(pos, portal, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }

        BlockPos centre = shape.interior().getFirst();
        level.playSound(null, centre, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.5F, 0.6F);
        level.playSound(null, centre, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.4F);
        level.sendParticles(ParticleTypes.END_ROD, centre.getX() + 0.5D, centre.getY() + 0.5D, centre.getZ() + 0.5D,
                40, 0.8D, 1.2D, 0.8D, 0.05D);
    }

    public static void tickInPortal(ServerPlayer player) {

        if (player.isOnPortalCooldown()) {
            player.setPortalCooldown();
            return;
        }

        long now = player.serverLevel().getGameTime();
        Long last = LAST_PORTAL_TICK.get(player.getUUID());

        if (last != null && last == now) {
            return;
        }

        int time = last != null && last == now - 1 ? PORTAL_TIME.getOrDefault(player.getUUID(), 0) + 1 : 1;
        LAST_PORTAL_TICK.put(player.getUUID(), now);
        PORTAL_TIME.put(player.getUUID(), time);

        if (time < (player.isCreative() ? 1 : PORTAL_TICKS)) {
            return;
        }

        forget(player);
        player.setPortalCooldown();

        if (Academy.isIn(player.level())) {
            leave(player);
        } else {
            enter(player);
        }
    }

    public static void forget(ServerPlayer player) {
        PORTAL_TIME.remove(player.getUUID());
        LAST_PORTAL_TICK.remove(player.getUUID());
    }

    public static boolean enter(ServerPlayer player) {

        AcademyData data = AcademyData.get(player);

        if (!data.isInvited()) {
            player.displayClientMessage(Component.literal("§dThe portal won't let you through: you need an invitation "
                    + "to the " + Academy.NAME + "."), true);
            player.playNotifySound(SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 0.6F);
            return false;
        }

        data.setReturnPoint(new AcademyData.ReturnPoint(player.level().dimension(), player.position(), player.getYRot()));

        if (!Academy.sendToAcademy(player)) {
            return false;
        }

        player.setPortalCooldown();
        playArrival(player);

        return true;
    }

    public static void leave(ServerPlayer player) {

        AcademyData.ReturnPoint point = AcademyData.get(player).getReturnPoint();
        ServerLevel target = point == null ? null : player.server.getLevel(point.dimension());

        if (target == null || Academy.isIn(target)) {
            Academy.sendHome(player);
        } else {
            Vec3 position = point.position();
            player.teleportTo(target, position.x, position.y, position.z, point.yaw(), 0.0F);
            player.fallDistance = 0.0F;
        }

        player.setPortalCooldown();
        playArrival(player);
    }

    private static void playArrival(ServerPlayer player) {
        player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS,
                1.2F, 1.2F);
        player.serverLevel().sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0D, player.getZ(),
                30, 0.5D, 0.8D, 0.5D, 0.05D);
    }
}
