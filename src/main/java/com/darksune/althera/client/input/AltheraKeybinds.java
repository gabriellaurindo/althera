package com.darksune.althera.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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

    public static final KeyMapping SEAL = new KeyMapping(
            "key.althera.seal",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.althera"
    );

    public static final KeyMapping ULTIMATE_SKILL = new KeyMapping(
            "key.althera.ultimate.skill",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            "key.categories.althera"
    );

    public static final KeyMapping SPELL_TREE = new KeyMapping(
            "key.althera.spell.tree",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.althera"
    );

    public static final KeyMapping JOURNAL = new KeyMapping(
            "key.althera.journal",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "key.categories.althera"
    );

    public static final KeyMapping SPELL_WHEEL = new KeyMapping(
            "key.althera.spell.wheel",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.althera"
    );

    public static final KeyMapping SPELL_PREVIOUS = new KeyMapping(
            "key.althera.spell.previous",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "key.categories.althera"
    );

    public static final KeyMapping SPELL_NEXT = new KeyMapping(
            "key.althera.spell.next",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.althera"
    );

    public static final KeyMapping SPELL_SCROLL = new KeyMapping(
            "key.althera.spell.scroll",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "key.categories.althera"
    );

    public static boolean isHeld(KeyMapping mapping) {

        if (mapping.isUnbound()) {
            return false;
        }

        InputConstants.Key key = mapping.getKey();
        long window = Minecraft.getInstance().getWindow().getWindow();

        return switch (key.getType()) {
            case KEYSYM -> InputConstants.isKeyDown(window, key.getValue());
            case MOUSE -> GLFW.glfwGetMouseButton(window, key.getValue()) == GLFW.GLFW_PRESS;
            case SCANCODE -> false;
        };
    }

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
                throw new RuntimeException("Failed to register keybind: " + field.getName(), e);
            }
        }
    }
}