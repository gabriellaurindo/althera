package com.darksune.althera.client.input;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import static com.darksune.althera.common.commandseal.skill.CommandSealSkillType.*;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeyInputHandler {

    private static final int DISGUISE_HOLD_TICKS = 10;

    private static int summonHeldTicks;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        handleSummonKey();

        if (AltheraKeybinds.HERO_SCREEN.consumeClick()) {
            KeyActions.handleHero();
        }

        if (AltheraKeybinds.COMMAND_SEAL_SKILL_ONE.consumeClick()) {
            KeyActions.handleCommandSealSkill(OVERDRIVE);
        }

        if (AltheraKeybinds.COMMAND_SEAL_SKILL_TWO.consumeClick()) {
            KeyActions.handleCommandSealSkill(REVIVE);
        }

        if (AltheraKeybinds.COMMAND_SEAL_SKILL_THREE.consumeClick()) {
            KeyActions.handleCommandSealSkill(HEAL);
        }

        if (AltheraKeybinds.ULTIMATE_SKILL.consumeClick()) {
            KeyActions.handleUltimateSkill();
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
}
