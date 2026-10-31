package com.darksune.althera.common.hero;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityDimensions;

public record HeroHitbox(float width, float height) {

    public static final HeroHitbox DEFAULT = new HeroHitbox(0.6f, 1.8f);

    public static final Codec<HeroHitbox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("width", DEFAULT.width).forGetter(HeroHitbox::width),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("height", DEFAULT.height).forGetter(HeroHitbox::height)
    ).apply(instance, HeroHitbox::new));

    public EntityDimensions dimensions() {
        return EntityDimensions.scalable(width, height);
    }
}
