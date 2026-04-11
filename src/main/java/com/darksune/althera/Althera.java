package com.darksune.althera;

import com.darksune.althera.common.ModKeybinds;
import com.darksune.althera.common.registry.AltheraRegistries;
import com.darksune.althera.common.util.ManaUtil;
import com.darksune.althera.data.config.AltheraConfig;
import com.darksune.althera.network.SummonPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.UUID;

import static java.util.Objects.nonNull;

@EventBusSubscriber
@Mod(Althera.MOD_ID)
public final class Althera {

    public static final String MOD_ID = "althera";

    public Althera(final IEventBus modEventBus, final ModContainer modContainer) {
        AltheraRegistries.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, AltheraConfig.SPEC);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (ModKeybinds.SUMMON_KEY.consumeClick()) {
            sendSummon();
        }
    }

    private static void sendSummon() {
        Minecraft.getInstance().getConnection().send(new SummonPayload());
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.SUMMON_KEY);
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {

        var registrar = event.registrar("1");

        registrar.playToServer(
                SummonPayload.TYPE,
                SummonPayload.STREAM_CODEC,
                (payload, context) -> {

                    final Player player = context.player();
                    final Level level = player.level();

                    ManaUtil.setDefaultMana(player);


                    final Zombie oldSummon = ManaUtil.hasSummon(player, level);
                    if (nonNull(oldSummon)) {
                        oldSummon.discard();
                        return;
                    }

                    if (ManaUtil.isPlayerSemMana(player, 20)) {
                        return;
                    }

                    final Zombie zombie = EntityType.ZOMBIE.create(level);

                    if (zombie != null) {
                        zombie.moveTo(
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                player.getYRot(),
                                0
                        );

                        // 🟢 marca dono
                        zombie.addTag("friendly_summon");
                        zombie.getPersistentData().putUUID("owner", player.getUUID());

                        zombie.setPersistenceRequired();

                        zombie.targetSelector.getAvailableGoals().clear();

                        zombie.targetSelector.addGoal(
                                1,
                                new NearestAttackableTargetGoal<>(
                                        zombie,
                                        Monster.class,
                                        true,
                                        target -> !target.getTags().contains("friendly_summon")
                                )
                        );

                        zombie.setCustomName(Component.literal("Gangue do canudo"));

                        zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));

                        zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
                        zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
                        zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
                        zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));

                        zombie.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                        zombie.setDropChance(EquipmentSlot.HEAD, 0.0F);
                        zombie.setDropChance(EquipmentSlot.CHEST, 0.0F);
                        zombie.setDropChance(EquipmentSlot.LEGS, 0.0F);
                        zombie.setDropChance(EquipmentSlot.FEET, 0.0F);

                        zombie.setCanPickUpLoot(false);
                        zombie.setTarget(null);

                        level.addFreshEntity(zombie);
                    }
                }
        );
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;

        Level level = zombie.level();
        if (level.isClientSide) return;

        if (!zombie.getTags().contains("friendly_summon")) return;

        // 🧠 pega dono
        if (!zombie.getPersistentData().hasUUID("owner")) return;

        UUID ownerId = zombie.getPersistentData().getUUID("owner");
        Player owner = level.getPlayerByUUID(ownerId);

        if (owner == null) return;

        // ⏱️ roda a cada 2 segundos
        if (zombie.tickCount % 40 == 0) {

            ManaUtil.setDefaultMana(owner);
            int mana = ManaUtil.getMana(owner);

            int cost = 10;

            if (mana < cost) {
                owner.sendSystemMessage(Component.literal("Sem mana! Servo desapareceu."));
                zombie.discard();
                return;
            }

            ManaUtil.consumeMana(owner, cost);

            owner.sendSystemMessage(Component.literal(
                    "Mana: " + ManaUtil.getMana(owner) + "/" + ManaUtil.getMaxMana(owner)
            ));
        }

        // 🧠 teleporte (mantém separado)
        double distance = zombie.distanceTo(owner);

        if (distance > 30) {
            zombie.teleportTo(
                    owner.getX() + (level.getRandom().nextDouble() - 0.5) * 2,
                    owner.getY(),
                    owner.getZ() + (level.getRandom().nextDouble() - 0.5) * 2
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        ManaUtil.setDefaultMana(player);
    }

    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Level level = player.level();
        if (level.isClientSide) return;

        ManaUtil.setDefaultMana(player);

        // ⏱️ a cada 5 segundos
        if (player.tickCount % 40 == 0) {
            ManaUtil.regenMana(player, level);
        }
    }
}
