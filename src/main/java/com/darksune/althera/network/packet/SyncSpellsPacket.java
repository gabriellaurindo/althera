package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.spell.SpellDefinition;
import com.darksune.althera.common.spell.SpellRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncSpellsPacket(Map<ResourceLocation, SpellDefinition> spells) implements CustomPacketPayload {

    public static final Type<SyncSpellsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "sync_spells"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSpellsPacket> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap<ResourceLocation, SpellDefinition>::new,
                    ResourceLocation.STREAM_CODEC,
                    ByteBufCodecs.fromCodecWithRegistries(SpellDefinition.CODEC)
            ).map(SyncSpellsPacket::new, packet -> new HashMap<>(packet.spells));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncSpellsPacket packet, IPayloadContext context) {

        if (context.connection().isMemoryConnection()) {
            return;
        }

        context.enqueueWork(() -> {
            SpellRegistry.clear();
            packet.spells.forEach(SpellRegistry::register);
        });
    }
}
