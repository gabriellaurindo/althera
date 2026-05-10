package com.darksune.althera.common.commandseal;

import com.darksune.althera.Althera;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Althera.MOD_ID)
public class CommandSealTickSystem {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

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
}
