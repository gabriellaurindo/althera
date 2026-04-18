package com.darksune.althera.common;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class AltheraKeybinds {

    public static final KeyMapping SUMMON_KEY = new KeyMapping(
            "key.althera.summon",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.althera"
    );

    public static final KeyMapping HERO_SCREEN = new KeyMapping(
            "key.althera.hero.screen",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.althera"
    );
}