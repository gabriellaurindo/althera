package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.StringRepresentable;

public enum UltimateEffectType implements StringRepresentable {

    DAMAGE("damage", DamageEffect.CODEC),
    KNOCKBACK("knockback", KnockbackEffect.CODEC),
    IGNITE("ignite", IgniteEffect.CODEC),
    MOB_EFFECT("mob_effect", StatusEffect.CODEC),
    EXPLOSION("explosion", ExplosionEffect.CODEC),
    PARTICLES("particles", ParticlesEffect.CODEC),
    SOUND("sound", SoundEffect.CODEC),
    ULTIMATE_ANIMATION("ultimate_animation", UltimateAnimationEffect.CODEC);

    public static final Codec<UltimateEffectType> CODEC = StringRepresentable.fromEnum(UltimateEffectType::values);

    private final String id;

    private final MapCodec<? extends UltimateEffect> codec;

    UltimateEffectType(String id, MapCodec<? extends UltimateEffect> codec) {
        this.id = id;
        this.codec = codec;
    }

    public MapCodec<? extends UltimateEffect> codec() {
        return codec;
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
