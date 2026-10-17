package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.system.HeroSelectionSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SelectHeroPacket(ResourceLocation heroId) implements CustomPacketPayload {

    public static final Type<SelectHeroPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Althera.MOD_ID,
                    "select_hero"
            ));

    public static final StreamCodec<FriendlyByteBuf, SelectHeroPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) ->
                            buf.writeResourceLocation(packet.heroId),

                    buf -> new SelectHeroPacket(
                            buf.readResourceLocation()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SelectHeroPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            HeroSelectionSystem.selectHero(player, packet.heroId);
        });
    }
}
