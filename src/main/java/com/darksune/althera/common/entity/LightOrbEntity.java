package com.darksune.althera.common.entity;

import com.darksune.althera.common.util.LightOrbUtil;
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

    private UUID ownerUuid;

    public void setOwnerUuid(final UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public Player getOwnerUuid() {
        if (ownerUuid == null) return null;
        return level().getPlayerByUUID(ownerUuid);
    }

    public UUID getOwnerUUID() {
        return ownerUuid;
    }

    public LightOrbEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        //todo
        //this.setPersistenceRequired();
    }

    @Override
    public void tick() {
        super.tick();

        // CLIENT → particles
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

        Player player = getOwnerUuid();
        if (player == null || !LightOrbUtil.isActiveOrb(this)) {
            discard();
            return;
        }

        // 🧭 player direction
        float yaw = player.getYRot();

        // convert to radians
        double rad = Math.toRadians(yaw);

        double radius = 2.0; // side distance
        double height = 2.0;

        // 👉 player's right side (perpendicular)
        double offsetX = Math.sin(rad) * radius;
        double offsetZ = -Math.cos(rad) * radius;

        Vec3 target = new Vec3(
                player.getX() + offsetX,
                player.getY() + height,
                player.getZ() + offsetZ
        );

        // smooth movement
        Vec3 direction = target.subtract(position()).scale(0.2);
        setPos(position().add(direction));
        handleOrb();
    }

    public void handleOrb() {

        Level level = level();
        if (level.isClientSide) return;

        Player owner = getOwnerUuid();
        if (owner == null) return;

        // ⏱️ every 4 seconds
        if (tickCount % 80 == 0) {
            // 🛡️ Resistance I
            owner.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    100,
                    0,
                    false,
                    false,
                    true
            ));
        }
        // 🧠 teleport
        double distance = distanceTo(owner);

        if (distance > 30) {
            teleportTo(
                    owner.getX() + (level.getRandom().nextDouble() - 0.5) * 2,
                    owner.getY(),
                    owner.getZ() + (level.getRandom().nextDouble() - 0.5) * 2
            );
        }
    }

    // the orb is recreated from the hero state (login, respawn...), never loaded from the save
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (ownerUuid != null) {
            compound.putUUID("Owner", ownerUuid);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            ownerUuid = compound.getUUID("Owner");
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }
}