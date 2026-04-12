package com.darksune.althera.common.util;

import com.darksune.althera.common.entity.SummonedZombieEntity;
import net.minecraft.network.chat.Component;
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

        int mana = ManaUtil.getMana(player);
        int max = ManaUtil.getMaxMana(player);
        int newMana = Math.min(mana + MANA_REGEN, max);
        ManaUtil.setMana(player, newMana);

        player.sendSystemMessage(Component.literal(
                "Mana: " + newMana + "/" + max
        ));

        if (newMana == max) {
            return;
        }

        incrementMaxMana(player, MANA_REGEN);
    }

    public static void incrementMaxMana(final Player player, final int amount) {
        int total = getManaRegenered(player) + amount;
        setManaRegenered(player, total);

        int levelsGained = total / 25 - (total - amount) / 25;

        if (levelsGained > 0) {
            setMaxMana(player, getMaxMana(player) + levelsGained);
        }
    }

    public static SummonedZombieEntity hasSummon(final Player player, final Level level) {
        return level.getEntitiesOfClass(SummonedZombieEntity.class, player.getBoundingBox().inflate(50))
                .stream()
                .filter(z -> z.getOwner() != null && player.getUUID().equals(z.getOwner().getUUID()))
                .findFirst()
                .orElse(null);
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
