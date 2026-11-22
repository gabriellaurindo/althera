package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.item.SpellBookItem;
import com.darksune.althera.common.spell.PlayerSpellData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record EquipSpellPacket(int slot, Optional<ResourceLocation> node) implements CustomPacketPayload {

    public static final Type<EquipSpellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "equip_spell"));

    public static final StreamCodec<FriendlyByteBuf, EquipSpellPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeVarInt(packet.slot);
                        buf.writeOptional(packet.node, FriendlyByteBuf::writeResourceLocation);
                    },
                    buf -> new EquipSpellPacket(buf.readVarInt(), buf.readOptional(FriendlyByteBuf::readResourceLocation))
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EquipSpellPacket packet, IPayloadContext context) {

        context.enqueueWork(() -> {

            if (!(context.player() instanceof ServerPlayer player)
                    || packet.slot < 0 || packet.slot >= PlayerSpellData.SLOT_COUNT) {
                return;
            }

            PlayerSpellData data = PlayerSpellData.get(player);

            if (packet.node.isPresent() && !data.canEquip(packet.node.get())) {
                return;
            }

            if (SpellBookItem.isCastingWith(player)) {
                player.stopUsingItem();
            }

            data.setSlot(packet.slot, packet.node.orElse(null));
        });
    }
}
