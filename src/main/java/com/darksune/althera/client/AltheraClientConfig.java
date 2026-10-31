package com.darksune.althera.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class AltheraClientConfig {

    public enum HudCorner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;

        public boolean isRight() {
            return this == TOP_RIGHT || this == BOTTOM_RIGHT;
        }

        public boolean isBottom() {
            return this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
        }
    }

    public enum SkillTimersPosition {
        CROSSHAIR,
        HERO_HUD
    }

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<HudCorner> HERO_HUD_CORNER = BUILDER
            .comment("Screen corner of the hero HUD (portrait, health, mana and interventions)")
            .defineEnum("heroHudCorner", HudCorner.TOP_LEFT);

    public static final ModConfigSpec.IntValue HERO_HUD_OFFSET_X = BUILDER
            .comment("Horizontal distance of the hero HUD from its corner, in GUI pixels")
            .defineInRange("heroHudOffsetX", 6, 0, 1000);

    public static final ModConfigSpec.IntValue HERO_HUD_OFFSET_Y = BUILDER
            .comment("Vertical distance of the hero HUD from its corner, in GUI pixels")
            .defineInRange("heroHudOffsetY", 6, 0, 1000);

    public static final ModConfigSpec.EnumValue<SkillTimersPosition> SKILL_TIMERS_POSITION = BUILDER
            .comment("Where active and cooldown timers of Command Seals and the Ultimate are shown")
            .defineEnum("skillTimersPosition", SkillTimersPosition.CROSSHAIR);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private AltheraClientConfig() {
    }
}
