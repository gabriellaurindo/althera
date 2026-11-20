package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.item.SpellBookItem;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TrainSpellNodePacket(ResourceLocation node, boolean train) implements CustomPacketPayload {

    public static final Type<TrainSpellNodePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "train_spell_node"));

    public static final StreamCodec<FriendlyByteBuf, TrainSpellNodePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeResourceLocation(packet.node);
                        buf.writeBoolean(packet.train);
                    },
                    buf -> new TrainSpellNodePacket(buf.readResourceLocation(), buf.readBoolean())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TrainSpellNodePacket packet, IPayloadContext context) {

        context.enqueueWork(() -> {

            if (!(context.player() instanceof ServerPlayer player) || SpellNodeRegistry.get(packet.node) == null) {
                return;
            }

            if (SpellBookItem.isCastingWith(player)) {
                player.stopUsingItem();
            }

            PlayerSpellData data = PlayerSpellData.get(player);

            if (packet.train) {
                data.startTraining(packet.node);
            } else {
                data.stopTraining(packet.node);
            }
        });
    }
}
