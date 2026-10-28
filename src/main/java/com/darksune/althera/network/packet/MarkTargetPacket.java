package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.system.HeroCommandSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MarkTargetPacket(int entityId) implements CustomPacketPayload {

    public static final Type<MarkTargetPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Althera.MOD_ID,
                    "mark_target"
            ));

    public static final StreamCodec<FriendlyByteBuf, MarkTargetPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) ->
                            buf.writeVarInt(packet.entityId),

                    buf -> new MarkTargetPacket(
                            buf.readVarInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MarkTargetPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            HeroCommandSystem.markTarget(player, packet.entityId);
        });
    }
}
