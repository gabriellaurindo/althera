package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.ultimate.UltimateDefinition;
import com.darksune.althera.common.ultimate.UltimateRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncUltimatesPacket(Map<ResourceLocation, UltimateDefinition> ultimates) implements CustomPacketPayload {

    public static final Type<SyncUltimatesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "sync_ultimates"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncUltimatesPacket> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap<ResourceLocation, UltimateDefinition>::new,
                    ResourceLocation.STREAM_CODEC,
                    ByteBufCodecs.fromCodecWithRegistries(UltimateDefinition.CODEC)
            ).map(SyncUltimatesPacket::new, packet -> new HashMap<>(packet.ultimates));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncUltimatesPacket packet, IPayloadContext context) {

        if (context.connection().isMemoryConnection()) {
            return;
        }

        context.enqueueWork(() -> {
            UltimateRegistry.clear();
            packet.ultimates.forEach(UltimateRegistry::register);
        });
    }
}
