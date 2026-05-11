package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.commandseal.CommandSealSystem;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UseCommandSealSkillPacket()
        implements CustomPacketPayload {

    public static final Type<UseCommandSealSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Althera.MOD_ID,
                    "use_command_seal_skill"
            ));

    public static final StreamCodec<FriendlyByteBuf, UseCommandSealSkillPacket> STREAM_CODEC =
            StreamCodec.unit(new UseCommandSealSkillPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UseCommandSealSkillPacket packet, IPayloadContext context) {

        context.enqueueWork(() -> {

            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            CommandSealSystem.activateSkill(player, CommandSealSkillType.REVIVE);
        });
    }
}