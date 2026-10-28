package com.darksune.althera.common.entity;

import com.darksune.althera.common.ai.goal.AssistOwnerGoal;
import com.darksune.althera.common.ai.goal.CommandedTargetGoal;
import com.darksune.althera.common.ai.goal.EngageThreatsGoal;
import com.darksune.althera.common.ai.goal.FollowOwnerGoal;
import com.darksune.althera.common.ai.goal.HeroMeleeAttackGoal;
import com.darksune.althera.common.ai.goal.HeroRangedAttackGoal;
import com.darksune.althera.common.ai.goal.ProtectOwnerGoal;
import com.darksune.althera.common.ai.goal.RivalTargetGoal;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.hero.HeroAnimation;
import com.darksune.althera.common.hero.HeroAttack;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRegistry;
import com.darksune.althera.common.hero.HeroStats;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static com.darksune.althera.common.util.LightOrbUtil.habilitarEspirito;

public class HeroEntity extends CasterMob implements GeoEntity, OwnableEntity {

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
    private static final EntityDataAccessor<Boolean> DISGUISED =
            SynchedEntityData.defineId(
                    HeroEntity.class,
                    EntityDataSerializers.BOOLEAN
            );
    private static final EntityDataAccessor<Float> HEALTH_RATIO =
            SynchedEntityData.defineId(
                    HeroEntity.class,
                    EntityDataSerializers.FLOAT
            );
    private static final float MASKED_MAX_HEALTH = 20.0F;
    private static final int REVEAL_TICKS = 10;
    private static final int RIVAL_MEMORY_TICKS = 600;
    private static final int COMMANDED_TARGET_TICKS = 600;
    private static final double COMMANDED_TARGET_RANGE = 48.0D;
    private static final String ULTIMATE_CONTROLLER = "ultimate";
    private static final String ULTIMATE_ANIMATION = "ultimate";
    private static final String DEATH_ANIMATION = "death";

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID ownerUuid;
    @Nullable
    private HeroSummoner summoner;
    @Nullable
    private ResourceLocation heroId;
    private int dodgeTick = -1;
    private int revealTicks;
    private boolean benchmark;
    @Nullable
    private UUID rivalOwner;
    private int rivalUntil;
    @Nullable
    private LivingEntity commandedTarget;
    private int commandedUntil;

    boolean searchingForLand;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public HeroEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 0;

