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

public record SelectSpellSlotPacket(int slot) implements CustomPacketPayload {

    public static final Type<SelectSpellSlotPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "select_spell_slot"));

    public static final StreamCodec<FriendlyByteBuf, SelectSpellSlotPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeVarInt(packet.slot),
                    buf -> new SelectSpellSlotPacket(buf.readVarInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SelectSpellSlotPacket packet, IPayloadContext context) {

        context.enqueueWork(() -> {

            if (!(context.player() instanceof ServerPlayer player)
                    || packet.slot < 0 || packet.slot >= PlayerSpellData.SLOT_COUNT) {
                return;
            }

            if (SpellBookItem.isCastingWith(player)) {
                player.stopUsingItem();
            }

            PlayerSpellData.get(player).setSelectedSlot(packet.slot);
        });
    }
}
