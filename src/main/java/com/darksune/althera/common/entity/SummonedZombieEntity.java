package com.darksune.althera.common.entity;

import com.darksune.althera.common.attachment.ManaData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.UUID;

import static com.darksune.althera.common.util.LightOrbUtil.habilitarEspirito;

public class SummonedZombieEntity extends Zombie {

    private UUID owner;

    public void setOwner(final UUID owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        if (owner == null) return null;
        return level().getPlayerByUUID(owner);
    }

    public SummonedZombieEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);

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

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes();
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
    protected void registerGoals() {
        super.registerGoals();

        // limpa targets padrão
        this.targetSelector.getAvailableGoals().clear();

        // adiciona comportamento custom
        this.targetSelector.addGoal(
                1,
                new NearestAttackableTargetGoal<>(
                        this,
                        Monster.class,
                        true,
                        target -> !(target instanceof SummonedZombieEntity)
                                && !(target instanceof Player)
                )
        );
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
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }
}
