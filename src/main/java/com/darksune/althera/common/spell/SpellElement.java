package com.darksune.althera.common.spell;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SpellElement implements StringRepresentable {
    NONE("none"),
    FIRE("fire"),
    LIGHTNING("lightning"),
    ICE("ice");

    public static final Codec<SpellElement> CODEC = StringRepresentable.fromEnum(SpellElement::values);

    private final String id;

    SpellElement(String id) {
        this.id = id;
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
