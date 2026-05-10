package com.darksune.althera.common.entity;

import com.darksune.althera.common.ai.goal.AssistOwnerGoal;
import com.darksune.althera.common.ai.goal.FollowOwnerGoal;
import com.darksune.althera.common.ai.goal.HeroMeleeAttackGoal;
import com.darksune.althera.common.ai.goal.ProtectOwnerGoal;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRegistry;
import com.darksune.althera.common.system.HeroStatsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

import static com.darksune.althera.common.util.LightOrbUtil.habilitarEspirito;

public class HeroEntity extends PathfinderMob implements GeoEntity, OwnableEntity {

    private static final EntityDataAccessor<String> HERO_ID =
            SynchedEntityData.defineId(
                    HeroEntity.class,
                    EntityDataSerializers.STRING
            );
    private static final EntityDataAccessor<Integer> ATTACK_TICKS =
            SynchedEntityData.defineId(
                    HeroEntity.class,
                    EntityDataSerializers.INT
            );

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID ownerUuid;

    boolean searchingForLand;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

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
        this.moveControl = new HeroEntityMoveControl(this);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }

    public void setOwnerUuid(final UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public Player getOwner() {
        if (ownerUuid == null) return null;
        return this.level().getPlayerByUUID(ownerUuid);
    }

    public boolean isOwnedBy(Player player) {
        return player.getUUID().equals(this.getOwnerUUID());
    }

    public void setHeroId(final ResourceLocation heroId) {
        this.entityData.set(HERO_ID, heroId.toString());
    }

    public ResourceLocation getHeroId() {

        String raw = this.entityData.get(HERO_ID);

        if (raw.isEmpty()) {
            return null;
        }

        return ResourceLocation.parse(raw);
    }

    public HeroDefinition getHeroDefinition() {

        ResourceLocation id = getHeroId();

        return id != null
                ? HeroRegistry.get(id)
                : null;
    }

    public int getAttackAnimationTicks() {
        return this.entityData.get(ATTACK_TICKS);
    }

    public void setAttackAnimationTicks(int ticks) {
        this.entityData.set(ATTACK_TICKS, ticks);
    }

    public void triggerAttackAnimation() {
        setAttackAnimationTicks(10);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        // =========================
        // MOVEMENT
        // =========================

        controllers.add(
                new AnimationController<>(
                        this,
                        "movement",
                        5,
                        state -> {

                            if (state.isMoving()) {
                                state.getController().setAnimationSpeed(2D);
                                return state.setAndContinue(RawAnimation.begin().thenLoop("walk")
                                );
                            }

                            state.getController().setAnimationSpeed(1D);
                            return state.setAndContinue(RawAnimation.begin().thenLoop("idle")
                            );
                        }
                )
        );

        // =========================
        // ATTACK
        // =========================

        controllers.add(
                new AnimationController<>(
                        this,
                        "attack",
                        0,
                        state -> {

                            if (getAttackAnimationTicks() > 0) {
                                state.getController().setAnimationSpeed(1.2D);
                                state.getController().setAnimation(RawAnimation.begin().thenPlay("attack"));
                                return PlayState.CONTINUE;
                            }

                            state.getController().forceAnimationReset();
                            return PlayState.STOP;
                        }
                )
        );
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        if (ownerUuid != null) {
            compound.putUUID("Owner", ownerUuid);
        }

        if (getHeroId() != null) {
            compound.putString(
                    "HeroId",
                    getHeroId().toString()
            );
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.hasUUID("Owner")) {
            ownerUuid = compound.getUUID("Owner");
        }

        if (compound.contains("HeroId")) {
            setHeroId(ResourceLocation.parse(compound.getString("HeroId")));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(HERO_ID, "");
        builder.define(ATTACK_TICKS, 0);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = level();
        if (level.isClientSide) return;

        if (getAttackAnimationTicks() > 0) {
            setAttackAnimationTicks(getAttackAnimationTicks() - 1);
        }

        final Player owner = getOwner();
        if (owner == null) return;

        final HeroData heroData = HeroData.get(owner);

        final HeroDefinition definition = heroData.getHeroDefinition();

        if (definition != null) {
            ResourceLocation newId = definition.getId();

            if (!newId.equals(this.getHeroId())) {
                this.setHeroId(newId);
            }
        }

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
            this.discard();
        }
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(1, new HeroEntitySwimUpGoal(this, 1.0, this.level().getSeaLevel()));
        this.goalSelector.addGoal(2, new HeroMeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.1D, 5F, 2F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

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
        return ownerUuid;
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

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }

    private void handleManaAndRegen(Player owner, HeroData heroData) {
        final ManaData manaData = ManaData.get(owner);

        int cost = 20;

        if (manaData.getMana() < cost) {
            owner.sendSystemMessage(Component.literal("Not enough mana! Summon dismissed."));
            remove();
            return;
        }

//        manaData.consumeMana(owner, cost);

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
                .add(Attributes.ATTACK_DAMAGE, 1.25D)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    public static HeroEntity create(final Player player) {
        final HeroEntity hero = AltheraEntities.HERO.get().create(player.level());
        final HeroData heroData = HeroData.get(player);
        final HeroDefinition definition = heroData.getHeroDefinition();

        if (definition != null) {
            hero.setHeroId(definition.getId());
        }
        HeroStatsSystem.applyAttributes(hero, player);
        return hero;
    }

    public void remove() {
        if (getOwner() != null) {
            final HeroData heroData = HeroData.get(getOwner());
            heroData.clearSummon();
            heroData.sync(getOwner());
            habilitarEspirito(getOwner());
        }
        this.discard();
    }

    boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        } else {
            LivingEntity livingentity = this.getTarget();
            return livingentity != null && livingentity.isInWater();
        }
    }

    protected boolean closeToNextPos() {
        Path path = this.getNavigation().getPath();
        if (path != null) {
            BlockPos blockpos = path.getTarget();
            if (blockpos != null) {
                double d0 = this.distanceToSqr((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
                if (d0 < 4.0) {
                    return true;
                }
            }
        }

        return false;
    }

    static class HeroEntityMoveControl extends MoveControl {
        private final HeroEntity heroEntity;

        public HeroEntityMoveControl(HeroEntity heroEntity) {
            super(heroEntity);
            this.heroEntity = heroEntity;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.heroEntity.getTarget();
            if (this.heroEntity.wantsToSwim() && this.heroEntity.isInWater()) {
                if (livingentity != null && livingentity.getY() > this.heroEntity.getY() || this.heroEntity.searchingForLand) {
                    this.heroEntity.setDeltaMovement(this.heroEntity.getDeltaMovement().add(0.0, 0.002, 0.0));
                }

                if (this.operation != Operation.MOVE_TO || this.heroEntity.getNavigation().isDone()) {
                    this.heroEntity.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.heroEntity.getX();
                double d1 = this.wantedY - this.heroEntity.getY();
                double d2 = this.wantedZ - this.heroEntity.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 /= d3;
                float f = (float)(Mth.atan2(d2, d0) * 180.0F / (float)Math.PI) - 90.0F;
                this.heroEntity.setYRot(this.rotlerp(this.heroEntity.getYRot(), f, 90.0F));
                this.heroEntity.yBodyRot = this.heroEntity.getYRot();
                float f1 = (float)(this.speedModifier * this.heroEntity.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.heroEntity.getSpeed(), f1);
                this.heroEntity.setSpeed(f2);
                this.heroEntity.setDeltaMovement(this.heroEntity.getDeltaMovement().add((double)f2 * d0 * 0.005, (double)f2 * d1 * 0.1, (double)f2 * d2 * 0.005));
            } else {
                if (!this.heroEntity.onGround()) {
                    this.heroEntity.setDeltaMovement(this.heroEntity.getDeltaMovement().add(0.0, -0.008, 0.0));
                }

                super.tick();
            }
        }
    }

    public void setSearchingForLand(boolean searchingForLand) {
        this.searchingForLand = searchingForLand;
    }

    static class HeroEntitySwimUpGoal extends Goal {
        private final HeroEntity heroEntity;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public HeroEntitySwimUpGoal(HeroEntity heroEntity, double speedModifier, int seaLevel) {
            this.heroEntity = heroEntity;
            this.speedModifier = speedModifier;
            this.seaLevel = seaLevel;
        }

        @Override
        public boolean canUse() {
            return !this.heroEntity.level().isDay() && this.heroEntity.isInWater() && this.heroEntity.getY() < (double)(this.seaLevel - 2);
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        @Override
        public void tick() {
            if (this.heroEntity.getY() < (double)(this.seaLevel - 1) && (this.heroEntity.getNavigation().isDone() || this.heroEntity.closeToNextPos())) {
                Vec3 vec3 = DefaultRandomPos.getPosTowards(
                        this.heroEntity, 4, 8, new Vec3(this.heroEntity.getX(), (double)(this.seaLevel - 1), this.heroEntity.getZ()), (float) (Math.PI / 2)
                );
                if (vec3 == null) {
                    this.stuck = true;
                    return;
                }

                this.heroEntity.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
            }
        }

        @Override
        public void start() {
            this.heroEntity.setSearchingForLand(true);
            this.stuck = false;
        }

        @Override
        public void stop() {
            this.heroEntity.setSearchingForLand(false);
        }
    }
}
