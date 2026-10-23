package com.darksune.althera.common.entity;

import com.darksune.althera.common.ai.goal.SpellAttackGoal;
import com.darksune.althera.common.block.entity.NobleSigilBlockEntity;
import com.darksune.althera.common.commandseal.skill.OverdriveCommandSealSkill;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRank;
import com.darksune.althera.common.hero.HeroRegistry;
import com.darksune.althera.common.spell.SpellDefinition;
import com.darksune.althera.common.spell.SpellRegistry;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.ultimate.UltimateDefinition;
import com.darksune.althera.common.ultimate.UltimateRegistry;
import com.darksune.althera.common.ultimate.effect.UltimateContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NobleMageEntity extends CasterMob implements Enemy, HeroSummoner {

    private static final EntityDataAccessor<Integer> CAST_TICKS =
            SynchedEntityData.defineId(NobleMageEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SERVANT_SPIRIT =
            SynchedEntityData.defineId(NobleMageEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SERVANT_LEVEL =
            SynchedEntityData.defineId(NobleMageEntity.class, EntityDataSerializers.INT);

    private static final List<ResourceLocation> SPELLS = List.of(
            ResourceLocation.fromNamespaceAndPath("althera", "fire_bolt"),
            ResourceLocation.fromNamespaceAndPath("althera", "frost_shard"),
            ResourceLocation.fromNamespaceAndPath("althera", "lightning_strike")
    );
    public static final List<Integer> SERVANT_LEVELS = List.of(20, 30, 40);
    private static final double BASE_HEALTH = 20.0D;
    private static final double BASE_ATTACK = 1.0D;
    private static final double BASE_ARMOR = 0.0D;
    private static final double SERVANT_STAT_SHARE = 0.25D;
    private static final int RECALL_AFTER_TICKS = 200;
    private static final int SPIRIT_REGEN_INTERVAL = 40;
    private static final float MAGE_DAMAGE_SHARE = 0.25F;
    private static final int CAST_POSE_EXTRA_TICKS = 5;
    private static final Vec3 HAND_OFFSET = new Vec3(0.35D, 1.35D, 0.6D);
    private static final int SEAL_CHARGES = 3;
    private static final int REVIVE_DELAY_TICKS = 20;
    private static final float ULTIMATE_HEALTH_RATIO = 0.5F;
    private static final float OVERDRIVE_HEALTH_RATIO = 0.3F;
    private static final float HEAL_HEALTH_RATIO = 0.2F;
    private static final int SIGIL_RADIUS = 16;
    private static final int SIGIL_CHECK_INTERVAL = 100;

    @Nullable
    private ResourceLocation servantHeroId;
    private int servantLevel = SERVANT_LEVELS.getFirst();
    private float servantHealth = -1.0F;
    private boolean servantFallen;
    @Nullable
    private HeroEntity servant;
    @Nullable
    private ResourceLocation spellId;
    @Nullable
    private BlockPos sigilPos;
    private int ticksWithoutTarget;
    private int sealCharges = SEAL_CHARGES;
    private boolean healUsed;
    private boolean reviveUsed;
    private boolean overdriveUsed;
    private boolean ultimateUsed;
    private long lastDay = -1L;
    private int reviveTicks;
    private int overdriveTicks;
    private int ultimateTicks;

    public NobleMageEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 60;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK);
    }

    public static boolean isPureblood(final Entity entity) {
        return entity instanceof NobleMageEntity
                || entity instanceof HeroEntity hero && hero.getSummoner() instanceof NobleMageEntity;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CAST_TICKS, 0);
        builder.define(SERVANT_SPIRIT, false);
        builder.define(SERVANT_LEVEL, SERVANT_LEVELS.getFirst());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SpellAttackGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, HeroEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnGroupData) {
        final List<HeroDefinition> candidates = HeroRegistry.getRollable().stream()
                .filter(hero -> hero.getRank() == HeroRank.EX)
                .toList();
        if (candidates.isEmpty()) {
            servantFallen = true;
        } else {
            servantHeroId = candidates.get(random.nextInt(candidates.size())).getId();
        }
        setServantLevel(SERVANT_LEVELS.get(random.nextInt(SERVANT_LEVELS.size())));
        spellId = SPELLS.get(random.nextInt(SPELLS.size()));
        applyServantShare();
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private void applyServantShare() {
        final HeroDefinition definition = getServantDefinition();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                BASE_HEALTH + SERVANT_STAT_SHARE * HeroStatsSystem.getMaxHealth(definition, servantLevel, 1.0D));
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                BASE_ATTACK + SERVANT_STAT_SHARE * HeroStatsSystem.getAttack(definition, servantLevel, 1.0D));
        getAttribute(Attributes.ARMOR).setBaseValue(
                BASE_ARMOR + SERVANT_STAT_SHARE * HeroStatsSystem.getArmor(definition, servantLevel, 1.0D));
        setHealth(getMaxHealth());
    }

    public int getServantLevel() {
        return this.entityData.get(SERVANT_LEVEL);
    }

    private void setServantLevel(final int level) {
        servantLevel = level;
        this.entityData.set(SERVANT_LEVEL, level);
    }

    @Nullable
    public SpellDefinition getSpell() {
        return spellId == null ? null : SpellRegistry.get(spellId);
    }

    public int getCastTicks() {
        return this.entityData.get(CAST_TICKS);
    }

    public void startCastPose(final int castTicks) {
        this.entityData.set(CAST_TICKS, castTicks + CAST_POSE_EXTRA_TICKS);
    }

    public void bindToSigil(final BlockPos pos) {
        sigilPos = pos.immutable();
        restrictTo(sigilPos, SIGIL_RADIUS);
    }

    private void checkSigil() {
        if (sigilPos == null || !level().isLoaded(sigilPos)) return;

        if (!(level().getBlockEntity(sigilPos) instanceof NobleSigilBlockEntity sigil)) {
            sigilPos = null;
            clearRestriction();
        } else if (!sigil.claim(this)) {
            discard();
        }
    }

    public boolean isServantInSpirit() {
        return this.entityData.get(SERVANT_SPIRIT);
    }

    @Nullable
    public HeroDefinition getServantDefinition() {
        return servantHeroId == null ? null : HeroRegistry.get(servantHeroId);
    }

    public boolean hasServant() {
        return !servantFallen && reviveTicks == 0 && getServantDefinition() != null;
    }

    @Override
    public LivingEntity asEntity() {
        return this;
    }

    @Override
    public boolean isAlly(final Entity entity) {
        return isPureblood(entity);
    }

    @Override
    public boolean isAlliedTo(final Entity entity) {
        return isAlly(entity) || super.isAlliedTo(entity);
    }

    @Override
    public boolean canAttack(final LivingEntity target) {
        return !isAlly(target) && super.canAttack(target);
    }

    @Override
    public boolean canHarmPlayer(final Player player) {
        return !isAlly(player);
    }

    @Override
    public Vec3 getProjectileOrigin() {
        return bodyRelativePoint(HAND_OFFSET);
    }

    @Nullable
    @Override
    public LivingEntity getEffectOwner() {
        return this;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            if (isServantInSpirit()) {
                spawnSpiritParticle();
            }
            return;
        }

        if (getCastTicks() > 0) {
            this.entityData.set(CAST_TICKS, getCastTicks() - 1);
        }

        if (isDeadOrDying()) return;

        if (tickCount % SIGIL_CHECK_INTERVAL == 0) {
            checkSigil();
            if (isRemoved()) return;
        }

        resetDailySeals();
        tickRevive();
        tickServant();
        tickSeals();
        tickUltimate();
        tickOverdrive();
        this.entityData.set(SERVANT_SPIRIT, hasServant() && servant == null);
    }

    private void tickServant() {
        if (servant != null && servant.isRemoved()) {
            servant = null;
        }
        if (!hasServant()) return;

        final LivingEntity target = getTarget();
        ticksWithoutTarget = target == null ? ticksWithoutTarget + 1 : 0;

        if (servant == null) {
            if (target != null && target.isAlive()) {
                manifestServant(target);
            } else if (tickCount % SPIRIT_REGEN_INTERVAL == 0) {
                servantHealth = Math.min(getServantHealth() + 1.0F, getServantMaxHealth());
            }
            return;
        }

        servantHealth = servant.getHealth();

        if (target != null && servant.getTarget() == null) {
            servant.commandAttack(target);
        } else if (ticksWithoutTarget >= RECALL_AFTER_TICKS && servant.getTarget() == null
                && ultimateTicks == 0 && overdriveTicks == 0) {
            recallServant();
        }
    }

    private void resetDailySeals() {
        final long day = level().getDayTime() / 24000L;
        if (day == lastDay) return;

        lastDay = day;
        sealCharges = SEAL_CHARGES;
        healUsed = false;
        reviveUsed = false;
        overdriveUsed = false;
        ultimateUsed = false;
    }

    private boolean useSealCharge() {
        if (sealCharges <= 0) return false;
        sealCharges--;
        return true;
    }

    private void tickSeals() {
        if (!hasServant()) return;

        final float healthRatio = getServantHealth() / getServantMaxHealth();

        if (!ultimateUsed && healthRatio <= ULTIMATE_HEALTH_RATIO) {
            startUltimate();
        }

        if (!overdriveUsed && reviveUsed && healthRatio <= OVERDRIVE_HEALTH_RATIO && useSealCharge()) {
            overdriveUsed = true;
            startOverdrive();
        } else if (!healUsed && healthRatio <= HEAL_HEALTH_RATIO && useSealCharge()) {
            healUsed = true;
            healServant();
        }
    }

    private void healServant() {
        servantHealth = getServantMaxHealth();
        if (servant != null) {
            servant.setHealth(servant.getMaxHealth());
            servantHealth = servant.getHealth();
        }
        playSealEffects(servant != null ? servant : this);
    }

    private void tickRevive() {
        if (reviveTicks == 0 || --reviveTicks > 0) return;

        servantHealth = getServantMaxHealth();
        final LivingEntity target = getTarget();
        if (target != null && target.isAlive()) {
            manifestServant(target);
        }
    }

    private void startOverdrive() {
        overdriveTicks = OverdriveCommandSealSkill.DURATION_TICKS;
        if (servant != null) {
            OverdriveCommandSealSkill.empower(servant, overdriveTicks);
        }
        playSealEffects(servant != null ? servant : this);
    }

    private void tickOverdrive() {
        if (overdriveTicks == 0 || --overdriveTicks > 0) return;

        if (servant != null) {
            servant.kill();
        } else if (hasServant()) {
            defeatServant();
        }
    }

    @Nullable
    private UltimateDefinition getServantUltimate() {
        final HeroDefinition definition = getServantDefinition();
        return definition == null ? null : UltimateRegistry.get(definition.getUltimate());
    }

    @Nullable
    private LivingEntity getUltimateTarget() {
        if (getTarget() != null) return getTarget();
        return servant == null ? null : servant.getTarget();
    }

    private void startUltimate() {
        final UltimateDefinition ultimate = getServantUltimate();
        final LivingEntity target = getUltimateTarget();
        if (servant == null || target == null || ultimate == null) return;

        ultimateUsed = true;
        ultimateTicks = ultimate.channelTicks();
        lookAt(target, 360.0F, 360.0F);
        startCastPose(ultimate.channelTicks());
        ultimate.start(UltimateContext.of(this, servant));
    }

    private void tickUltimate() {
        if (ultimateTicks == 0) return;

        final UltimateDefinition ultimate = getServantUltimate();
        if (servant == null || ultimate == null) {
            ultimateTicks = 0;
            return;
        }

        final LivingEntity target = getUltimateTarget();
        if (target != null) {
            lookAt(target, 360.0F, 360.0F);
        }

        final UltimateContext context = UltimateContext.of(this, servant);
        if (--ultimateTicks > 0) {
            ultimate.tick(context);
        } else {
            ultimate.end(context);
        }
    }

    private void playSealEffects(final LivingEntity at) {
        if (!(level() instanceof ServerLevel serverLevel)) return;

        serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, at.getX(), at.getY() + at.getBbHeight() * 0.5D, at.getZ(),
                40, 0.5, 0.8, 0.5, 0.3);
        level().playSound(null, at.getX(), at.getY(), at.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, getSoundSource(), 1.0F, 1.0F);
    }

    private float getServantMaxHealth() {
        return (float) HeroStatsSystem.getMaxHealth(getServantDefinition(), servantLevel, 1.0D);
    }

    private float getServantHealth() {
        return servantHealth < 0 ? getServantMaxHealth() : servantHealth;
    }

    private void manifestServant(final LivingEntity target) {
        final HeroDefinition definition = getServantDefinition();
        final HeroEntity hero = AltheraEntities.HERO.get().create(level());
        if (definition == null || hero == null) return;

        hero.setSummoner(this);
        HeroStatsSystem.applyServantAttributes(hero, definition, servantLevel, getServantHealth());
        final Vec3 side = Vec3.directionFromRotation(0, yBodyRot + 90.0F).scale(1.5D);
        hero.moveTo(getX() + side.x, getY(), getZ() + side.z, getYRot(), 0.0F);
        if (!level().noCollision(hero)) {
            hero.moveTo(getX(), getY(), getZ(), getYRot(), 0.0F);
        }
        level().addFreshEntity(hero);
        servant = hero;
        if (overdriveTicks > 0) {
            OverdriveCommandSealSkill.empower(hero, overdriveTicks);
        }
        hero.commandAttack(target);
        playServantShift(hero, SoundEvents.ILLUSIONER_MIRROR_MOVE);
    }

    private void recallServant() {
        if (servant == null) return;

        servantHealth = servant.getHealth();
        playServantShift(servant, SoundEvents.ILLUSIONER_PREPARE_MIRROR);
        servant.discard();
        servant = null;
    }

    private void playServantShift(final HeroEntity hero, final SoundEvent sound) {
        if (!(level() instanceof ServerLevel serverLevel)) return;

        serverLevel.sendParticles(ParticleTypes.END_ROD, hero.getX(), hero.getY() + hero.getBbHeight() * 0.5D, hero.getZ(),
                24, 0.4, 0.6, 0.4, 0.08);
        level().playSound(null, hero.getX(), hero.getY(), hero.getZ(), sound, getSoundSource(), 1.0F, 1.0F);
    }

    private void spawnSpiritParticle() {
        final Vec3 side = Vec3.directionFromRotation(0, yBodyRot + 90.0F).scale(1.2D);
        level().addParticle(ParticleTypes.END_ROD,
                getX() + side.x, getY() + 2.0D, getZ() + side.z,
                (random.nextDouble() - 0.5D) * 0.02D,
                (random.nextDouble() - 0.5D) * 0.02D,
                (random.nextDouble() - 0.5D) * 0.02D);
    }

    @Override
    public boolean hurt(final DamageSource source, final float amount) {
        if (level().isClientSide || !hasServant() || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurt(source, amount);
        }

        if (!super.hurt(source, amount * MAGE_DAMAGE_SHARE)) return false;

        damageServant(amount * (1.0F - MAGE_DAMAGE_SHARE));

        if (hasServant() && source.getEntity() instanceof LivingEntity attacker && canAttack(attacker)) {
            if (servant != null) {
                servant.commandAttack(attacker);
            } else if (isAlive()) {
                manifestServant(attacker);
            }
        }
        return true;
    }

    private void damageServant(final float amount) {
        if (servant != null) {
            final float health = servant.getHealth() - amount;
            if (health <= 0) {
                servant.kill();
            } else {
                servant.setHealth(health);
                servantHealth = health;
            }
            return;
        }

        servantHealth = getServantHealth() - amount;
        if (servantHealth <= 0) {
            defeatServant();
        }
    }

    @Override
    public void onServantDefeated(final HeroEntity defeated) {
        if (defeated != servant) return;
        defeatServant();
    }

    private void defeatServant() {
        servant = null;
        servantHealth = 0.0F;
        overdriveTicks = 0;
        ultimateTicks = 0;

        if (!reviveUsed && useSealCharge()) {
            reviveUsed = true;
            reviveTicks = REVIVE_DELAY_TICKS;
            playSealEffects(this);
            return;
        }
        servantFallen = true;
    }

    @Override
    public void die(final DamageSource source) {
        super.die(source);
        if (sigilPos != null && level().getBlockEntity(sigilPos) instanceof NobleSigilBlockEntity sigil) {
            sigil.onMageDied(this);
        }
    }

    @Override
    public void remove(final RemovalReason reason) {
        if (servant != null && !level().isClientSide) {
            servant.discard();
            servant = null;
        }
        super.remove(reason);
    }

    @Override
    public boolean removeWhenFarAway(final double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (servantHeroId != null) {
            compound.putString("ServantHero", servantHeroId.toString());
        }
        compound.putInt("ServantLevel", servantLevel);
        compound.putFloat("ServantHealth", servant != null ? servant.getHealth() : servantHealth);
        compound.putBoolean("ServantFallen", servantFallen);
        if (spellId != null) {
            compound.putString("Spell", spellId.toString());
        }
        if (sigilPos != null) {
            compound.put("Sigil", NbtUtils.writeBlockPos(sigilPos));
        }
        compound.putInt("SealCharges", sealCharges);
        compound.putBoolean("HealUsed", healUsed);
        compound.putBoolean("ReviveUsed", reviveUsed);
        compound.putBoolean("OverdriveUsed", overdriveUsed);
        compound.putBoolean("UltimateUsed", ultimateUsed);
        compound.putLong("LastDay", lastDay);
        compound.putInt("ReviveTicks", reviveTicks);
        compound.putInt("OverdriveTicks", overdriveTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        servantHeroId = compound.contains("ServantHero") ? ResourceLocation.tryParse(compound.getString("ServantHero")) : null;
        setServantLevel(compound.getInt("ServantLevel"));
        servantHealth = compound.contains("ServantHealth") ? compound.getFloat("ServantHealth") : -1.0F;
        servantFallen = compound.getBoolean("ServantFallen");
        spellId = compound.contains("Spell") ? ResourceLocation.tryParse(compound.getString("Spell")) : null;
        sigilPos = NbtUtils.readBlockPos(compound, "Sigil").orElse(null);
        if (sigilPos != null) {
            restrictTo(sigilPos, SIGIL_RADIUS);
        }
        sealCharges = compound.contains("SealCharges") ? compound.getInt("SealCharges") : SEAL_CHARGES;
        healUsed = compound.getBoolean("HealUsed");
        reviveUsed = compound.getBoolean("ReviveUsed");
        overdriveUsed = compound.getBoolean("OverdriveUsed");
        ultimateUsed = compound.getBoolean("UltimateUsed");
        lastDay = compound.contains("LastDay") ? compound.getLong("LastDay") : -1L;
        reviveTicks = compound.getInt("ReviveTicks");
        overdriveTicks = compound.getInt("OverdriveTicks");
    }
}
