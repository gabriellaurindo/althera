package com.darksune.althera.common.quest;

import com.darksune.althera.common.academy.Academy;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class QuestSystem {

    private QuestSystem() {
    }

    public static boolean isAvailable(Player player, ResourceLocation id) {

        Quest quest = QuestRegistry.get(id);
        QuestData data = QuestData.get(player);

        return quest != null && !data.isClaimed(id) && quest.requires().stream().allMatch(data::isClaimed);
    }

    public static List<ResourceLocation> visible(Player player) {
        QuestData data = QuestData.get(player);
        return QuestRegistry.getOrderedIds().stream()
                .filter(id -> data.isClaimed(id) || isAvailable(player, id))
                .toList();
    }

    public static int progress(Player player, ResourceLocation id, int index) {

        Quest quest = QuestRegistry.get(id);

        if (quest == null || index >= quest.tasks().size()) {
            return 0;
        }

        return quest.tasks().get(index).progress(player, QuestData.get(player).getCounter(id, index));
    }

    public static boolean isComplete(Player player, ResourceLocation id) {

        Quest quest = QuestRegistry.get(id);

        if (quest == null) {
            return false;
        }

        for (int i = 0; i < quest.tasks().size(); i++) {
            if (progress(player, id, i) < quest.tasks().get(i).required()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isClaimable(Player player, ResourceLocation id) {
        return isAvailable(player, id) && isComplete(player, id);
    }

    public static boolean hasClaimable(Player player) {
        return QuestRegistry.getOrderedIds().stream().anyMatch(id -> isClaimable(player, id));
    }

    public static boolean claim(ServerPlayer player, ResourceLocation id) {

        Quest quest = QuestRegistry.get(id);

        if (quest == null || !isClaimable(player, id)) {
            return false;
        }

        for (QuestTask task : quest.tasks()) {
            if (task instanceof QuestTask.ItemTask item && item.consume()) {
                consume(player, item);
            }
        }

        QuestData.get(player).claim(id);
        quest.rewards().forEach(reward -> reward.give(player));

        player.displayClientMessage(Component.literal("§6Quest complete: §e" + quest.title()), true);
        player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.7F, 1.2F);

        return true;
    }

    private static void consume(ServerPlayer player, QuestTask.ItemTask task) {

        int left = task.count();

        for (int slot = 0; slot < player.getInventory().getContainerSize() && left > 0; slot++) {

            ItemStack stack = player.getInventory().getItem(slot);

            if (stack.is(task.item())) {
                int taken = Math.min(left, stack.getCount());
                stack.shrink(taken);
                left -= taken;
            }
        }
    }

    public static void onKill(ServerPlayer player, Entity victim) {

        QuestData data = QuestData.get(player);

        for (ResourceLocation id : QuestRegistry.getOrderedIds()) {

            if (!isAvailable(player, id)) {
                continue;
            }

            List<QuestTask> tasks = QuestRegistry.get(id).tasks();

            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i) instanceof QuestTask.Kill kill && kill.matches(victim)
                        && data.getCounter(id, i) < kill.count()) {
                    data.setCounter(id, i, data.getCounter(id, i) + 1);
                }
            }
        }
    }

    public static void onVisit(ServerPlayer player) {

        if (!Academy.isIn(player.level())) {
            return;
        }

        QuestData data = QuestData.get(player);

        for (ResourceLocation id : QuestRegistry.getOrderedIds()) {

            if (!isAvailable(player, id)) {
                continue;
            }

            List<QuestTask> tasks = QuestRegistry.get(id).tasks();

            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i) instanceof QuestTask.VisitAcademy && data.getCounter(id, i) < 1) {
                    data.setCounter(id, i, 1);
                }
            }
        }
    }
}
