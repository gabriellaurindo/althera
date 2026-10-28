package com.darksune.althera.common.item;

import net.minecraft.resources.ResourceLocation;

import static com.darksune.althera.Althera.MOD_ID;

public class AltheraItemId {

    public static final ResourceLocation SUMMON_SEAL = id("summon_seal");

    public static final ResourceLocation ENEMY_MAGE_SPAWN_EGG = id("enemy_mage_spawn_egg");

    public static final ResourceLocation EMBLEM = id("emblem");

    //Block items
    public static final ResourceLocation RITUAL_CORE = id("ritual_core");
    public static final ResourceLocation NOBLE_SIGIL = id("noble_sigil");

    public static ResourceLocation id(final String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
