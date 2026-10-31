package com.darksune.althera.client;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Set;

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

        public String displayName() {
            return switch (this) {
                case TOP_LEFT -> "Top left";
                case TOP_RIGHT -> "Top right";
                case BOTTOM_LEFT -> "Bottom left";
                case BOTTOM_RIGHT -> "Bottom right";
            };
        }
    }

    public enum SkillTimersPosition {
        CROSSHAIR,
        HERO_HUD;

        public String displayName() {
            return this == CROSSHAIR ? "Crosshair" : "Hero HUD";
        }
    }

    public static final int MAX_HUD_OFFSET = 1000;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<HudCorner> HERO_HUD_CORNER = BUILDER
            .comment("Screen corner of the hero HUD (portrait, health, mana and interventions)")
            .defineEnum("heroHudCorner", HudCorner.TOP_LEFT);

    public static final ModConfigSpec.IntValue HERO_HUD_OFFSET_X = BUILDER
            .comment("Horizontal distance of the hero HUD from its corner, in GUI pixels")
            .defineInRange("heroHudOffsetX", 6, 0, MAX_HUD_OFFSET);

    public static final ModConfigSpec.IntValue HERO_HUD_OFFSET_Y = BUILDER
            .comment("Vertical distance of the hero HUD from its corner, in GUI pixels")
            .defineInRange("heroHudOffsetY", 6, 0, MAX_HUD_OFFSET);

    public static final ModConfigSpec.EnumValue<SkillTimersPosition> SKILL_TIMERS_POSITION = BUILDER
            .comment("Where active and cooldown timers of Command Seals and the Ultimate are shown")
            .defineEnum("skillTimersPosition", SkillTimersPosition.HERO_HUD);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static final Set<String> HERO_SCREEN_KEYS =
            Set.of("heroHudCorner", "heroHudOffsetX", "heroHudOffsetY", "skillTimersPosition");

    public static Screen createScreen(ModContainer container, Screen parent) {
        return new ConfigurationScreen(container, parent,
                (context, key, original) -> HERO_SCREEN_KEYS.contains(key) ? null : original);
    }

    private AltheraClientConfig() {
    }
}
