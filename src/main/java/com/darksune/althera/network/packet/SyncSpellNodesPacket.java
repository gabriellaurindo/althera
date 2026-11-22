package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncSpellNodesPacket(Map<ResourceLocation, SpellNode> nodes) implements CustomPacketPayload {

    public static final Type<SyncSpellNodesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "sync_spell_nodes"));

    public static final StreamCodec<FriendlyByteBuf, SyncSpellNodesPacket> STREAM_CODEC =
            ByteBufCodecs.<FriendlyByteBuf, ResourceLocation, SpellNode, Map<ResourceLocation, SpellNode>>map(
                    HashMap::new,
                    ResourceLocation.STREAM_CODEC,
                    ByteBufCodecs.fromCodec(SpellNode.CODEC)
            ).map(SyncSpellNodesPacket::new, packet -> new HashMap<>(packet.nodes));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncSpellNodesPacket packet, IPayloadContext context) {

        if (context.connection().isMemoryConnection()) {
            return;
        }

        context.enqueueWork(() -> {
            SpellNodeRegistry.clear();
            packet.nodes.forEach(SpellNodeRegistry::register);
        });
    }
}
