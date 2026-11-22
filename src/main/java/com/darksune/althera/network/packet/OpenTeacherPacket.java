package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.client.screen.JournalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenTeacherPacket(ResourceLocation teacher) implements CustomPacketPayload {

    public static final Type<OpenTeacherPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "open_teacher"));

    public static final StreamCodec<FriendlyByteBuf, OpenTeacherPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeResourceLocation(packet.teacher),
            buf -> new OpenTeacherPacket(buf.readResourceLocation())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenTeacherPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().setScreen(new JournalScreen(null, packet.teacher)));
    }
}
