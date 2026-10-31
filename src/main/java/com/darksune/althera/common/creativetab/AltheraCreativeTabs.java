package com.darksune.althera.common.creativetab;

import com.darksune.althera.Althera;
import com.darksune.althera.common.hero.HeroRank;
import com.darksune.althera.common.item.AltheraItems;
import com.darksune.althera.common.item.HeroCatalystItem;
import com.darksune.althera.common.item.component.HeroCatalyst;
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
                            .icon(() -> new ItemStack(AltheraItems.EMBLEM.get()))
                            .displayItems((parameters, output) -> {

                                // your items here
                                output.accept(AltheraItems.RITUAL_CORE.get());
                                output.accept(AltheraItems.RITUAL_PEDESTAL.get());
                                output.accept(AltheraItems.ENEMY_MAGE_SPAWN_EGG.get());
                                output.accept(AltheraItems.NOBLE_SIGIL.get());
                                HeroCatalyst.BUILT_IN_CLASSES.forEach(classId ->
                                        output.accept(HeroCatalystItem.of(classId, HeroRank.EX)));

                            })
                            .build()
            );

    public static void register(net.neoforged.bus.api.IEventBus bus) {
        TABS.register(bus);
    }
}