package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestHeroStatsPacket() implements CustomPacketPayload {

    public static final Type<RequestHeroStatsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Althera.MOD_ID,
                    "request_hero_stats"
            ));

    public static final StreamCodec<FriendlyByteBuf, RequestHeroStatsPacket> STREAM_CODEC =
            StreamCodec.unit(new RequestHeroStatsPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestHeroStatsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            HeroEntity hero = HeroSummonSystem.getSummon(player);

            if (hero != null && hero.isAlive()) {
                PacketDistributor.sendToPlayer(player, HeroStatsPacket.of(hero));
            }
        });
    }
}
