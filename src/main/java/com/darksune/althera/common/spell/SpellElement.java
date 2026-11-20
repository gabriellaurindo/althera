package com.darksune.althera.common.spell;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
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

    public ChatFormatting color() {
        return switch (this) {
            case FIRE -> ChatFormatting.GOLD;
            case LIGHTNING -> ChatFormatting.YELLOW;
            case ICE -> ChatFormatting.AQUA;
            case NONE -> ChatFormatting.LIGHT_PURPLE;
        };
    }

    public String displayName() {
        return Character.toUpperCase(id.charAt(0)) + id.substring(1);
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
