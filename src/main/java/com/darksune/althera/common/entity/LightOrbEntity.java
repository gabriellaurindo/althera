package com.darksune.althera.common.entity;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.system.HeroStatsSystem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class LightOrbEntity extends Entity {

    private UUID owner;

    public void setOwner(final UUID owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        if (owner == null) return null;
        return level().getPlayerByUUID(owner);
    }

    public LightOrbEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();

        // CLIENT → partículas
        if (level().isClientSide) {
            level().addParticle(ParticleTypes.END_ROD,
                    getX(),
                    getY(),
                    getZ(),
                    (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.02
            );
            return;
        }

        Player player = getOwner();
        if (player == null) {
            discard();
            return;
        }

        // 🧭 direção do player
        float yaw = player.getYRot();

        // converte pra radiano
        double rad = Math.toRadians(yaw);

        double radius = 2.0; // distância lateral
        double height = 2.0;

        // 👉 lado direito do player (perpendicular)
        double offsetX = Math.sin(rad) * radius;
        double offsetZ = -Math.cos(rad) * radius;

        Vec3 target = new Vec3(
                player.getX() + offsetX,
                player.getY() + height,
                player.getZ() + offsetZ
        );

        // movimento suave
        Vec3 direction = target.subtract(position()).scale(0.2);
        setPos(position().add(direction));
        handleOrb();
    }

    public void handleOrb() {

        Level level = level();
        if (level.isClientSide) return;

        Player owner = getOwner();
        if (owner == null) return;

        // ⏱️ a cada 4 segundos
        if (tickCount % 80 == 0) {

            // 💪 Força I (amplifier 0 = nível 1)
            owner.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_BOOST,
                    100, // duração (5 segundos)
                    0,
                    false,
                    false,
                    true
            ));

            // 🛡️ Resistência I
            owner.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    100,
                    0,
                    false,
                    false,
                    true
            ));
        }
        if (tickCount % 40 == 0) {
            final HeroData heroData = HeroData.get(owner);

            int newHealth = Math.min(
                    (int) heroData.getHealth() + 2,
                    (int) HeroStatsSystem.getMaxHealth(heroData.getLevel())
            );

            heroData.setHealth(newHealth);
            heroData.sync(owner);
        }
        // 🧠 teleporte
        double distance = distanceTo(owner);

        if (distance > 30) {
            teleportTo(
                    owner.getX() + (level.getRandom().nextDouble() - 0.5) * 2,
                    owner.getY(),
                    owner.getZ() + (level.getRandom().nextDouble() - 0.5) * 2
            );
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (owner != null) {
            compound.putUUID("Owner", owner);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            owner = compound.getUUID("Owner");
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }
}