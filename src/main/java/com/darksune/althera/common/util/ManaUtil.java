package com.darksune.althera.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static java.util.Objects.nonNull;

public class ManaUtil {

    public static final int MANA_REGEN = 10;

    public static final String MANA = "mana";
    public static final String MANA_MAX = "mana_max";
    public static final String MANA_REGENERED = "mana_regenered";

    public static int getMana(Player player) {
        return player.getPersistentData().getInt(MANA);
    }

    public static void setMana(Player player, int value) {
        player.getPersistentData().putInt(MANA, value);
    }

    public static int getManaRegenered(final Player player) {
        return player.getPersistentData().getInt(MANA_REGENERED);
    }

    public static void setManaRegenered(Player player, int value) {
        player.getPersistentData().putInt(MANA_REGENERED, value);
    }

    public static int getMaxMana(Player player) {
        return player.getPersistentData().getInt(MANA_MAX);
    }

    public static void setMaxMana(Player player, int value) {
        player.getPersistentData().putInt(MANA_MAX, value);
    }

    public static void setDefaultMana(Player player) {
        var data = player.getPersistentData();
        if (!data.contains(MANA)) {
            data.putInt(MANA, 200);
            data.putInt(MANA_MAX, 200);
            data.putInt(MANA_REGENERED, 0);
        }
    }

    public static boolean consumeMana(final Player player, int amount) {
        int mana = getMana(player);
        if (isPlayerSemMana(player, amount)) {
            return false;
        }
        setMana(player, mana - amount);
        return true;
    }

    public static void regenMana(final Player player, final Level level) {
        if (nonNull(hasSummon(player, level))) {
            return;
        }
        incrementMaxMana(player, MANA_REGEN);

        int mana = ManaUtil.getMana(player);
        int max = ManaUtil.getMaxMana(player);
        int newMana = Math.min(mana + MANA_REGEN, max);
        if (newMana == max) {
            return;
        }
        ManaUtil.setMana(player, newMana);

        player.sendSystemMessage(Component.literal(
                "Mana regenerada: " + newMana + "/" + max
        ));
    }

    public static void incrementMaxMana(final Player player, final int amount) {
        int manaRegenered = getManaRegenered(player) + amount;
        setManaRegenered(player, manaRegenered);
        int oldRegenered = getManaRegenered(player);
        int newRegenered = oldRegenered + amount;

        int oldLevel = oldRegenered / 25;
        int newLevel = newRegenered / 25;

        if (newLevel > oldLevel) {
            int gained = newLevel - oldLevel;

            int maxMana = getMaxMana(player);
            setMaxMana(player, maxMana + gained);
        }

        setManaRegenered(player, newRegenered);
    }

    public static Zombie hasSummon(final Player player, final Level level) {
        return level.getEntitiesOfClass(Zombie.class, player.getBoundingBox().inflate(50))
                .stream()
                .filter(z -> z.getTags().contains("friendly_summon")
                        && player.getUUID().equals(z.getPersistentData().getUUID("owner")))
                .findFirst().orElse(null);
    }

    public static boolean isPlayerSemMana(final Player player, final int amount) {
        int mana = getMana(player);
        if (mana < amount) {
            player.sendSystemMessage(Component.literal("Sem mana!"));
            return true;
        }
        return false;
    }
}
