package com.darksune.althera.common.entity;

import com.darksune.althera.common.ai.goal.AssistOwnerGoal;
import com.darksune.althera.common.ai.goal.ProtectOwnerGoal;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID owner;

    public HeroEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 0;

        // nome
        this.setCustomName(Component.literal("Gangue do canudo"));
        this.setCustomNameVisible(true);

        // arma
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
        // não dropa arma
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);

        // não pega loot
        this.setCanPickUpLoot(false);
        // Nao da despawn
        this.setPersistenceRequired();
    }

    public void setOwner(final UUID owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        if (owner == null) return null;
        return level().getPlayerByUUID(owner);
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

                            return PlayState.STOP;
                        }
                )
        );
    }

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
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(heroData.getMaxHealth());
        heroData.sync(owner);
        // ⏱️ roda a cada 2 segundos
        if (tickCount % 40 == 0) {
            final ManaData manaData = ManaData.get(owner);

            int cost = 20;

            if (manaData.getMana() < cost) {
                owner.sendSystemMessage(Component.literal("Sem mana! Servo desapareceu."));
                discard();
                habilitarEspirito(owner);
                return;
            }
            manaData.consumeMana(owner, cost);


            if (getHealth() < heroData.getMaxHealth()) {
                heal(1.0F); // cura 1 de vida
                heroData.setHealth(this.getHealth());
                heroData.sync(owner);
            }
        }

        // 🧠 teleporte (mantém separado)
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
    protected void registerGoals() {
        super.registerGoals();

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1,
                new ProtectOwnerGoal<>(this)
        );
        this.targetSelector.addGoal(2,
                new AssistOwnerGoal<>(this)
        );
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
    }

    @Override
    public void die(final DamageSource source) {
        if (level().isClientSide) {
            super.die(source);
            return;
        }
        syncHealth();
        habilitarEspirito(this.getOwner());
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

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return owner;
    }

    public static HeroEntity create(final Level level, final Player player) {
        final HeroEntity hero = AltheraEntities.HERO.get().create(level);
        final HeroData heroData = HeroData.get(player);
        heroData.setAttributes(hero, player);
        return hero;
    }

    public void remove() {
        syncHealth();
        this.discard();
    }

    public void syncHealth() {
        final HeroData heroData = HeroData.get(getOwner());
        heroData.setHealth(this.getHealth());
        heroData.sync(getOwner());
    }
}
