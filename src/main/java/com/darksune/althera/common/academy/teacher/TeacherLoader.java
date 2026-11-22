package com.darksune.althera.common.academy.teacher;

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

public class TeacherLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new Gson();

    public TeacherLoader() {
        super(GSON, "academy_teacher");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {

        TeacherRegistry.clear();

        int failed = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            try {
                TeacherRegistry.register(entry.getKey(), TeacherDefinition.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());
            } catch (Exception e) {
                failed++;
                LOGGER.error("Failed to load academy teacher {}: {}", entry.getKey(), e.getMessage());
            }
        }

        LOGGER.info("Loaded {} academy teachers ({} failed)", map.size() - failed, failed);
    }
}
