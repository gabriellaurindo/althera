package com.darksune.althera.common.event;

import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroProgressionSystem;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class CombatEvents {

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof HeroEntity hero)) return;

        final Player owner = hero.getOwner();

        if (owner == null) {
            return;
        }

        HeroProgressionSystem.addXp(owner, event);
    }
}