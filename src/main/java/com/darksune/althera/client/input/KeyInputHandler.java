package com.darksune.althera.client.input;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        if (AltheraKeybinds.SUMMON_KEY.consumeClick()) {
            KeyActions.handleSummon();
        }

        if (AltheraKeybinds.HERO_SCREEN.consumeClick()) {
            KeyActions.handleHero();
        }

        if (AltheraKeybinds.COMMAND_SEAL_SKILL.consumeClick()) {
            KeyActions.handleCommandSealSkill();
        }
    }
}
