package com.darksune.althera;

import com.darksune.althera.common.registry.AltheraRegistries;
import com.darksune.althera.data.config.AltheraConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Althera.MOD_ID)
public final class Althera {

    public static final String MOD_ID = "althera";

    public Althera(IEventBus modEventBus, ModContainer modContainer) {
        AltheraRegistries.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, AltheraConfig.SPEC);
    }
}
