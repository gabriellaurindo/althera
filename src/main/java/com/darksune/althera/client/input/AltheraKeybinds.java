package com.darksune.althera.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class AltheraKeybinds {

    public static final KeyMapping SUMMON_KEY = new KeyMapping(
            "key.althera.summon",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.althera"
    );

    public static final KeyMapping HERO_SCREEN = new KeyMapping(
            "key.althera.hero.screen",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.althera"
    );

    public static final KeyMapping COMMAND_SEAL_SKILL = new KeyMapping(
            "key.althera.commandseal",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "key.categories.althera"
    );

    public static void register(RegisterKeyMappingsEvent event) {

        for (Field field : AltheraKeybinds.class.getDeclaredFields()) {

            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (!KeyMapping.class.isAssignableFrom(field.getType())) {
                continue;
            }

            try {

                KeyMapping key = (KeyMapping) field.get(null);

                event.register(key);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "Failed to register keybind: " + field.getName(),
                        e
                );
            }
        }
    }
}