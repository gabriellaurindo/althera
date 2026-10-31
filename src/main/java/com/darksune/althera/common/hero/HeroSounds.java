package com.darksune.althera.common.hero;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

public record HeroSounds(Optional<Holder<SoundEvent>> hurt, Optional<Holder<SoundEvent>> death,
                         Optional<Holder<SoundEvent>> step) {

    public static final HeroSounds DEFAULT = new HeroSounds(Optional.empty(), Optional.empty(), Optional.empty());

    public static final Codec<HeroSounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SoundEvent.CODEC.optionalFieldOf("hurt").forGetter(HeroSounds::hurt),
            SoundEvent.CODEC.optionalFieldOf("death").forGetter(HeroSounds::death),
            SoundEvent.CODEC.optionalFieldOf("step").forGetter(HeroSounds::step)
    ).apply(instance, HeroSounds::new));
}
