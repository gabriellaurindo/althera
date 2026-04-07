package com.darksune.althera.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentTable;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public final class SummonSealItem extends Item {

    public SummonSealItem(final Properties properties) {
        super(properties);
    }

    public static SummonSealItem create() {
        return new SummonSealItem(buildProperties());
    }

    private static Properties buildProperties() {
        return new Properties()
                .stacksTo(1);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (!level.isClientSide) {
            BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
            Player player = context.getPlayer();

            Zombie zombie = EntityType.ZOMBIE.create(level);

            if (zombie != null) {
                zombie.moveTo(
                        pos.getX() + 0.5,
                        pos.getY(),
                        pos.getZ() + 0.5,
                        level.getRandom().nextFloat() * 360F,
                        0
                );

                // 🟢 marca como "do jogador"
                zombie.addTag("friendly_summon");
                zombie.getPersistentData().putUUID("owner", player.getUUID());

                // 🟢 não despawnar
                zombie.setPersistenceRequired();

                // 🧠 limpa targets padrão
                zombie.targetSelector.getAvailableGoals().clear();

                // 🎯 atacar apenas zombies que NÃO são friendly
                zombie.targetSelector.addGoal(
                        1,
                        new NearestAttackableTargetGoal<>(
                                zombie,
                                Monster.class,
                                true,
                                target -> !target.getTags().contains("friendly_summon")
                        )
                );
                zombie.setCustomName(Component.nullToEmpty("Gangue do canudo"));

                zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));

                zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
                zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
                zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
                zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));

// evitar drop
                zombie.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                zombie.setDropChance(EquipmentSlot.HEAD, 0.0F);
                zombie.setDropChance(EquipmentSlot.CHEST, 0.0F);
                zombie.setDropChance(EquipmentSlot.LEGS, 0.0F);
                zombie.setDropChance(EquipmentSlot.FEET, 0.0F);

                zombie.setCanPickUpLoot(false);

                // 🚫 garantir que não ataque o player
                zombie.setTarget(null);

                level.addFreshEntity(zombie);

            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
