package com.darksune.althera.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ManaUtil {

    public static int getMana(Player player) {
        return player.getPersistentData().getInt("mana");
    }

    public static void setMana(Player player, int value) {
        player.getPersistentData().putInt("mana", value);
    }

    public static int getManaSpent(Player player) {
        return player.getPersistentData().getInt("mana_spent");
    }

    public static void setManaSpent(Player player, int value) {
        player.getPersistentData().putInt("mana_spent", value);
    }

    public static int getMaxMana(Player player) {
        return player.getPersistentData().getInt("max_mana");
    }

    public static void setMaxMana(Player player, int value) {
        player.getPersistentData().putInt("max_mana", value);
    }

    public static void setDefaultMana(Player player) {
        var data = player.getPersistentData();
        if (!data.contains("mana")) {
            data.putInt("mana", 200);
            data.putInt("max_mana", 200);
            data.putInt("mana_spent", 0);
        }
    }

    public static void addMana(Player player, int amount) {
        setMana(player, getMana(player) + amount);
    }

    public static boolean consumeMana(Player player, int amount) {
        int mana = getMana(player);
        if (mana < amount) {
            player.sendSystemMessage(Component.literal("Sem mana!"));
            return false;
        }
        int manaSpent = getManaSpent(player) + amount;
        setManaSpent(player, manaSpent);
        setMana(player, mana - amount);

        int oldSpent = getManaSpent(player);
        int newSpent = oldSpent + amount;

        int oldLevel = oldSpent / 25;
        int newLevel = newSpent / 25;

        if (newLevel > oldLevel) {
            int gained = newLevel - oldLevel;

            int maxMana = getMaxMana(player);
            setMaxMana(player, maxMana + gained);
        }

        setManaSpent(player, newSpent);

        return true;
    }
}
