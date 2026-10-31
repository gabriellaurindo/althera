package com.darksune.althera.common.hero;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;

public class HeroLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new Gson();

    private static final String DEFAULT_ULTIMATE = "althera:explosion";

    private final HolderLookup.Provider registries;

    public HeroLoader(HolderLookup.Provider registries) {
        super(GSON, "hero");
        this.registries = registries;
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

        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);

        int failed = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {

            try {

                ResourceLocation id = entry.getKey();
                JsonObject json = entry.getValue().getAsJsonObject();

                HeroDefinition hero = parseHero(id, json, ops);

                HeroRegistry.register(hero);

                LOGGER.debug("Loaded hero {}", id);

            } catch (Exception e) {

                failed++;

                LOGGER.error("Failed to load hero {}: {}", entry.getKey(), e.toString());
            }
        }

        LOGGER.info(
                "Loaded {} heroes ({} failed, {} rollable)",
                map.size() - failed,
                failed,
                HeroRegistry.getRollable().size()
        );
    }

    private HeroDefinition parseHero(
            ResourceLocation id,
            JsonObject json,
            RegistryOps<JsonElement> ops
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

        ResourceLocation heroClass =
                HeroClass.parseId(
                        GsonHelper.getAsString(
                                json,
                                "class",
                                "saber"
                        )
                );

        HeroRank rank =
                HeroRank.valueOf(
                        GsonHelper.getAsString(
                                json,
                                "rank"
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
                personality,
                GsonHelper.getAsFloat(json, "scale", 1.0f),
                ResourceLocation.parse(GsonHelper.getAsString(json, "ultimate", DEFAULT_ULTIMATE)),
                parseAttack(json, ops),
                parseAnimation(json, ops),
                parseStats(json, ops),
                parseSounds(json, ops),
                parseHitbox(json, ops)
        );
    }

    private HeroSounds parseSounds(JsonObject json, RegistryOps<JsonElement> ops) {

        if (!json.has("sounds")) {
            return HeroSounds.DEFAULT;
        }

        return HeroSounds.CODEC.parse(ops, json.get("sounds")).getOrThrow();
    }

    private HeroHitbox parseHitbox(JsonObject json, RegistryOps<JsonElement> ops) {

        if (!json.has("hitbox")) {
            return HeroHitbox.DEFAULT;
        }

        return HeroHitbox.CODEC.parse(ops, json.get("hitbox")).getOrThrow();
    }

    private HeroStats parseStats(JsonObject json, RegistryOps<JsonElement> ops) {

        if (!json.has("stats")) {
            return HeroStats.DEFAULT;
        }

        return HeroStats.CODEC.parse(ops, json.get("stats")).getOrThrow();
    }

    private HeroAnimation parseAnimation(JsonObject json, RegistryOps<JsonElement> ops) {

        if (!json.has("animation")) {
            return HeroAnimation.DEFAULT;
        }

        return HeroAnimation.CODEC.parse(ops, json.get("animation")).getOrThrow();
    }

    private HeroAttack parseAttack(JsonObject json, RegistryOps<JsonElement> ops) {

        if (!json.has("attack")) {
            return HeroAttack.MELEE;
        }

        return HeroAttack.CODEC.parse(ops, json.get("attack")).getOrThrow();
    }
}