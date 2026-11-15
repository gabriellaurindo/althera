package com.darksune.althera.common.entity;

import com.darksune.althera.common.ultimate.effect.UltimateContext;
import com.darksune.althera.common.ultimate.effect.UltimateEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SpellCaster {

    LivingEntity entity();

    boolean canHarmPlayer(Player player);

    Vec3 getProjectileOrigin();

    @Nullable
    LivingEntity getEffectOwner();

    float getSpellPower();

    default void triggerUltimateAnimation() {
    }

    default boolean isAllyOf(final Entity entity) {
        return entity().isAlliedTo(entity);
    }

    default DamageSource attackSource() {
        final LivingEntity entity = entity();
        return entity instanceof Player player
                ? entity.damageSources().playerAttack(player)
                : entity.damageSources().mobAttack(entity);
    }

    default void applyEffects(final List<UltimateEffect> effects, @Nullable final LivingEntity target,
                              @Nullable final Vec3 impact) {
        final LivingEntity owner = getEffectOwner();
        if (owner == null || effects.isEmpty()) return;

        final UltimateContext context = UltimateContext.attack(owner, this, target, impact);
        effects.forEach(effect -> effect.apply(context));
    }

    @Nullable
    static SpellCaster of(@Nullable final Entity entity) {
        if (entity instanceof CasterMob caster) return caster;
        if (entity instanceof Player player) return new PlayerCaster(player);
        return null;
    }
}
