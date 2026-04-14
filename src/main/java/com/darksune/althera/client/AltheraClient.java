package com.darksune.althera.client;

import com.darksune.althera.Althera;
import com.darksune.althera.client.renderer.entity.LightOrbRenderer;
import com.darksune.althera.client.renderer.entity.SummonedRenderer;
import com.darksune.althera.client.renderer.entity.SummonedZombieRenderer;
import com.darksune.althera.common.entity.AltheraEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Althera.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public class AltheraClient {
    public AltheraClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                AltheraEntities.LIGHT_ORB.get(),
                LightOrbRenderer::new
        );
        event.registerEntityRenderer(
                AltheraEntities.SUMMONED_ZOMBIE.get(),
                SummonedZombieRenderer::new
        );
        event.registerEntityRenderer(
                AltheraEntities.SUMMONED.get(),
                SummonedRenderer::new
        );
    }
}
