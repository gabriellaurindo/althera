package com.darksune.althera.common.entity;

import com.darksune.althera.common.academy.AcademyDummies;
import com.darksune.althera.common.item.AltheraItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class TrainingDummyEntity extends Mob {

    public static final String NAME = "Training Dummy";

    private static final float MAX_HEALTH = 1024.0F;

    private static final int IDLE_RESET_TICKS = 60;

    private static final int TICKS_PER_SECOND = 20;

    private boolean managed;

    private long firstHitTick = -1;

    private long lastHitTick;

    private float total;

    private float firstHit;

    public TrainingDummyEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        setNoAi(true);
        setPersistenceRequired();
        setCustomName(Component.literal(NAME));
        setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if (!level().isClientSide && !managed && source.getEntity() instanceof Player player
                && player.isShiftKeyDown() && player.getMainHandItem().isEmpty() && source.getDirectEntity() == player) {
            spawnAtLocation(new ItemStack(AltheraItems.TRAINING_DUMMY.get()));
            discard();
            return false;
        }

        return super.hurt(source, amount);
    }

    public void record(DamageSource source, float amount) {

        long now = level().getGameTime();

        if (firstHitTick < 0 || now - lastHitTick > IDLE_RESET_TICKS) {
            firstHitTick = now;
            firstHit = amount;
            total = 0.0F;
        }

        lastHitTick = now;
        total += amount;

        float seconds = (now - firstHitTick) / (float) TICKS_PER_SECOND;
        String rate = seconds > 0.0F ? String.format(Locale.ROOT, "%.1f/s", (total - firstHit) / seconds) : "...";
        String report = String.format(Locale.ROOT, "§e%.1f §7| §f%.1f total §7| §b%s", amount, total, rate);

        setCustomName(Component.literal(report));

        ServerPlayer attacker = attacker(source.getEntity());

        if (attacker != null) {
            attacker.displayClientMessage(Component.literal(report), true);
        }
    }

    @Nullable
    private static ServerPlayer attacker(@Nullable Entity entity) {

        if (entity instanceof ServerPlayer player) {
            return player;
        }

        return entity instanceof OwnableEntity ownable && ownable.getOwner() instanceof ServerPlayer owner ? owner : null;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        if (getHealth() < getMaxHealth()) {
            setHealth(getMaxHealth());
        }

        if (firstHitTick >= 0 && level().getGameTime() - lastHitTick > IDLE_RESET_TICKS) {
            firstHitTick = -1;
            setCustomName(Component.literal(NAME));
        }

        if (managed && !AcademyDummies.isTracked(this)) {
            discard();
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public boolean shouldBeSaved() {
        return !managed && super.shouldBeSaved();
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOOL_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOOL_BREAK;
    }

}
