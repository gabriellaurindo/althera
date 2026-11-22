package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.spell.PlayerSpellData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SeenSpellNodesPacket() implements CustomPacketPayload {

    public static final SeenSpellNodesPacket INSTANCE = new SeenSpellNodesPacket();

    public static final Type<SeenSpellNodesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "seen_spell_nodes"));

    public static final StreamCodec<FriendlyByteBuf, SeenSpellNodesPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SeenSpellNodesPacket packet, IPayloadContext context) {

        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerSpellData.get(player).clearFresh();
            }
        });
    }
}
