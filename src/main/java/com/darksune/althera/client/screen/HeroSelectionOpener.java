package com.darksune.althera.client.screen;

import com.darksune.althera.Althera;
import com.darksune.althera.common.hero.HeroRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class HeroSelectionOpener {

    private static boolean pending;

    private HeroSelectionOpener() {}

    public static void request() {
        pending = true;
        tryOpen();
    }

    public static boolean open() {
        final Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || HeroRegistry.getSelectable().isEmpty()) {
            return false;
        }

        mc.setScreen(new HeroSelectionScreen());
        return true;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (pending) {
            tryOpen();
        }
    }

    private static void tryOpen() {
        final Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null || mc.screen != null) {
            return;
        }

        if (open()) {
            pending = false;
        }
    }
}
