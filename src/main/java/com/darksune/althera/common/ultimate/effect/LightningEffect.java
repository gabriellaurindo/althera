package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public record LightningEffect() implements UltimateEffect {

    public static final MapCodec<LightningEffect> CODEC = MapCodec.unit(LightningEffect::new);

    @Override
    public UltimateEffectType type() {
        return UltimateEffectType.LIGHTNING;
    }

    @Override
    public void apply(UltimateContext context) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(context.level());
        if (bolt == null) return;

        Vec3 position = context.position();
        bolt.moveTo(position.x, position.y, position.z);
        bolt.setVisualOnly(true);
        context.level().addFreshEntity(bolt);
    }
}
