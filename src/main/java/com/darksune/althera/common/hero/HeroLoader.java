package com.darksune.althera.common.hero;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class HeroLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();

    public HeroLoader() {
        super(GSON, "hero");
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return super.prepare(resourceManager, profiler);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {

        HeroRegistry.clear();

        System.out.println("=== HERO LOADER ===");
        System.out.println("Entries: " + map.size());

        for (var entry : map.entrySet()) {
            System.out.println("Found: " + entry.getKey());
        }

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {

            ResourceLocation id = entry.getKey();
            JsonObject json = entry.getValue().getAsJsonObject();

            HeroDefinition hero = parseHero(id, json);
            HeroRegistry.register(hero);
        }
    }

    private HeroDefinition parseHero(ResourceLocation id, JsonObject json) {

        String name = json.get("name").getAsString();
        String description = json.get("description").getAsString();

        HeroClass heroClass = HeroClass.valueOf(json.get("class").getAsString().toUpperCase());

        HeroRarity rarity = HeroRarity.valueOf(
                json.get("rarity").getAsString().toUpperCase()
        );

        HeroNature nature = HeroNature.valueOf(
                json.get("nature").getAsString().toUpperCase()
        );

        ResourceLocation model = ResourceLocation.parse(json.get("model").getAsString());
        ResourceLocation texture = ResourceLocation.parse(json.get("texture").getAsString());

        String personality = json.get("personality").getAsString();

        return new HeroDefinition(
                id,
                name,
                description,
                heroClass,
                rarity,
                nature,
                model,
                texture,
                personality
        );
    }
}