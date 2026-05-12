package com.darksune.althera.common.ultimate;

import com.darksune.althera.Althera;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Althera.MOD_ID)
public class UltimateTickSystem {

    @SubscribeEvent
    public static void tickUltimateSkills(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        UltimateData ultimateData = UltimateData.get(player);

        HeroEntity heroEntity = HeroSummonSystem.getSummon(player);

        if (heroEntity != null) {
            UltimateSystem.tickActiveSkills(player, heroEntity, ultimateData);
        }

        UltimateSystem.tickCooldownSkills(player, ultimateData);
    }

    @SubscribeEvent
    public static void resetDailyUltimateCooldowns(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Level level = player.level();

        if (level.isClientSide()) {
            return;
        }

        UltimateData data = UltimateData.get(player);

        long currentDay = level.getDayTime() / 24000L;

        if (data.getLastUltimateResetDay() == currentDay) {
            return;
        }

        data.setLastUltimateResetDay(currentDay);

        data.resetCooldowns();
    }
}
