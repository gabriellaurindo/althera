package com.darksune.althera.common.hero;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class HeroLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();

    public HeroLoader() {
        super(GSON, "hero");
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        return super.prepare(resourceManager, profiler);
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> map,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {

        HeroRegistry.clear();

        System.out.println("=== HERO LOADER ===");
        System.out.println("Entries: " + map.size());

        for (var entry : map.entrySet()) {
            System.out.println("Found: " + entry.getKey());
        }

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {

            try {

                ResourceLocation id = entry.getKey();
                JsonObject json = entry.getValue().getAsJsonObject();

                HeroDefinition hero = parseHero(id, json);

                HeroRegistry.register(hero);

                System.out.println("Loaded hero: " + id);

            } catch (Exception e) {

                System.err.println(
                        "Failed to load hero: " + entry.getKey()
                );

                e.printStackTrace();
            }
        }
    }

    private HeroDefinition parseHero(
            ResourceLocation id,
            JsonObject json
    ) {

        String name =
                GsonHelper.getAsString(
                        json,
                        "name",
                        "Unknown Hero"
                );

        String description =
                GsonHelper.getAsString(
                        json,
                        "description",
                        ""
                );

        HeroClass heroClass =
                HeroClass.valueOf(
                        GsonHelper.getAsString(
                                json,
                                "class",
                                "SABER"
                        ).toUpperCase()
                );

        HeroRank rank =
                HeroRank.valueOf(
                        GsonHelper.getAsString(
                                json,
                                "rank",
                                "COMMON"
                        ).toUpperCase()
                );

        HeroNature nature =
                HeroNature.valueOf(
                        GsonHelper.getAsString(
                                json,
                                "nature",
                                "COMMON"
                        ).toUpperCase()
                );

        // =========================
        // OPTIONAL
        // =========================

        ResourceLocation model = null;

        if (json.has("model")
                && !json.get("model").isJsonNull()) {

            model = ResourceLocation.parse(
                    json.get("model").getAsString()
            );
        }

        ResourceLocation texture = null;

        if (json.has("texture")
                && !json.get("texture").isJsonNull()) {

            texture = ResourceLocation.parse(
                    json.get("texture").getAsString()
            );
        }

        ResourceLocation animations = null;

        if (json.has("animations")
                && !json.get("animations").isJsonNull()) {

            animations = ResourceLocation.parse(
                    json.get("animations").getAsString()
            );
        }

        String personality =
                GsonHelper.getAsString(
                        json,
                        "personality",
                        ""
                );

        return new HeroDefinition(
                id,
                name,
                description,
                heroClass,
                rank,
                nature,
                model,
                texture,
                animations,
                personality
        );
    }
}