package com.darksune.althera.client.input;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeyInputHandler {

    private static final int DISGUISE_HOLD_TICKS = 10;

    private static final int SEAL_WHEEL_HOLD_TICKS = 4;

    private static int summonHeldTicks;

    private static int sealHeldTicks;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        handleSummonKey();

        handleSealKey();

        if (AltheraKeybinds.HERO_SCREEN.consumeClick()) {
            KeyActions.handleHero();
        }

        if (AltheraKeybinds.ULTIMATE_SKILL.consumeClick()) {
            KeyActions.handleUltimateSkill();
        }

        if (AltheraKeybinds.OPEN_CONFIG.consumeClick()) {
            KeyActions.handleConfig();
        }
    }

    private static void handleSummonKey() {
        boolean clicked = false;
        while (AltheraKeybinds.SUMMON_KEY.consumeClick()) {
            clicked = true;
        }

        if (AltheraKeybinds.SUMMON_KEY.isDown()) {
            if (++summonHeldTicks == DISGUISE_HOLD_TICKS) {
                KeyActions.handleDisguise();
            }
            return;
        }

        if (summonHeldTicks < DISGUISE_HOLD_TICKS && (clicked || summonHeldTicks > 0)) {
            KeyActions.handleSummon();
        }
        summonHeldTicks = 0;
    }

    private static void handleSealKey() {
        boolean clicked = false;
        while (AltheraKeybinds.SEAL.consumeClick()) {
            clicked = true;
        }

        if (AltheraKeybinds.SEAL.isDown()) {
            if (++sealHeldTicks == SEAL_WHEEL_HOLD_TICKS) {
                KeyActions.handleSealWheel();
            }
            return;
        }

        if (sealHeldTicks < SEAL_WHEEL_HOLD_TICKS && (clicked || sealHeldTicks > 0)) {
            KeyActions.handleSealTap();
        }
        sealHeldTicks = 0;
    }
}
