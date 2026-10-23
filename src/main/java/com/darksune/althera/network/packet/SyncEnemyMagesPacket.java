package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.enemy.EnemyMageDefinition;
import com.darksune.althera.common.enemy.EnemyMageRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncEnemyMagesPacket(Map<ResourceLocation, EnemyMageDefinition> mages) implements CustomPacketPayload {

    public static final Type<SyncEnemyMagesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "sync_enemy_mages"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEnemyMagesPacket> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap<ResourceLocation, EnemyMageDefinition>::new,
                    ResourceLocation.STREAM_CODEC,
                    ByteBufCodecs.fromCodecWithRegistries(EnemyMageDefinition.CODEC)
            ).map(SyncEnemyMagesPacket::new, packet -> new HashMap<>(packet.mages));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncEnemyMagesPacket packet, IPayloadContext context) {

        if (context.connection().isMemoryConnection()) {
            return;
        }

        context.enqueueWork(() -> {
            EnemyMageRegistry.clear();
            packet.mages.forEach(EnemyMageRegistry::register);
        });
    }
}
