package com.darksune.althera.common.entity;

import com.darksune.althera.common.academy.teacher.AcademyTeachers;
import com.darksune.althera.common.academy.teacher.TeacherDefinition;
import com.darksune.althera.common.academy.teacher.TeacherRegistry;
import com.darksune.althera.network.packet.OpenTeacherPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class TeacherEntity extends PathfinderMob {

    private static final EntityDataAccessor<String> TEACHER =
            SynchedEntityData.defineId(TeacherEntity.class, EntityDataSerializers.STRING);

    private static final float LOOK_DISTANCE = 8.0F;

    public TeacherEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEACHER, "");
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, LOOK_DISTANCE));
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    @Nullable
    public ResourceLocation getTeacherId() {
        return ResourceLocation.tryParse(entityData.get(TEACHER));
    }

    @Nullable
    public TeacherDefinition getDefinition() {
        return TeacherRegistry.get(getTeacherId());
    }

    public void setTeacher(ResourceLocation id, TeacherDefinition definition) {
        entityData.set(TEACHER, id.toString());
        setCustomName(Component.literal(definition.displayName()));
        setCustomNameVisible(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && !AcademyTeachers.isTracked(this)) {
            discard();
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {

        ResourceLocation id = getTeacherId();

        if (id == null) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new OpenTeacherPacket(id));
        }

        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
}
