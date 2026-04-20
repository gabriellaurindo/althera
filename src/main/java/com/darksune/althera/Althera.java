package com.darksune.althera;

import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.entity.SummonedEntity;
import com.darksune.althera.common.registry.AltheraRegistries;
import com.darksune.althera.config.AltheraConfig;
import com.darksune.althera.network.SummonPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import static com.darksune.althera.common.util.LightOrbUtil.desabilitarEspirito;
import static com.darksune.althera.common.util.LightOrbUtil.habilitarEspirito;
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
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {

        var registrar = event.registrar("1");

        registrar.playToServer(
                SummonPayload.TYPE,
                SummonPayload.STREAM_CODEC,
                (payload, context) -> {

                    final Player player = context.player();
                    final Level level = player.level();
                    final ManaData manaData = ManaData.get(player);
                    final HeroEntity oldSummon = manaData.hasSummon(player, level);
                    if (nonNull(oldSummon)) {
                        oldSummon.remove();
                        habilitarEspirito(player);
                        return;
                    }

                    if (!manaData.hasEnoughMana(20)) {
                        return;
                    }
                    desabilitarEspirito(player);
                    final HeroEntity hero = HeroEntity.create(level, player);

                    if (hero == null) {
                        return;
                    }

                    hero.moveTo(
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            player.getYRot(),
                            0
                    );
                    level.addFreshEntity(hero);
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

        // ⏱️ a cada 2 segundos
        if (player.tickCount % 40 == 0) {
            final ManaData manaData = ManaData.get(player);
            manaData.regenMana(player, level);
        }
    }
}
