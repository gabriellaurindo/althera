package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.ultimate.UltimateSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UseUltimateSkillPacket() implements CustomPacketPayload {

    public static final Type<UseUltimateSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Althera.MOD_ID,
                    "use_ultimate_skill"
            ));

    public static final StreamCodec<FriendlyByteBuf, UseUltimateSkillPacket> STREAM_CODEC =
            StreamCodec.unit(new UseUltimateSkillPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UseUltimateSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            UltimateSystem.activateUltimate(player);
        });
    }
}
