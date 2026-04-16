package com.darksune.althera;

import com.darksune.althera.common.ModKeybinds;
import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.LightOrbEntity;
import com.darksune.althera.common.entity.SummonedEntity;
import com.darksune.althera.common.entity.SummonedZombieEntity;
import com.darksune.althera.common.registry.AltheraRegistries;
import com.darksune.althera.config.AltheraConfig;
import com.darksune.althera.network.SummonPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

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
                    final ManaData manaData = ManaData.get(player);
                    final SummonedZombieEntity oldSummon = manaData.hasSummon(player, level);
                    if (nonNull(oldSummon)) {
                        oldSummon.discard();
                        habilitarEspirito(player);
                        return;
                    }

                    if (!manaData.hasEnoughMana(20)) {
                        return;
                    }
                    desabilitarEspirito(player);
                    final SummonedZombieEntity zombie = AltheraEntities.SUMMONED_ZOMBIE.get().create(level);

                    if (zombie != null) {
                        zombie.moveTo(
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                player.getYRot(),
                                0
                        );
                        zombie.setOwner(player.getUUID());
                        level.addFreshEntity(zombie);
                    }
                }
        );
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(
                AltheraEntities.SUMMONED_ZOMBIE.get(),
                SummonedZombieEntity.createAttributes().add(Attributes.MAX_HEALTH, 100.0D).build()
        );
        event.put(
                AltheraEntities.SUMMONED.get(),
                SummonedEntity.createAttributes().build()
        );
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {

        if (event.getEntity() instanceof SummonedZombieEntity zombie) {
            handleZombie(zombie);
        }

        if (event.getEntity() instanceof LightOrbEntity orb) {
            handleOrb(orb);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        final ManaData manaData = player.getData(AltheraAttachments.MANA.get());

        int mana = manaData.getMana();
        int manaMax = manaData.getMaxMana();

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int barWidth = 182;
        int barHeight = 5;

        int x = (width - barWidth) / 2;
        int y = height - 32;

        // evita divisão por zero (importante)
        float ratio = manaMax > 0 ? (float) mana / manaMax : 0;
        int filled = (int) (barWidth * ratio);

        GuiGraphics gui = event.getGuiGraphics();

        // fundo
        gui.fill(x, y, x + barWidth, y + barHeight, 0xFF000000);

        // barra
        gui.fill(x, y, x + filled, y + barHeight, 0xFF00BFFF);

        // texto
        gui.drawString(mc.font, mana + "/" + manaMax, x, y - 10, 0xFFFFFF);
    }

    public static void handleZombie(final SummonedZombieEntity zombie) {

        Level level = zombie.level();
        if (level.isClientSide) return;

        final Player owner = zombie.getOwner();

        if (owner == null) return;

        // ⏱️ roda a cada 2 segundos
        if (zombie.tickCount % 40 == 0) {

//            ManaUtil.setDefaultMana(owner);
            final ManaData manaData = ManaData.get(owner);

            int cost = 20;

            if (manaData.getMana() < cost) {
                owner.sendSystemMessage(Component.literal("Sem mana! Servo desapareceu."));
                zombie.discard();
                habilitarEspirito(owner);
                return;
            }

            manaData.consumeMana(owner, cost);
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

    public static void handleOrb(final LightOrbEntity orb) {

        Level level = orb.level();
        if (level.isClientSide) return;

        Player owner = orb.getOwner();
        if (owner == null) return;

        // ⏱️ a cada 4 segundos
        if (orb.tickCount % 80 == 0) {

            // 💪 Força I (amplifier 0 = nível 1)
            owner.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_BOOST,
                    100, // duração (5 segundos)
                    0,
                    false,
                    false,
                    true
            ));

            // 🛡️ Resistência I
            owner.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    100,
                    0,
                    false,
                    false,
                    true
            ));
        }

        // 🧠 teleporte
        double distance = orb.distanceTo(owner);

        if (distance > 30) {
            orb.teleportTo(
                    owner.getX() + (level.getRandom().nextDouble() - 0.5) * 2,
                    owner.getY(),
                    owner.getZ() + (level.getRandom().nextDouble() - 0.5) * 2
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Level level = player.level();
        if (level.isClientSide) return;

        // ⏱️ a cada 2 segundos
        if (player.tickCount % 40 == 0) {
            final ManaData manaData = ManaData.get(player);
            manaData.regenMana(player, level);
        }
    }

    private static void habilitarEspirito(final Player player) {
        Level level = player.level();

        // ❌ já existe? não cria outro
        if (getPlayerOrb(player, level) != null) {
            return;
        }

        LightOrbEntity orb = AltheraEntities.LIGHT_ORB.get().create(level);

        if (orb != null) {
            orb.setPos(player.getX(), player.getY() + 1.5, player.getZ());
            orb.setOwner(player.getUUID());

            level.addFreshEntity(orb);
        }
    }

    private static void desabilitarEspirito(final Player player) {
        final Level level = player.level();

        level.getEntitiesOfClass(LightOrbEntity.class, player.getBoundingBox().inflate(50))
                .stream()
                .filter(o -> player.getUUID().equals(o.getOwner().getUUID()))
                .forEach(Entity::discard);
    }

    private static LightOrbEntity getPlayerOrb(Player player, Level level) {
        return level.getEntitiesOfClass(LightOrbEntity.class, player.getBoundingBox().inflate(50))
                .stream()
                .filter(o -> player.getUUID().equals(o.getOwner().getUUID()))
                .findFirst()
                .orElse(null);
    }
}
