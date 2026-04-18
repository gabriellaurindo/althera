package com.darksune.althera.common.event;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
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

        if (owner != null) {
            final HeroData data = HeroData.get(owner);
            data.incrementMaxHealth(0.5, owner);
        }
    }
}