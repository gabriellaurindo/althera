package com.darksune.althera;

import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.command.HeroCommand;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.entity.SummonedEntity;
import com.darksune.althera.common.hero.HeroLoader;
import com.darksune.althera.common.registry.AltheraRegistries;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.system.HeroSummonSystem;
import com.darksune.althera.config.AltheraConfig;
import com.darksune.althera.network.SummonPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import static com.darksune.althera.common.util.LightOrbUtil.habilitarEspirito;
import static java.util.Objects.nonNull;

@EventBusSubscriber
@Mod(Althera.MOD_ID)
public final class Althera {

    public static final String MOD_ID = "althera";


    public Althera(final IEventBus modEventBus, final ModContainer modContainer) {
        AltheraRegistries.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, AltheraConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(this::onReloadListeners);
    }

    private void onReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new HeroLoader());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        HeroCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {

        var registrar = event.registrar("1");

        registrar.playToServer(
                SummonPayload.TYPE,
                SummonPayload.STREAM_CODEC,
                (payload, context) -> {

                    final Player player = context.player();
                    final ManaData manaData = ManaData.get(player);
                    final HeroEntity oldSummon = HeroSummonSystem.getSummon(player);
                    if (nonNull(oldSummon)) {
                        oldSummon.remove();
                        return;
                    }

                    if (!manaData.hasEnoughMana(20)) {
                        return;
                    }

                    HeroSummonSystem.spawnSummon(player);
                }
        );
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(
                AltheraEntities.HERO.get(),
                HeroEntity.createAttributes().build()
        );
        event.put(
                AltheraEntities.SUMMONED.get(),
                SummonedEntity.createAttributes().build()
        );
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
        int y = height - 48;

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

    @SubscribeEvent
    public static void onPlayerTick(final EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Level level = player.level();
        if (level.isClientSide) return;
        final HeroData heroData = HeroData.get(player);

        // ⏱️ a cada 2 segundos
        if (player.tickCount % 40 == 0) {
            final ManaData manaData = ManaData.get(player);
            manaData.regenMana(player, level);
        }

        if (player.tickCount % 1200 == 0) { // ⏱️ 1 minuto
            if (heroData.getInterventions() > 0) {
                heroData.setInterventions(heroData.getInterventions() - 1);
                heroData.sync(player);
            }
        }

        if (player.tickCount % 40 == 0) {
            if (heroData.isDefeated() || !heroData.isSummoned()) {
                int newHealth = Math.min(
                        (int) heroData.getHealth() + 2,
                        (int) HeroStatsSystem.getMaxHealth(heroData)
                );

                if (heroData.isDefeated() && newHealth >= HeroStatsSystem.getMaxHealth(heroData)) {
                    heroData.setDefeated(false);

                    player.sendSystemMessage(
                            Component.literal("§aYour summon has recovered and can be summoned again!")
                    );
                }

                heroData.setHealth(newHealth);
                heroData.sync(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        final HeroData heroData = HeroData.get(player);

        if (!heroData.isSummoned()) {
            habilitarEspirito(player);
            return;
        }

        HeroSummonSystem.spawnSummon(player);
    }

    @SubscribeEvent
    public static void onPlayerDamage(final LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getSource().getEntity() == null) {
            return;
        }

        final HeroData heroData = HeroData.get(player);
        if (heroData.getInterventions() >= HeroStatsSystem.getMaxInterventions()) {
            return;
        }

        final HeroEntity hero = HeroSummonSystem.spawnOrMove(player);
        if (hero == null) {
            return;
        }

        float damage = event.getAmount();
        if (damage <= 0) {
            return;
        }

        hero.hurt(event.getSource(), damage);

        heroData.incrementInterventions();
        heroData.sync(player);

        event.setCanceled(true);
        //todo no futuro colocar uma animacao ou algo do genero pra entender que gastou um save
        //player.invulnerableTime = 20;
    }

    @SubscribeEvent
    public static void onHeroDamage(LivingIncomingDamageEvent event) {

        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getEntity() instanceof HeroEntity hero)) return;

        if (hero.isOwnedBy(player)) {
            event.setCanceled(true);
        }
    }
}
