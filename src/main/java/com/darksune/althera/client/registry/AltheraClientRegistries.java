package com.darksune.althera.client.registry;

import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.client.renderer.entity.HeroRenderer;
import com.darksune.althera.client.renderer.entity.LightOrbRenderer;
import com.darksune.althera.client.renderer.entity.SummonedRenderer;
import com.darksune.althera.common.entity.AltheraEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class AltheraClientRegistries {

    private AltheraClientRegistries() {}

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(AltheraClientRegistries::registerKeybinds);
        modEventBus.addListener(AltheraClientRegistries::registerRenderers);
    }

    private static void registerKeybinds(RegisterKeyMappingsEvent event) {
        AltheraKeybinds.register(event);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(
                AltheraEntities.LIGHT_ORB.get(),
                LightOrbRenderer::new
        );

        event.registerEntityRenderer(
                AltheraEntities.HERO.get(),
                HeroRenderer::new
        );

        event.registerEntityRenderer(
                AltheraEntities.SUMMONED.get(),
                SummonedRenderer::new
        );
    }
}