package com.darksune.althera.client;

import com.darksune.althera.Althera;
import com.darksune.althera.client.registry.AltheraClientRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Althera.MOD_ID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
public class AltheraClient {

    public AltheraClient(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, AltheraClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, AltheraClientConfig::createScreen);

        AltheraClientRegistries.register(modEventBus);
    }
}
