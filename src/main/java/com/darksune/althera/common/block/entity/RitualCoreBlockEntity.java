package com.darksune.althera.common.block.entity;

import com.darksune.althera.common.multiblock.MultiblockValidator;
import com.darksune.althera.common.multiblock.MultiblockValidator.CircleState;
import com.darksune.althera.common.system.RitualSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RitualCoreBlockEntity extends BlockEntity {

    private static final int INSPECT_INTERVAL_TICKS = 10;
    private static final float IDLE_SPIN_DEGREES = 0.4F;
    private static final float CHANNEL_SPIN_DEGREES = 9.0F;

    private static final String CHANNEL_TICKS_TAG = "ChannelTicks";
    private static final String OFFERED_TAG = "Offered";

    private CircleState circleState = CircleState.EMPTY;
    private int ticksUntilInspect;
    private float innerAngle;
    private float previousInnerAngle;

    private int channelTicks = -1;
    private List<BlockPos> offered = List.of();
    private List<ItemStack> offeredItems = List.of();
    @Nullable
    private UUID channelPlayer;

    public RitualCoreBlockEntity(BlockPos pos, BlockState state) {
        super(AltheraBlockEntities.RITUAL_CORE.get(), pos, state);
    }

    public CircleState getCircleState() {
        return circleState;
    }

    public float getInnerAngle(final float partialTick) {
        return Mth.rotLerp(partialTick, previousInnerAngle, innerAngle);
    }

    public boolean isChanneling() {
        return channelTicks >= 0;
    }

    public float getChannelProgress(final float partialTick) {
        if (!isChanneling()) return 0.0F;
        return Mth.clamp((channelTicks + partialTick) / RitualSystem.CHANNEL_TICKS, 0.0F, 1.0F);
    }

    public boolean isOffered(final BlockPos pedestalPos) {
        return isChanneling() && offered.contains(pedestalPos);
    }

    public void startChannel(final Player player, final List<RitualPedestalBlockEntity> pedestals) {
        channelTicks = 0;
        channelPlayer = player.getUUID();
        offered = pedestals.stream().map(RitualPedestalBlockEntity::getBlockPos).toList();
        offeredItems = pedestals.stream().map(pedestal -> pedestal.getItem().copy()).toList();
        syncChannel();
    }

    private void endChannel() {
        channelTicks = -1;
        channelPlayer = null;
        offered = List.of();
        offeredItems = List.of();
        syncChannel();
    }

    private void syncChannel() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, RitualCoreBlockEntity core) {
        core.previousInnerAngle = core.innerAngle;
        core.innerAngle = (core.innerAngle + IDLE_SPIN_DEGREES
                + CHANNEL_SPIN_DEGREES * core.getChannelProgress(0.0F)) % 360.0F;

        if (core.isChanneling() && core.channelTicks < RitualSystem.CHANNEL_TICKS) {
            core.channelTicks++;
        }

        if (core.ticksUntilInspect-- > 0) return;

        core.ticksUntilInspect = INSPECT_INTERVAL_TICKS;
        core.circleState = MultiblockValidator.inspect(level, pos);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RitualCoreBlockEntity core) {
        if (!core.isChanneling() || !(level instanceof ServerLevel serverLevel)) return;

        final Player player = core.channelPlayer != null ? level.getPlayerByUUID(core.channelPlayer) : null;
        if (player == null || !player.isAlive() || !core.isWithinReach(player)) {
            core.endChannel();
            RitualSystem.interrupt(level, pos, player, null);
            return;
        }

        if (!MultiblockValidator.isValid(level, pos) || !core.offeringsIntact(level)) {
            core.endChannel();
            RitualSystem.interrupt(level, pos, player, null);
            return;
        }

        RitualSystem.tickChannel(serverLevel, pos, core.offered, core.channelTicks);

        if (++core.channelTicks < RitualSystem.CHANNEL_TICKS) return;

        final List<BlockPos> offered = core.offered;
        RitualSystem.complete(player, serverLevel, pos, offered);
        core.endChannel();
    }

    private boolean isWithinReach(final Player player) {
        final double maxDistance = RitualSystem.MAX_CHANNEL_DISTANCE;
        return player.position().distanceToSqr(Vec3.atCenterOf(worldPosition)) <= maxDistance * maxDistance;
    }

    private boolean offeringsIntact(final Level level) {
        for (int index = 0; index < offered.size(); index++) {
            if (!(level.getBlockEntity(offered.get(index)) instanceof RitualPedestalBlockEntity pedestal)
                    || !ItemStack.isSameItemSameComponents(pedestal.getItem(), offeredItems.get(index))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (!tag.contains(CHANNEL_TICKS_TAG)) return;

        channelTicks = tag.getInt(CHANNEL_TICKS_TAG);
        offered = Arrays.stream(tag.getLongArray(OFFERED_TAG)).mapToObj(BlockPos::of).toList();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        final CompoundTag tag = new CompoundTag();
        tag.putInt(CHANNEL_TICKS_TAG, channelTicks);
        tag.put(OFFERED_TAG, new LongArrayTag(offered.stream().mapToLong(BlockPos::asLong).toArray()));
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
