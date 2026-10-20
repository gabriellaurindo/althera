package com.darksune.althera.common.ultimate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;

public class UltimateLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new Gson();

    private final HolderLookup.Provider registries;

    public UltimateLoader(HolderLookup.Provider registries) {
        super(GSON, "ultimate");
        this.registries = registries;
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> map,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {

        UltimateRegistry.clear();

        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);

        int failed = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {

            try {

                UltimateRegistry.register(entry.getKey(), UltimateDefinition.CODEC.parse(ops, entry.getValue()).getOrThrow());

            } catch (Exception e) {

                failed++;

                LOGGER.error("Failed to load ultimate {}: {}", entry.getKey(), e.getMessage());
            }
        }

        LOGGER.info("Loaded {} ultimates ({} failed)", map.size() - failed, failed);
    }
}
