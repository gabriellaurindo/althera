package com.darksune.althera;

import com.darksune.althera.common.command.HeroCommand;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.entity.SummonedEntity;
import com.darksune.althera.common.hero.HeroLoader;
import com.darksune.althera.common.registry.AltheraRegistries;
import com.darksune.althera.config.AltheraConfig;
import com.darksune.althera.network.AltheraNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber
@Mod(Althera.MOD_ID)
public final class Althera {

    public static final String MOD_ID = "althera";

    public Althera(final IEventBus modEventBus, final ModContainer modContainer) {
        AltheraRegistries.register(modEventBus);
        AltheraNetwork.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, AltheraConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(this::onReloadListeners);
    }

    private void onReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new HeroLoader());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        HeroCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(
                AltheraEntities.HERO.get(),
                HeroEntity.createAttributes().build()
        );
        event.put(
                AltheraEntities.SUMMONED.get(),
                SummonedEntity.createAttributes().build()
        );
    }

    // todo: Comentado por enquanto
//    @SubscribeEvent
//    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
//
//        var defaultSkin = event.getSkin(PlayerSkin.Model.WIDE);
//        var slimSkin = event.getSkin(PlayerSkin.Model.SLIM);
//
//        if (defaultSkin instanceof PlayerRenderer renderer) {
//            renderer.addLayer(new SealLayer(renderer));
//        }
//
//        if (slimSkin instanceof PlayerRenderer renderer) {
//            renderer.addLayer(new SealLayer(renderer));
//        }
//    }
}
