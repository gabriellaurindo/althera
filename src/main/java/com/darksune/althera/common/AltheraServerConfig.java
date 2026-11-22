package com.darksune.althera.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class AltheraServerConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SPAWN_IN_ACADEMY = BUILDER
            .comment("Send new players to the Noble Magic Academy the first time they join.",
                    "Turn it off for modpacks with their own starting map.")
            .define("academy.spawnInAcademy", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private AltheraServerConfig() {
    }
}
