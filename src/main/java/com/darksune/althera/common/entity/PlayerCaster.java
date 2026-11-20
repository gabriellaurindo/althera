package com.darksune.althera.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public record PlayerCaster(Player player) implements SpellCaster {

    public static final float SPELL_POWER = 4.0F;

    private static final double ORIGIN_FORWARD = 0.6D;

    private static final double ORIGIN_DROP = 0.2D;

    @Override
    public LivingEntity entity() {
        return player;
    }

    @Override
    public boolean canHarmPlayer(final Player target) {
        return target != player && player.canHarmPlayer(target);
    }

    @Override
    public Vec3 getProjectileOrigin() {
        return player.getEyePosition()
                .add(player.getLookAngle().scale(ORIGIN_FORWARD))
                .subtract(0, ORIGIN_DROP, 0);
    }

    @Override
    public LivingEntity getEffectOwner() {
        return player;
    }

    @Override
    public float getSpellPower() {
        return SPELL_POWER;
    }

    @Override
    public boolean isAllyOf(final Entity entity) {
        return entity instanceof HeroEntity hero && hero.getOwner() == player
                || player.isAlliedTo(entity);
    }
}
