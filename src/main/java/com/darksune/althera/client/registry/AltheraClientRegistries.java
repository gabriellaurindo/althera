package com.darksune.althera.client.registry;

import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.client.item.CatalystClassProperty;
import com.darksune.althera.client.layer.SealLayer;
import com.darksune.althera.client.renderer.block.RitualCircleRenderer;
import com.darksune.althera.client.renderer.block.RitualPedestalRenderer;
import com.darksune.althera.client.renderer.entity.HeroRenderer;
import com.darksune.althera.client.renderer.entity.LightOrbRenderer;
import com.darksune.althera.client.renderer.entity.EnemyMageRenderer;
import com.darksune.althera.client.renderer.entity.SummonedRenderer;
import com.darksune.althera.common.block.entity.AltheraBlockEntities;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.item.AltheraItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class AltheraClientRegistries {

    private AltheraClientRegistries() {}

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(AltheraClientRegistries::registerKeybinds);
        modEventBus.addListener(AltheraClientRegistries::registerRenderers);
        modEventBus.addListener(AltheraClientRegistries::registerLayers);
        modEventBus.addListener(AltheraClientRegistries::registerItemProperties);
        modEventBus.addListener(AltheraClientRegistries::registerAdditionalModels);
    }

    private static void registerItemProperties(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                AltheraItems.HERO_CATALYST.get(),
                CatalystClassProperty.ID,
                CatalystClassProperty::get
        ));
    }

    private static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(RitualCircleRenderer.CRYSTAL_MODEL);
    }

    private static void registerKeybinds(RegisterKeyMappingsEvent event) {
        AltheraKeybinds.register(event);
    }

    private static void registerLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model model : event.getSkins()) {
            if (event.getSkin(model) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new SealLayer(renderer));
            }
        }
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

        event.registerEntityRenderer(
                AltheraEntities.ENEMY_MAGE.get(),
                EnemyMageRenderer::new
        );

        event.registerEntityRenderer(
                AltheraEntities.HERO_PROJECTILE.get(),
                NoopRenderer::new
        );

        event.registerBlockEntityRenderer(
                AltheraBlockEntities.RITUAL_PEDESTAL.get(),
                RitualPedestalRenderer::new
        );

        event.registerBlockEntityRenderer(
                AltheraBlockEntities.RITUAL_CORE.get(),
                RitualCircleRenderer::new
        );
    }
}