package com.darksune.althera.common.commandseal;

import com.darksune.althera.Althera;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Althera.MOD_ID)
public class CommandSealTickSystem {

    @SubscribeEvent
    public static void tickCommandSealSkills(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CommandSealData commandSealData = CommandSealData.get(player);

        HeroEntity heroEntity = HeroSummonSystem.getSummon(player);

        if (heroEntity != null) {
            CommandSealSystem.tickActiveSkills(player, heroEntity, commandSealData);
        }

        CommandSealSystem.tickCooldownSkills(player, commandSealData);

    }

    @SubscribeEvent
    public static void resetDailyCommandSealCharges(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Level level = player.level();

        if (level.isClientSide()) {
            return;
        }

        final CommandSealData data = CommandSealData.get(player);

        long currentDay = level.getDayTime() / 24000L;

        if (data.getLastChargeResetDay() == currentDay) {
            return;
        }

        data.setLastChargeResetDay(currentDay);

        data.resetCharges();
    }
}
