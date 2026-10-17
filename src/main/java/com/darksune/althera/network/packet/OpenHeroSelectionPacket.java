package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.client.screen.HeroSelectionOpener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenHeroSelectionPacket() implements CustomPacketPayload {

    public static final Type<OpenHeroSelectionPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "open_hero_selection"));

    public static final StreamCodec<FriendlyByteBuf, OpenHeroSelectionPacket> STREAM_CODEC =
            StreamCodec.unit(new OpenHeroSelectionPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenHeroSelectionPacket packet, IPayloadContext context) {
        context.enqueueWork(HeroSelectionOpener::request);
    }
}
