package com.darksune.althera.common.block.entity;

import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.NobleMageEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class NobleSigilBlockEntity extends BlockEntity {

    private static final int CHECK_INTERVAL = 20;
    private static final int MAX_MISSING_CHECKS = 10;
    private static final long RESPAWN_DELAY_TICKS = 24000L;
    private static final double SPAWN_PLAYER_RANGE = 32.0D;

    @Nullable
    private UUID mageUuid;
    private long respawnAt;
    private int missingChecks;

    public NobleSigilBlockEntity(BlockPos pos, BlockState state) {
        super(AltheraBlockEntities.NOBLE_SIGIL.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, NobleSigilBlockEntity sigil) {
        if (level.getGameTime() % CHECK_INTERVAL != 0 || !(level instanceof ServerLevel serverLevel)) return;

        if (sigil.mageUuid != null) {
            sigil.checkMage(serverLevel);
        } else if (level.getGameTime() >= sigil.respawnAt
                && level.getDifficulty() != Difficulty.PEACEFUL
                && level.hasNearbyAlivePlayer(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SPAWN_PLAYER_RANGE)) {
            sigil.spawnMage(serverLevel, pos);
        }
    }

    private void checkMage(final ServerLevel level) {
        if (level.getEntity(mageUuid) instanceof NobleMageEntity mage && mage.isAlive()) {
            missingChecks = 0;
            return;
        }
        if (++missingChecks < MAX_MISSING_CHECKS) return;

        startRespawnTimer(level);
    }

    private void spawnMage(final ServerLevel level, final BlockPos pos) {
        final NobleMageEntity mage = AltheraEntities.NOBLE_MAGE.get().create(level);
        if (mage == null) return;

        mage.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        EventHooks.finalizeMobSpawn(mage, level, level.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null);
        mage.bindToSigil(pos);
        level.addFreshEntity(mage);
        mageUuid = mage.getUUID();
        missingChecks = 0;
        setChanged();
    }

    public boolean claim(final NobleMageEntity mage) {
        if (mageUuid != null && !mageUuid.equals(mage.getUUID())) return false;

        if (mageUuid == null) {
            mageUuid = mage.getUUID();
            setChanged();
        }
        missingChecks = 0;
        return true;
    }

    public void onMageDied(final NobleMageEntity mage) {
        if (!mage.getUUID().equals(mageUuid) || level == null) return;
        startRespawnTimer(level);
    }

    private void startRespawnTimer(final Level level) {
        mageUuid = null;
        missingChecks = 0;
        respawnAt = level.getGameTime() + RESPAWN_DELAY_TICKS;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (mageUuid != null) {
            tag.putUUID("Mage", mageUuid);
        }
        tag.putLong("RespawnAt", respawnAt);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        mageUuid = tag.hasUUID("Mage") ? tag.getUUID("Mage") : null;
        respawnAt = tag.getLong("RespawnAt");
    }
}
