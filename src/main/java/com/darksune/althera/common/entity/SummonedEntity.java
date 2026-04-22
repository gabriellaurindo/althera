package com.darksune.althera.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
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

public class SummonedEntity extends PathfinderMob implements GeoEntity, OwnableEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SummonedEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 0;
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