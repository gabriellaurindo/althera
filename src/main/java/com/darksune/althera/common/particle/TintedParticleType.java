package com.darksune.althera.common.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class TintedParticleType extends ParticleType<TintedParticleOptions> {

    public static final Codec<Integer> COLOR_CODEC = Codec.withAlternative(
            Codec.STRING.comapFlatMap(TintedParticleType::parseHex, color -> String.format("#%06X", color & 0xFFFFFF)),
            Codec.INT
    );

    private final MapCodec<TintedParticleOptions> codec;

    private final StreamCodec<RegistryFriendlyByteBuf, TintedParticleOptions> streamCodec;

    private final TintedParticleOptions defaults;

    public TintedParticleType(int color, float scale, int lifetime) {
        super(false);
        this.defaults = new TintedParticleOptions(this, color, scale, lifetime);
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                COLOR_CODEC.optionalFieldOf("color", color).forGetter(TintedParticleOptions::color),
                Codec.floatRange(0.01F, 64.0F).optionalFieldOf("scale", scale).forGetter(TintedParticleOptions::scale),
                Codec.intRange(1, 1200).optionalFieldOf("lifetime", lifetime).forGetter(TintedParticleOptions::lifetime)
        ).apply(instance, (c, s, l) -> new TintedParticleOptions(this, c, s, l)));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.INT, TintedParticleOptions::color,
                ByteBufCodecs.FLOAT, TintedParticleOptions::scale,
                ByteBufCodecs.VAR_INT, TintedParticleOptions::lifetime,
                (c, s, l) -> new TintedParticleOptions(this, c, s, l)
        );
    }

    private static DataResult<Integer> parseHex(String value) {
        String hex = value.startsWith("#") ? value.substring(1) : value;
        try {
            return DataResult.success(Integer.parseInt(hex, 16) & 0xFFFFFF);
        } catch (NumberFormatException e) {
            return DataResult.error(() -> "Not a colour: " + value);
        }
    }

    public TintedParticleOptions defaults() {
        return defaults;
    }

    public TintedParticleOptions with(int color, float scale, int lifetime) {
        return new TintedParticleOptions(this, color, scale, lifetime);
    }

    @Override
    public MapCodec<TintedParticleOptions> codec() {
        return codec;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, TintedParticleOptions> streamCodec() {
        return streamCodec;
    }
}
