package com.darksune.althera.common.spell;

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

public class SpellNodeLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new Gson();

    public SpellNodeLoader() {
        super(GSON, "spell_node");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> map,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {

        SpellNodeRegistry.clear();

        int failed = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {

            try {

                SpellNodeRegistry.register(entry.getKey(), SpellNode.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());

            } catch (Exception e) {

                failed++;

                LOGGER.error("Failed to load spell node {}: {}", entry.getKey(), e.getMessage());
            }
        }

        SpellNodeRegistry.getAll().forEach((id, node) -> node.parent()
                .filter(parent -> SpellNodeRegistry.get(parent) == null)
                .ifPresent(parent -> LOGGER.warn("Spell node {} has an unknown parent {}", id, parent)));

        LOGGER.info("Loaded {} spell nodes ({} failed)", map.size() - failed, failed);
    }
}
