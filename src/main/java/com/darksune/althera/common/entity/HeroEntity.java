package com.darksune.althera.common.entity;

import com.darksune.althera.common.attachment.ManaData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
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

        // 🧭 Seguir o dono (player)
//        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.0D, 2.0F, 10.0F));

        // ⚔️ Atacar inimigos
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));

        // 👀 olhar ao redor
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        // 🚶 andar aleatoriamente (opcional)
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));

        // 🎯 escolher alvo (monstros)
        this.targetSelector.addGoal(1,
                new NearestAttackableTargetGoal<>(
                        this,
                        Monster.class,
                        true
                )
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    //TODO: Temporario
    @Override
    public boolean isAlliedTo(final Entity entity) {
        return entity instanceof Player;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return null;
    }
}
