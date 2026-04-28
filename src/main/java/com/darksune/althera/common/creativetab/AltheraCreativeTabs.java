package com.darksune.althera.common.creativetab;

import com.darksune.althera.Althera;
import com.darksune.althera.common.item.AltheraItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AltheraCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Althera.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TABS.register("main",
                    () -> CreativeModeTab.builder()
                            .title(Component.literal("Althera"))
                            .icon(() -> new ItemStack(AltheraItems.SUMMON_SEAL.get()))
                            .displayItems((parameters, output) -> {

                                // seus itens aqui
                                output.accept(AltheraItems.SUMMON_SEAL.get());
                                output.accept(AltheraItems.RITUAL_CORE.get());

                            })
                            .build()
            );

    public static void register(net.neoforged.bus.api.IEventBus bus) {
        TABS.register(bus);
    }
}