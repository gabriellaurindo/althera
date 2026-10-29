package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.client.seal.SealTattoos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record SealTattooPacket(UUID playerId, int tattoo, int color, boolean cast) implements CustomPacketPayload {

    public static final Type<SealTattooPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "seal_tattoo"));

    public static final StreamCodec<FriendlyByteBuf, SealTattooPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        UUIDUtil.STREAM_CODEC.encode(buf, packet.playerId);
                        buf.writeVarInt(packet.tattoo);
                        buf.writeInt(packet.color);
                        buf.writeBoolean(packet.cast);
                    },

                    buf -> new SealTattooPacket(
                            UUIDUtil.STREAM_CODEC.decode(buf),
                            buf.readVarInt(),
                            buf.readInt(),
                            buf.readBoolean()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SealTattooPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> SealTattoos.update(packet.playerId, packet.tattoo, packet.color, packet.cast));
    }
}
