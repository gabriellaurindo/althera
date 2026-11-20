package com.darksune.althera.common.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class CasterMob extends PathfinderMob implements SpellCaster {

    protected CasterMob(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public abstract boolean canHarmPlayer(Player player);

    public abstract Vec3 getProjectileOrigin();

    @Nullable
    public abstract LivingEntity getEffectOwner();

    @Override
    public LivingEntity entity() {
        return this;
    }

    @Override
    public float getSpellPower() {
        return (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void triggerUltimateAnimation() {
    }

    protected Vec3 bodyRelativePoint(final Vec3 offset) {
        final float yaw = yBodyRot * Mth.DEG_TO_RAD;
        final double sin = Mth.sin(yaw);
        final double cos = Mth.cos(yaw);
        final Vec3 center = getBoundingBox().getCenter();
        final Vec3 point = position().add(
                -cos * offset.x - sin * offset.z,
                offset.y,
                -sin * offset.x + cos * offset.z
        );
        final BlockHitResult wall = level().clip(
                new ClipContext(center, point, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)
        );
        return wall.getType() == HitResult.Type.MISS
                ? point
                : wall.getLocation().subtract(point.subtract(center).normalize().scale(0.2D));
    }
}
