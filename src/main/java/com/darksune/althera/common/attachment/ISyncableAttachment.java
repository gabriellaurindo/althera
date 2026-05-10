package com.darksune.althera.common.attachment;

import net.minecraft.server.level.ServerPlayer;

public interface ISyncableAttachment {

    boolean isDirty();

    void markDirty();

    void clearDirty();

    void sync(ServerPlayer player);
}