package com.darksune.althera.client;

import com.darksune.althera.Althera;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Althera.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public class AltheraClient {
    public AltheraClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        // Althera.LOGGER.info("HELLO FROM CLIENT SETUP");
        // Althera.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

//    @Override
//    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
//        itemModels.itemModelOutput.register(
//                EXAMPLE_ITEM.get(),
//                new ClientItem(
//                        // Defines the model to submit for rendering
//                        new BlockModelWrapper.Unbaked(
//                                // Points to a model JSON relative to the 'models' directory
//                                // Located at 'assets/examplemod/models/item/example_item.json'
//                                ModelLocationUtils.getModelLocation(EXAMPLE_ITEM.get()),
//                                Collections.emptyList()
//                        ),
//                        // Defines some settings to use during the rendering process
//                        new ClientItem.Properties(
//                                // When false, disables the animation where the item is raised
//                                // up towards its normal position on item swap
//                                false
//                        )
//                )
//        );
//    }
}
