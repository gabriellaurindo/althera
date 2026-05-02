package com.darksune.althera.common.item;

import net.minecraft.resources.ResourceLocation;

import static com.darksune.althera.Althera.MOD_ID;

public class AltheraItemId {

    public static final ResourceLocation SUMMON_SEAL = id("summon_seal");

    //Block items
    public static final ResourceLocation RITUAL_CORE = id("ritual_core");

    public static ResourceLocation id(final String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
