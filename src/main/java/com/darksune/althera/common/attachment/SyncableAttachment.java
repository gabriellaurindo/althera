package com.darksune.althera.common.attachment;

import net.minecraft.server.level.ServerPlayer;

public abstract class SyncableAttachment
        implements ISyncableAttachment {

    private boolean dirty;

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public void clearDirty() {
        this.dirty = false;
    }

    @Override
    public abstract void sync(ServerPlayer player);
}