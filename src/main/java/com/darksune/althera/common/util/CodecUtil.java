package com.darksune.althera.common.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.Optional;

public final class CodecUtil {

    private CodecUtil() {
    }

    public static MapCodec<Float> optionalFloatWithLegacyName(final String name, final String legacyName,
                                                              final float defaultValue) {
        return Codec.mapPair(Codec.FLOAT.optionalFieldOf(name), Codec.FLOAT.optionalFieldOf(legacyName)).xmap(
                fields -> fields.getFirst().or(fields::getSecond).orElse(defaultValue),
                value -> Pair.of(Optional.of(value), Optional.empty())
        );
    }
}
