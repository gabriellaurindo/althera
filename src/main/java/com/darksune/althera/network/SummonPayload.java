package com.darksune.althera.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SummonPayload() implements CustomPacketPayload {

    public static final Type<SummonPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("althera", "summon"));

    public static final StreamCodec<FriendlyByteBuf, SummonPayload> STREAM_CODEC =
            StreamCodec.unit(new SummonPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}