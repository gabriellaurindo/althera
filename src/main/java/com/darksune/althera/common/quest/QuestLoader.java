package com.darksune.althera.common.quest;

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

public class QuestLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new Gson();

    private final HolderLookup.Provider registries;

    public QuestLoader(HolderLookup.Provider registries) {
        super(GSON, "academy_quest");
        this.registries = registries;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {

        QuestRegistry.clear();

        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
        int failed = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            try {
                QuestRegistry.register(entry.getKey(), Quest.CODEC.parse(ops, entry.getValue()).getOrThrow());
            } catch (Exception e) {
                failed++;
                LOGGER.error("Failed to load academy quest {}: {}", entry.getKey(), e.getMessage());
            }
        }

        QuestRegistry.getAll().forEach((id, quest) -> quest.requires().stream()
                .filter(required -> QuestRegistry.get(required) == null)
                .forEach(required -> LOGGER.warn("Academy quest {} requires an unknown quest {}", id, required)));

        LOGGER.info("Loaded {} academy quests ({} failed)", map.size() - failed, failed);
    }
}
