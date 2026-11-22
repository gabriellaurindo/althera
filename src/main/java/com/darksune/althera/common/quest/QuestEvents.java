package com.darksune.althera.common.quest;

import com.darksune.althera.Althera;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Althera.MOD_ID)
public final class QuestEvents {

    private static final int VISIT_CHECK_TICKS = 20;

    private QuestEvents() {
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {

        Entity killer = event.getSource().getEntity();

        if (killer instanceof HeroEntity hero) {
            killer = hero.getOwner();
        }

        if (killer instanceof ServerPlayer player) {
            QuestSystem.onKill(player, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && player.tickCount % VISIT_CHECK_TICKS == 0) {
            QuestSystem.onVisit(player);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && QuestSystem.hasClaimable(player)) {
            player.sendSystemMessage(Component.literal("§dYou have news in your Journal. §7Press ")
                    .append(Component.keybind("key.althera.journal").withStyle(style -> style.withColor(0xFFD47FFF)))
                    .append(Component.literal("§7 to read it.")));
        }
    }
}
