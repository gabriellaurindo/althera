package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.quest.Quest;
import com.darksune.althera.common.quest.QuestRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncQuestsPacket(Map<ResourceLocation, Quest> quests) implements CustomPacketPayload {

    public static final Type<SyncQuestsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "sync_quests"));

    public static final StreamCodec<FriendlyByteBuf, SyncQuestsPacket> STREAM_CODEC =
            ByteBufCodecs.<FriendlyByteBuf, ResourceLocation, Quest, Map<ResourceLocation, Quest>>map(
                    HashMap::new,
                    ResourceLocation.STREAM_CODEC,
                    ByteBufCodecs.fromCodec(Quest.CODEC)
            ).map(SyncQuestsPacket::new, packet -> new HashMap<>(packet.quests));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncQuestsPacket packet, IPayloadContext context) {

        if (context.connection().isMemoryConnection()) {
            return;
        }

        context.enqueueWork(() -> {
            QuestRegistry.clear();
            packet.quests.forEach(QuestRegistry::register);
        });
    }
}
