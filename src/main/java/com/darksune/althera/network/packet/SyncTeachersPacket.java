package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.academy.teacher.TeacherDefinition;
import com.darksune.althera.common.academy.teacher.TeacherRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncTeachersPacket(Map<ResourceLocation, TeacherDefinition> teachers) implements CustomPacketPayload {

    public static final Type<SyncTeachersPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "sync_teachers"));

    public static final StreamCodec<FriendlyByteBuf, SyncTeachersPacket> STREAM_CODEC =
            ByteBufCodecs.<FriendlyByteBuf, ResourceLocation, TeacherDefinition, Map<ResourceLocation, TeacherDefinition>>map(
                    HashMap::new,
                    ResourceLocation.STREAM_CODEC,
                    ByteBufCodecs.fromCodec(TeacherDefinition.CODEC)
            ).map(SyncTeachersPacket::new, packet -> new HashMap<>(packet.teachers));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncTeachersPacket packet, IPayloadContext context) {

        if (context.connection().isMemoryConnection()) {
            return;
        }

        context.enqueueWork(() -> {
            TeacherRegistry.clear();
            packet.teachers.forEach(TeacherRegistry::register);
        });
    }
}
