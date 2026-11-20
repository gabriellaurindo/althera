package com.darksune.althera.common.spell;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SpellBranch implements StringRepresentable {
    NONE("none"),
    DAMAGE("damage"),
    DEFENSE("defense"),
    UTILITY("utility");

    public static final Codec<SpellBranch> CODEC = StringRepresentable.fromEnum(SpellBranch::values);

    private final String id;

    SpellBranch(String id) {
        this.id = id;
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
