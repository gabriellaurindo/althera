package com.darksune.althera.common.academy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class AcademySavedData extends SavedData {

    private static final String NAME = "althera_academy";

    private static final String BUILT_VERSION = "BuiltVersion";

    private int builtVersion;

    public static AcademySavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(AcademySavedData::new, AcademySavedData::load, null), NAME);
    }

    private static AcademySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        AcademySavedData data = new AcademySavedData();
        data.builtVersion = tag.getInt(BUILT_VERSION);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt(BUILT_VERSION, builtVersion);
        return tag;
    }

    public int getBuiltVersion() {
        return builtVersion;
    }

    public void setBuiltVersion(int version) {
        builtVersion = version;
        setDirty();
    }
}
