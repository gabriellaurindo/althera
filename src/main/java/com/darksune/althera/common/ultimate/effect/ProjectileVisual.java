package com.darksune.althera.common.ultimate.effect;

import com.darksune.althera.common.particle.TintedParticleType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record ProjectileVisual(ResourceLocation texture, float size, float stretch, float spin, boolean glow, int color) {

    public static final Codec<ProjectileVisual> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ProjectileVisual::texture),
            Codec.floatRange(0.05F, 16.0F).optionalFieldOf("size", 0.5F).forGetter(ProjectileVisual::size),
            Codec.floatRange(1.0F, 16.0F).optionalFieldOf("stretch", 1.0F).forGetter(ProjectileVisual::stretch),
            Codec.FLOAT.optionalFieldOf("spin", 0.0F).forGetter(ProjectileVisual::spin),
            Codec.BOOL.optionalFieldOf("glow", true).forGetter(ProjectileVisual::glow),
            TintedParticleType.COLOR_CODEC.optionalFieldOf("color", 0xFFFFFF).forGetter(ProjectileVisual::color)
    ).apply(instance, ProjectileVisual::new));
}
