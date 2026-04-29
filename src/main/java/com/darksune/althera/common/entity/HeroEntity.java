package com.darksune.althera.common.entity;

import com.darksune.althera.common.ai.goal.AssistOwnerGoal;
import com.darksune.althera.common.ai.goal.ProtectOwnerGoal;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.system.HeroStatsSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

import static com.darksune.althera.common.util.LightOrbUtil.habilitarEspirito;

public class HeroEntity extends PathfinderMob implements GeoEntity, OwnableEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID owner;

    public HeroEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 0;

        // nome
        this.setCustomName(Component.literal("Hero"));
        this.setCustomNameVisible(true);
        // não pega loot
        this.setCanPickUpLoot(false);
        // Nao da despawn
        this.setPersistenceRequired();
    }

    public void setOwner(final UUID owner) {
        this.owner = owner;
    }

    //todo otimizar isso deppois buscando byuuid
    //  return this.level().getPlayerByUUID(ownerUUID);
    //fazer isso em todas as classes que tem owner
    public Player getOwner() {
        if (owner == null) return null;
        return level().getServer().getPlayerList().getPlayer(owner);
    }

    public boolean isOwnedBy(Player player) {
        return player.getUUID().equals(this.getOwnerUUID());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(
                        this,
                        "controller",
                        0,
                        state -> {
                            if (state.isMoving()) {
                                return state.setAndContinue(
                                        RawAnimation.begin().thenLoop("walk")
                                );
                            }

                            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
                        }
                )
        );
    }
    //todo melhorar a movimentacao da entidade na agua, ela ta afundando

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        if (owner != null) {
            compound.putUUID("Owner", owner);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.hasUUID("Owner")) {
            owner = compound.getUUID("Owner");
        }
    }

    @Override
    public void tick() {
        super.tick();

        Level level = level();
        if (level.isClientSide) return;

        final Player owner = getOwner();
        if (owner == null) return;

        final HeroData heroData = HeroData.get(owner);

        // =========================
        // 🟣 REGEN / MANA (2s)
        // =========================
        if (tickCount % 40 == 0) {
            handleManaAndRegen(owner, heroData);
        }

        // =========================
        // 🟢 SYNC DE VIDA (quando muda)
        // =========================
        syncHealthIfChanged(owner, heroData);

        // =========================
        // 🔵 TELEPORTE
        // =========================
        handleTeleport(owner);

        // Zupi :)
        if (!this.getUUID().equals(heroData.getSummonUUID())) {
            remove(false);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2,
                new ProtectOwnerGoal<>(this)
        );
        this.targetSelector.addGoal(3,
                new AssistOwnerGoal<>(this)
        );
    }

    @Override
    public void die(final DamageSource source) {
        if (level().isClientSide) {
            super.die(source);
            return;
        }
        if (getOwner() != null) {
            final HeroData heroData = HeroData.get(getOwner());
            heroData.clearSummon();
            heroData.setDefeated(true);
            heroData.sync(getOwner());
            habilitarEspirito(this.getOwner());
            getOwner().sendSystemMessage(
                    Component.literal("§cYour summon has been defeated! It will recover over time.")
            );
        }
        super.die(source);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isAlliedTo(final Entity entity) {
        if (entity == getOwner()) return true;
        return super.isAlliedTo(entity);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return owner;
    }

    @Override
    public boolean canUsePortal(boolean isNetherPortal) {
        return false;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_WALL) ||
                source.is(DamageTypes.DROWN) ||
                source.is(DamageTypes.CRAMMING)) {
            return false;
        }

        return super.hurt(source, amount);
    }

    private void handleManaAndRegen(Player owner, HeroData heroData) {
        final ManaData manaData = ManaData.get(owner);

        int cost = 20;

        if (manaData.getMana() < cost) {
            owner.sendSystemMessage(Component.literal("Not enough mana! Summon dismissed."));
            remove(true);
            return;
        }

        manaData.consumeMana(owner, cost);

        if (getHealth() < HeroStatsSystem.getMaxHealth(heroData)) {
            heal(1.0F);
        }
    }

    private void syncHealthIfChanged(Player owner, HeroData heroData) {
        double current = this.getHealth();
        double saved = heroData.getHealth();

        if (current != saved) {
            heroData.setHealth(current);
            heroData.sync(owner);
        }
    }

    private void handleTeleport(Player owner) {
        double distance = distanceTo(owner);

        if (distance > 30) {
            teleportTo(
                    owner.getX() + (level().getRandom().nextDouble() - 0.5) * 2,
                    owner.getY(),
                    owner.getZ() + (level().getRandom().nextDouble() - 0.5) * 2
            );
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 1.25D);
    }

    public static HeroEntity create(final Player player) {
        final HeroEntity hero = AltheraEntities.HERO.get().create(player.level());
        HeroStatsSystem.applyAttributes(hero, player);
        return hero;
    }

    public void remove(final boolean habilitarEspirito) {
        if (getOwner() != null) {
            final HeroData heroData = HeroData.get(getOwner());
            heroData.clearSummon();
            heroData.sync(getOwner());
            if (habilitarEspirito) {
                habilitarEspirito(getOwner());
            }
        }
        this.discard();
    }
}
