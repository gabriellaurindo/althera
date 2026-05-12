package com.darksune.althera.common.event;

import com.darksune.althera.Althera;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import static com.darksune.althera.common.util.LightOrbUtil.habilitarEspirito;

@EventBusSubscriber(modid = Althera.MOD_ID)
public final class PlayerEvents {

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
                int maxHealth = (int) Math.round(
                        HeroStatsSystem.getMaxHealth(heroData)
                );

                int newHealth = Math.min(
                        (int) heroData.getHealth() + 2,
                        maxHealth
                );

                if (heroData.isDefeated() && newHealth >= maxHealth) {
                    heroData.setDefeated(false);
                    heroData.setCanResurrect(true);

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
}
