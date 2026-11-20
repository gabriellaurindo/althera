package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public record DashEffect(double distance) implements UltimateEffect {

    public static final MapCodec<DashEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("distance").forGetter(DashEffect::distance)
    ).apply(instance, DashEffect::new));

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.DASH;
    }

    @Override
    public void apply(UltimateContext context) {

        LivingEntity hero = context.caster().entity();
        Vec3 end = context.reach(distance);
        float yaw = (float) (Mth.atan2(context.direction().z, context.direction().x) * Mth.RAD_TO_DEG) - 90.0F;

        if (hero instanceof Mob mob) {
            mob.getNavigation().stop();
        }
        hero.teleportTo(end.x, end.y, end.z);
        hero.setYRot(yaw);
        hero.setYHeadRot(yaw);
        hero.setYBodyRot(yaw);
        hero.resetFallDistance();
    }
}