        // name
        this.setCustomName(Component.literal("Hero"));
        // doesn't pick up loot
        this.setCanPickUpLoot(false);
        // doesn't despawn
        this.setPersistenceRequired();
        this.moveControl = new HeroEntityMoveControl(this);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }

    public void setOwnerUuid(final UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public void setSummoner(final HeroSummoner summoner) {
        this.summoner = summoner;
    }

    @Nullable
    public HeroSummoner getSummoner() {
        return summoner;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        if (summoner != null) return summoner.asEntity();
        if (ownerUuid == null) return null;
        return this.level().getPlayerByUUID(ownerUuid);
    }

    /**
     * Looks up the owner in any dimension; {@link #getOwner()} only sees the hero's current dimension.
     */
    @Nullable
    public Player findOwner() {
        if (ownerUuid == null || !(level() instanceof ServerLevel serverLevel)) {
            return ownerUuid == null ? null : level().getPlayerByUUID(ownerUuid);
        }
        return serverLevel.getServer().getPlayerList().getPlayer(ownerUuid);
    }

    public boolean isOwnedBy(Player player) {
        return player.getUUID().equals(this.getOwnerUUID());
    }

    public void setHeroId(final ResourceLocation heroId) {
        this.heroId = heroId;
        publishHeroId();
    }

    private void publishHeroId() {
        this.entityData.set(HERO_ID, heroId == null || isDisguised() ? "" : heroId.toString());
    }

    public boolean isDisguised() {
        return this.entityData.get(DISGUISED);
    }

    public void setDisguised(final boolean disguised) {
        this.entityData.set(DISGUISED, disguised);
        publishHeroId();
        updateHealthRatio();
    }

    public ResourceLocation getHeroId() {

        if (!level().isClientSide) {
            return heroId;
        }

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

    public HeroAnimation getAnimation() {
        final HeroDefinition definition = getHeroDefinition();
        return definition == null || isDisguised() ? HeroAnimation.DEFAULT : definition.getAnimation();
    }

    public HeroAttack getAttack() {
        final HeroDefinition definition = getHeroDefinition();
        if (definition == null) return HeroAttack.MELEE;
        return isDisguised() ? definition.getAttack().disguised() : definition.getAttack();
    }

    public void performAttackEffects(@Nullable final LivingEntity target) {
        applyEffects(getAttack().onAttack(), target, null);
    }

    public void performHitEffects(@Nullable final LivingEntity target, final Vec3 position) {
        applyEffects(getAttack().onHit(), target, position);
    }

    @Nullable
    @Override
    public LivingEntity getEffectOwner() {
        return summoner != null ? summoner.asEntity() : findOwner();
    }

    @Override
    public boolean doHurtTarget(final Entity target) {
        final boolean hurt = super.doHurtTarget(target);
        if (hurt && target instanceof LivingEntity living) {
            performHitEffects(living, living.position());
        }
        return hurt;
    }

    @Override
    public Vec3 getProjectileOrigin() {
        return bodyRelativePoint(getAttack().projectileOrigin().scale(getModelScale()));
    }

    public void markAsBenchmark() {
        this.benchmark = true;
        this.setNoAi(true);
    }

    public boolean isBenchmark() {
        return benchmark;
    }

    @Override
    public boolean shouldBeSaved() {
        return !benchmark && summoner == null && super.shouldBeSaved();
    }

    public int getAttackAnimationTicks() {
        return this.entityData.get(ATTACK_TICKS);
    }

    public void setAttackAnimationTicks(int ticks) {
        this.entityData.set(ATTACK_TICKS, ticks);
    }

    public void triggerAttackAnimation() {
        setAttackAnimationTicks(getAnimation().attackTicks());
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

                            if (isDeadOrDying() && !isDisguised()) {
                                state.getController().setAnimationSpeed(1D);
                                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold(DEATH_ANIMATION));
                            }

                            if (state.isMoving()) {
                                state.getController().setAnimationSpeed(getAnimation().walkSpeed());
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

                            if (getAttackAnimationTicks() > 0 && !isDeadOrDying() && !isDisguised()) {
                                state.getController().setAnimationSpeed(getAnimation().attackSpeed());
                                state.getController().setAnimation(RawAnimation.begin().thenPlay("attack"));
                                return PlayState.CONTINUE;
                            }

                            state.getController().forceAnimationReset();
                            return PlayState.STOP;
                        }
                )
        );

        controllers.add(
                new AnimationController<>(
                        this,
                        ULTIMATE_CONTROLLER,
                        0,
                        state -> PlayState.STOP
                ).triggerableAnim(ULTIMATE_ANIMATION, RawAnimation.begin().thenPlay(ULTIMATE_ANIMATION))
        );
    }

    @Override
    public void triggerUltimateAnimation() {
        triggerAnim(ULTIMATE_CONTROLLER, ULTIMATE_ANIMATION);
    }

    public float getModelScale() {
        final HeroDefinition definition = getHeroDefinition();
        return definition == null || isDisguised() ? 1.0f : definition.getScale();
    }

    public boolean isPlayingUltimate() {
        final AnimationController<?> controller =
                getAnimatableInstanceCache().getManagerForId(getId()).getAnimationControllers().get(ULTIMATE_CONTROLLER);
        return controller != null && controller.isPlayingTriggeredAnimation();
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
        builder.define(DISGUISED, false);
        builder.define(HEALTH_RATIO, 1.0F);
    }

    @Override
    public float getHealth() {
        return level().isClientSide && isDisguised()
                ? this.entityData.get(HEALTH_RATIO) * MASKED_MAX_HEALTH
                : super.getHealth();
    }

    @Override
    public void setHealth(final float health) {
        super.setHealth(health);
        updateHealthRatio();
    }

    private void updateHealthRatio() {
        if (level().isClientSide) return;
        this.entityData.set(HEALTH_RATIO, getMaxHealth() > 0 ? super.getHealth() / getMaxHealth() : 0.0F);
    }

    private void maskDisguisedStats() {
        if (!isDisguised()) return;
        maskAttribute(Attributes.MAX_HEALTH, MASKED_MAX_HEALTH);
        maskAttribute(Attributes.ARMOR, 0.0D);
        maskAttribute(Attributes.MOVEMENT_SPEED, HeroStats.DEFAULT.speed());
    }

    private void maskAttribute(final Holder<Attribute> attribute, final double value) {
        final AttributeInstance instance = getAttribute(attribute);
        if (instance == null || instance.getValue() == value) return;
        instance.removeModifiers();
        instance.setBaseValue(value);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = level();
        if (level.isClientSide) {
            maskDisguisedStats();
            return;
        }
        if (isDeadOrDying()) return;
        if (benchmark) return;

        if (getAttackAnimationTicks() > 0) {
            setAttackAnimationTicks(getAttackAnimationTicks() - 1);
        }

        if (summoner != null) {
            tickServant(summoner.asEntity());
            return;
        }

        final Player owner = findOwner();
        if (owner == null) {
            // owner offline: don't leave the hero abandoned in the world
            this.discard();
            return;
        }

        final HeroData heroData = HeroData.get(owner);

        // Zupi :)
        if (!this.getUUID().equals(heroData.getSummonUUID())) {
            this.discard();
            return;
        }

        // owner in another dimension: login/respawn/dimension change bring the hero back
        if (owner.level() != level()) return;

        final HeroDefinition definition = heroData.getHeroDefinition();

        if (definition != null) {
            ResourceLocation newId = definition.getId();

            if (!newId.equals(this.getHeroId())) {
                this.setHeroId(newId);
            }
        }

        if (heroData.isDisguised() != isDisguised()) {
            HeroStatsSystem.applyAttributes(this, owner);
        }

        tickReveal(owner);

        // =========================
        // 🟣 REGEN / MANA (2s)
        // =========================
        if (tickCount % 40 == 0) {
            handleManaAndRegen(owner, heroData);
        }

        // =========================
        // 🟢 HEALTH SYNC (when it changes)
        // =========================
        syncHealthIfChanged(owner, heroData);

        // =========================
        // 🔵 TELEPORT
        // =========================
        handleTeleport(owner);
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(1, new HeroEntitySwimUpGoal(this, 1.0, this.level().getSeaLevel()));
        this.goalSelector.addGoal(2, new HeroMeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new HeroRangedAttackGoal(this, 1.1D));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.1D, 5F, 2F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new CommandedTargetGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2,
                new ProtectOwnerGoal<>(this)
        );
        this.targetSelector.addGoal(3,
                new AssistOwnerGoal<>(this)
        );
        this.targetSelector.addGoal(4, new RivalTargetGoal(this));
        this.targetSelector.addGoal(5, new EngageThreatsGoal(this));
    }

    private void tickServant(final LivingEntity master) {
        if (master.isRemoved() || !master.isAlive() || master.level() != level()) {
            discard();
            return;
        }
        handleTeleport(master);
    }

    public void commandAttack(final LivingEntity target) {
        if (target == this || !canAttack(target)) return;
        setTarget(target);
    }

    public void markTarget(final LivingEntity target) {
        commandedTarget = target;
        commandedUntil = tickCount + COMMANDED_TARGET_TICKS;
        setTarget(target);
    }

    @Nullable
    public LivingEntity getCommandedTarget() {
        if (commandedTarget == null) return null;

        if (tickCount > commandedUntil
                || !commandedTarget.isAlive()
                || commandedTarget.level() != level()
                || distanceToSqr(commandedTarget) > COMMANDED_TARGET_RANGE * COMMANDED_TARGET_RANGE
                || !canAttack(commandedTarget)) {
            commandedTarget = null;
        }
        return commandedTarget;
    }

    public static void knockBackAttacker(final LivingEntity attacker, final Entity defended) {
        double dx = attacker.getX() - defended.getX();
        double dz = attacker.getZ() - defended.getZ();
        final double length = Math.sqrt(dx * dx + dz * dz);
        if (length <= 0.001D) return;

        dx /= length;
        dz /= length;
        attacker.push(dx * 1.2D, 0.25D, dz * 1.2D);
        attacker.hurtMarked = true;
    }

    @Override
    public boolean canAttack(final LivingEntity target) {
        if (summoner != null && summoner.isAlly(target)) return false;
        if (target instanceof Player player && !canHarmPlayer(player)) return false;
        return super.canAttack(target);
    }

    @Override
    public boolean canHarmPlayer(final Player player) {
        if (summoner != null) return !summoner.isAlly(player);
        final Player owner = findOwner();
        return owner != null && owner != player && owner.canHarmPlayer(player);
    }

    public void rememberRival(@Nullable final Entity enemy) {
        if (level().isClientSide) return;

        final UUID enemyOwner = enemy instanceof HeroEntity hero ? hero.getOwnerUUID()
                : enemy instanceof Player player ? player.getUUID()
                : null;

        if (enemyOwner == null || enemyOwner.equals(ownerUuid)) return;

        rivalOwner = enemyOwner;
        rivalUntil = tickCount + RIVAL_MEMORY_TICKS;
    }

    @Nullable
    public LivingEntity findRival(final double range) {
        if (rivalOwner == null || !(level() instanceof ServerLevel serverLevel)) return null;

        if (tickCount > rivalUntil) {
            rivalOwner = null;
            return null;
        }

        final Player rivalPlayer = serverLevel.getServer().getPlayerList().getPlayer(rivalOwner);
        if (rivalPlayer == null) return null;

        final HeroEntity rivalHero = HeroSummonSystem.getSummon(rivalPlayer);
        if (isReachableRival(rivalHero, range)) return rivalHero;

        return isReachableRival(rivalPlayer, range) ? rivalPlayer : null;
    }

    private boolean isReachableRival(@Nullable final LivingEntity rival, final double range) {
        return rival != null
                && rival.isAlive()
                && rival.level() == level()
                && distanceToSqr(rival) <= range * range
                && canAttack(rival);
    }

    public boolean isThreat(final Mob mob) {
        if (mob == this || !mob.isAlive() || !canAttack(mob) || isAlliedTo(mob)) return false;

        final LivingEntity mobTarget = mob.getTarget();
        return mob == getLastHurtByMob()
                || mobTarget == this
                || (mobTarget != null && mobTarget == getOwner());
    }

    public Optional<Mob> findClosestThreat(final double radius, final Predicate<Mob> filter) {
        return level().getEntitiesOfClass(Mob.class, getBoundingBox().inflate(radius), mob -> isThreat(mob) && filter.test(mob))
                .stream()
                .min(Comparator.comparingDouble(this::distanceToSqr));
    }

    @Override
    public void die(final DamageSource source) {
        if (level().isClientSide) {
            super.die(source);
            return;
        }
        if (summoner != null) {
            summoner.onServantDefeated(this);
            super.die(source);
            return;
        }
        final Player owner = findOwner();
        if (owner != null) {
            HeroSummonSystem.handleDefeat(owner, this);
        }
        super.die(source);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= getAnimation().deathTicks() && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isAlliedTo(final Entity entity) {
        if (entity == getOwner()) return true;
        if (summoner != null && summoner.isAlly(entity)) return true;
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

        if (tryDodge(source)) {
            return false;
        }

        rememberRival(source.getEntity());

        return super.hurt(source, amount);
    }

    public boolean isRevealing() {
        return revealTicks > 0;
    }

    public void startReveal() {
        if (isRevealing() || !isDisguised()) return;

        revealTicks = REVEAL_TICKS;
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_PREPARE_MIRROR, getSoundSource(), 1.0F, 1.0F);
    }

    private void tickReveal(final Player owner) {
        if (!isRevealing() || !(level() instanceof ServerLevel serverLevel)) return;

        final double progress = 1.0D - (double) revealTicks / REVEAL_TICKS;
        final double angle = tickCount * 0.9D;
        final double height = getY() + getBbHeight() * progress;

        for (int i = 0; i < 2; i++) {
            final double side = angle + Math.PI * i;
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    getX() + Math.cos(side) * 0.6D, height, getZ() + Math.sin(side) * 0.6D, 2, 0.05, 0.05, 0.05, 0.0);
        }
        serverLevel.sendParticles(ParticleTypes.SMOKE, getX(), getY() + getBbHeight() * 0.9D, getZ(), 2, 0.2, 0.1, 0.2, 0.01);

        if (--revealTicks > 0) return;

        HeroSummonSystem.finishReveal(owner, this);
        serverLevel.sendParticles(ParticleTypes.END_ROD, getX(), getY() + getBbHeight() * 0.5D, getZ(), 24, 0.4, 0.6, 0.4, 0.08);
        serverLevel.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.2D, getZ(), 12, 0.5, 0.1, 0.5, 0.03);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, getSoundSource(), 1.0F, 1.0F);
    }

    public void playDisguiseEffects() {
        if (!(level() instanceof ServerLevel serverLevel)) return;

        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY() + getBbHeight() * 0.5D, getZ(), 20, 0.35, 0.6, 0.35, 0.02);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, getSoundSource(), 1.0F, 1.0F);
    }

    public boolean hasDodgedThisTick() {
        return dodgeTick == tickCount;
    }

    private boolean tryDodge(final DamageSource source) {
        if (level().isClientSide || invulnerableTime > 10 || !isDodgeable(source)) return false;

        final HeroDefinition definition = getHeroDefinition();
        final double dodgeChance = definition == null ? 0.0D
                : definition.getHeroClass().getDodgeChance() * (isDisguised() ? HeroStatsSystem.DISGUISE_STAT_MULTIPLIER : 1.0D);
        if (random.nextFloat() >= dodgeChance) return false;

        dodgeTick = tickCount;
        playDodgeEffects();
        return true;
    }

    private boolean isDodgeable(final DamageSource source) {
        final Entity attacker = source.getEntity();
        return attacker != null
                && attacker != getOwner()
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && !source.is(DamageTypeTags.IS_EXPLOSION);
    }

    private void playDodgeEffects() {
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, getSoundSource(), 1.0F, 1.2F);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD, getX(), getY() + getBbHeight() * 0.5, getZ(), 6, 0.3, 0.3, 0.3, 0.02);
        }
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

        int cost = HeroSummonSystem.getManaCost(heroData);

        if (!manaData.hasEnoughMana(owner, cost)) {
            owner.sendSystemMessage(Component.literal("§cNot enough mana! Summon dismissed. Recover half your mana to summon it again."));
            manaData.setExhausted(true);
            manaData.sync(owner);
            HeroSummonSystem.dismissSummon(owner);
            return;
        }
        //todo temp
        manaData.consumeMana(owner, cost);

        if (getHealth() < HeroStatsSystem.getMaxHealth(heroData)) {
            heal((float) HeroStatsSystem.getSummonedRegen(heroData));
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

    private void handleTeleport(LivingEntity owner) {
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
                .add(Attributes.STEP_HEIGHT, 1.0)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
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
        final Player owner = findOwner();
        if (owner != null) {
            final HeroData heroData = HeroData.get(owner);
            heroData.clearSummon();
            heroData.sync(owner);
            habilitarEspirito(owner);
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
