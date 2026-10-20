package com.darksune.althera.common.hero;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;

public class HeroClassLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new Gson();

    public HeroClassLoader() {
        super(GSON, "hero_class");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> map,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {

        HeroClassRegistry.clear();

        int failed = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {

            try {

                HeroClassRegistry.register(entry.getKey(), HeroClass.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());

            } catch (Exception e) {

                failed++;

                LOGGER.error("Failed to load hero class {}: {}", entry.getKey(), e.getMessage());
            }
        }

        LOGGER.info("Loaded {} hero classes ({} failed)", map.size() - failed, failed);
    }
}
