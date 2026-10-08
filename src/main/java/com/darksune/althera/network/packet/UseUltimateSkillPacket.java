package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.ultimate.UltimateSystem;
import com.darksune.althera.common.ultimate.skill.UltimateSkillType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UseUltimateSkillPacket(UltimateSkillType skillType) implements CustomPacketPayload {

    public static final Type<UseUltimateSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Althera.MOD_ID,
                    "use_ultimate_skill"
            ));

    public static final StreamCodec<FriendlyByteBuf, UseUltimateSkillPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) ->
                            buf.writeEnum(packet.skillType),

                    buf -> new UseUltimateSkillPacket(
                            buf.readEnum(UltimateSkillType.class)
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UseUltimateSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            UltimateSystem.activateSkill(player, packet.skillType);
        });
    }
}
