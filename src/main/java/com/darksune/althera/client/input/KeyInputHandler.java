package com.darksune.althera.client.input;

import com.darksune.althera.client.action.ClientActions;
import com.darksune.althera.common.AltheraKeybinds;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event) {
        event.register(AltheraKeybinds.SUMMON_KEY);
        event.register(AltheraKeybinds.HERO_SCREEN);
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
        final Minecraft mc = Minecraft.getInstance();


        // todo
//        if (mc.screen != null) return;
        if (AltheraKeybinds.SUMMON_KEY.consumeClick()) {
            ClientActions.handleSummon();
        }
        if (AltheraKeybinds.HERO_SCREEN.consumeClick()) {
            ClientActions.handleHero();
        }
    }
}