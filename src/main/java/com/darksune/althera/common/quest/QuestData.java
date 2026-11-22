package com.darksune.althera.common.quest;

import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.SyncableAttachment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuestData extends SyncableAttachment {

    private final Set<ResourceLocation> claimed = new HashSet<>();

    private final Map<String, Integer> counters = new HashMap<>();

    public static final Codec<QuestData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("claimed", List.of()).forGetter(data -> List.copyOf(data.claimed)),
            Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("counters", Map.of()).forGetter(data -> data.counters)
    ).apply(instance, (claimed, counters) -> {
        QuestData data = new QuestData();
        data.claimed.addAll(claimed);
        data.counters.putAll(counters);
        return data;
    }));

    public static final StreamCodec<FriendlyByteBuf, QuestData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                buf.writeCollection(data.claimed, FriendlyByteBuf::writeResourceLocation);
                buf.writeMap(data.counters, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeVarInt);
            },
            buf -> {
                QuestData data = new QuestData();
                data.claimed.addAll(buf.readList(FriendlyByteBuf::readResourceLocation));
                data.counters.putAll(buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readVarInt));
                return data;
            });

    public static QuestData get(Player player) {
        return player.getData(AltheraAttachments.QUESTS.get());
    }

    private static String key(ResourceLocation quest, int task) {
        return quest + "#" + task;
    }

    public boolean isClaimed(ResourceLocation quest) {
        return claimed.contains(quest);
    }

    public void claim(ResourceLocation quest) {
        claimed.add(quest);
        counters.keySet().removeIf(key -> key.startsWith(quest + "#"));
        markDirty();
    }

    public void reset(ResourceLocation quest) {
        claimed.remove(quest);
        counters.keySet().removeIf(key -> key.startsWith(quest + "#"));
        markDirty();
    }

    public void resetAll() {
        claimed.clear();
        counters.clear();
        markDirty();
    }

    public int getCounter(ResourceLocation quest, int task) {
        return counters.getOrDefault(key(quest, task), 0);
    }

    public void setCounter(ResourceLocation quest, int task, int value) {
        counters.put(key(quest, task), value);
        markDirty();
    }

    @Override
    public void sync(ServerPlayer player) {
        player.setData(AltheraAttachments.QUESTS.get(), this);
        clearDirty();
    }
}
