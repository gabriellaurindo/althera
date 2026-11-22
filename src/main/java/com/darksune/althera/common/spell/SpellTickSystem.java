package com.darksune.althera.common.spell;

import com.darksune.althera.Althera;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Althera.MOD_ID)
public class SpellTickSystem {

    @SubscribeEvent
    public static void tickSpellCooldowns(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        PlayerSpellData.get(player).tickCooldowns();
    }
}
