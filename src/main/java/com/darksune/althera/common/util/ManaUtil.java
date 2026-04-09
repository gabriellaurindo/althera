package com.darksune.althera.common.util;

import net.minecraft.world.entity.player.Player;

public class ManaUtil {

    public static int getMana(Player player) {
        return player.getPersistentData().getInt("mana");
    }

    public static void setMana(Player player, int value) {
        player.getPersistentData().putInt("mana", value);
    }

    public static void addMana(Player player, int amount) {
        setMana(player, getMana(player) + amount);
    }

    public static boolean consumeMana(Player player, int amount) {
        int mana = getMana(player);
        if (mana < amount) return false;

        setMana(player, mana - amount);
        return true;
    }
}
