package com.darksune.althera.common.hero;

import com.darksune.althera.common.ultimate.effect.UltimateContext;
import com.darksune.althera.common.ultimate.effect.UltimateEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record HeroAttack(
        Type type,
        double range,
        int cooldownTicks,
        int shootDelayTicks,
        float projectileSpeed,
        Vec3 projectileOrigin,
        ParticleOptions projectileTrail,
        List<UltimateEffect> onAttack,
        List<UltimateEffect> onHit
) {

    public static final Vec3 DEFAULT_PROJECTILE_ORIGIN = new Vec3(0.0D, 1.26D, 0.0D);

    public static final Codec<HeroAttack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.optionalFieldOf("type", Type.MELEE).forGetter(HeroAttack::type),
            Codec.DOUBLE.optionalFieldOf("range", 12.0D).forGetter(HeroAttack::range),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("cooldown_ticks", 20).forGetter(HeroAttack::cooldownTicks),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("shoot_delay_ticks", 0).forGetter(HeroAttack::shootDelayTicks),
            Codec.FLOAT.optionalFieldOf("projectile_speed", 1.2f).forGetter(HeroAttack::projectileSpeed),
            Vec3.CODEC.optionalFieldOf("projectile_origin", DEFAULT_PROJECTILE_ORIGIN).forGetter(HeroAttack::projectileOrigin),
            ParticleTypes.CODEC.optionalFieldOf("projectile_trail", ParticleTypes.FLAME).forGetter(HeroAttack::projectileTrail),
            UltimateEffect.CODEC.listOf().optionalFieldOf("on_attack", List.of()).forGetter(HeroAttack::onAttack),
            UltimateEffect.CODEC.listOf().optionalFieldOf("on_hit", List.of()).forGetter(HeroAttack::onHit)
    ).apply(instance, HeroAttack::new));

    public static final HeroAttack MELEE = new HeroAttack(
            Type.MELEE,
            12.0D,
            20,
            0,
            1.2f,
            DEFAULT_PROJECTILE_ORIGIN,
            ParticleTypes.FLAME,
            List.of(),
            List.of()
    );

    public boolean isRanged() {
        return type == Type.RANGED;
    }

    public void attack(UltimateContext context) {
        onAttack.forEach(effect -> effect.apply(context));
    }

    public void hit(UltimateContext context) {
        onHit.forEach(effect -> effect.apply(context));
    }

    public enum Type implements StringRepresentable {
        MELEE("melee"),
        RANGED("ranged");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String id;

        Type(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }
    }
}
