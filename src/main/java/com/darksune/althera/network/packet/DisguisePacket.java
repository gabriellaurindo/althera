package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
public record DisguisePacket() implements CustomPacketPayload {

    public static final Type<DisguisePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "disguise"));

    public static final StreamCodec<FriendlyByteBuf, DisguisePacket> STREAM_CODEC =
            StreamCodec.unit(new DisguisePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DisguisePacket packet, IPayloadContext context) {

        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            HeroSummonSystem.toggleDisguise(player);
        });
    }
}
