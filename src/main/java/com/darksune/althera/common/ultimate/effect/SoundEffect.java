package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public record SoundEffect(SoundEvent sound, float volume, float pitch) implements UltimateEffect {

    public static final MapCodec<SoundEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("sound").forGetter(SoundEffect::sound),
            Codec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(SoundEffect::volume),
            Codec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(SoundEffect::pitch)
    ).apply(instance, SoundEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.SOUND;
    }

    @Override
    public void apply(UltimateContext context) {
        context.level().playSound(
                null,
                context.hero().getX(),
                context.hero().getY(),
                context.hero().getZ(),
                sound,
                SoundSource.HOSTILE,
                volume,
                pitch
        );
    }
}
