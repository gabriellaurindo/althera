package com.darksune.althera.common.ultimate.effect;

import com.darksune.althera.common.entity.CasterMob;
import com.darksune.althera.common.entity.HeroProjectileEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record ProjectileEffect(int count, float spread, float speed, float amount, float attackRatio,
                               ParticleOptions trail, List<UltimateEffect> onHit) implements UltimateEffect {

    public static final MapCodec<ProjectileEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ProjectileEffect::count),
            Codec.FLOAT.optionalFieldOf("spread", 0.0F).forGetter(ProjectileEffect::spread),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed", 1.2F).forGetter(ProjectileEffect::speed),
            Codec.FLOAT.optionalFieldOf("amount", 0.0F).forGetter(ProjectileEffect::amount),
            Codec.FLOAT.optionalFieldOf("attack_ratio", 0.0F).forGetter(ProjectileEffect::attackRatio),
            ParticleTypes.CODEC.optionalFieldOf("trail", ParticleTypes.FLAME).forGetter(ProjectileEffect::trail),
            Codec.lazyInitialized(() -> UltimateEffect.CODEC.listOf()).optionalFieldOf("on_hit", List.of())
                    .forGetter(ProjectileEffect::onHit)
    ).apply(instance, ProjectileEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.PROJECTILE;
    }

    @Override
    public void apply(UltimateContext context) {
        CasterMob hero = context.caster();
        Vec3 from = hero.getProjectileOrigin();
        Vec3 aim = context.target() != null
                ? HeroProjectileEntity.aimAt(from, context.target())
                : context.direction();
        float damage = amount + attackRatio * (float) hero.getAttributeValue(Attributes.ATTACK_DAMAGE);

        for (int i = 0; i < count; i++) {
            float angle = count == 1 ? 0 : spread * ((float) i / (count - 1) - 0.5F);
            HeroProjectileEntity.shoot(hero, from, aim.yRot(angle * ((float) Math.PI / 180F)), damage, speed, trail, onHit);
        }
    }
}
