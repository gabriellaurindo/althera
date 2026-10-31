package com.darksune.althera.client.screen;

import com.darksune.althera.Althera;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class SealWheelHudEvents {

    private SealWheelHudEvents() {
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR)
                && Minecraft.getInstance().screen instanceof SealWheelScreen) {
            event.setCanceled(true);
        }
    }
}
