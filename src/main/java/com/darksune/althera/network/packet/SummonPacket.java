package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
public record SummonPacket() implements CustomPacketPayload {

    public static final Type<SummonPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "summon"));

    public static final StreamCodec<FriendlyByteBuf, SummonPacket> STREAM_CODEC =
            StreamCodec.unit(new SummonPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SummonPacket packet, IPayloadContext context) {

        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            HeroSummonSystem.toggleSummon(player);
        });
    }
}
