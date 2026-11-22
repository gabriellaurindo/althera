package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.quest.QuestSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClaimQuestPacket(ResourceLocation quest) implements CustomPacketPayload {

    public static final Type<ClaimQuestPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "claim_quest"));

    public static final StreamCodec<FriendlyByteBuf, ClaimQuestPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeResourceLocation(packet.quest),
            buf -> new ClaimQuestPacket(buf.readResourceLocation())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClaimQuestPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                QuestSystem.claim(player, packet.quest);
            }
        });
    }
}
