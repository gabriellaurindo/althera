package com.darksune.althera.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RitualPedestalBlockEntity extends BlockEntity {

    private static final String ITEM_TAG = "Item";

    private ItemStack item = ItemStack.EMPTY;

    public RitualPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(AltheraBlockEntities.RITUAL_PEDESTAL.get(), pos, state);
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isEmpty() {
        return item.isEmpty();
    }

    public void setItem(final ItemStack stack) {
        item = stack.copyWithCount(Math.min(stack.getCount(), 1));
        onContentChanged();
    }

    public ItemStack removeItem() {
        final ItemStack removed = item;
        item = ItemStack.EMPTY;
        onContentChanged();
        return removed;
    }

    private void onContentChanged() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(ITEM_TAG, item.saveOptional(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        item = ItemStack.parseOptional(registries, tag.getCompound(ITEM_TAG));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
