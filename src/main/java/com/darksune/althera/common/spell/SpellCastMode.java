package com.darksune.althera.common.spell;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SpellCastMode implements StringRepresentable {
    INSTANT("instant"),
    CHARGE("charge"),
    CONTINUOUS("continuous");

    public static final Codec<SpellCastMode> CODEC = StringRepresentable.fromEnum(SpellCastMode::values);

    private final String id;

    SpellCastMode(String id) {
        this.id = id;
    }

    public boolean isHeld() {
        return this != INSTANT;
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
