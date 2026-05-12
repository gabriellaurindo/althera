package com.darksune.althera.client;

import com.darksune.althera.Althera;
import com.darksune.althera.client.registry.AltheraClientRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Althera.MOD_ID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
public class AltheraClient {

    public AltheraClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        AltheraClientRegistries.register(modEventBus);
    }
}
