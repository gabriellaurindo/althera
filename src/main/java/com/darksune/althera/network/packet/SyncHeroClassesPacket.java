package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.hero.HeroClass;
import com.darksune.althera.common.hero.HeroClassRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncHeroClassesPacket(Map<ResourceLocation, HeroClass> classes) implements CustomPacketPayload {

    public static final Type<SyncHeroClassesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "sync_hero_classes"));

    public static final StreamCodec<FriendlyByteBuf, SyncHeroClassesPacket> STREAM_CODEC =
            ByteBufCodecs.<FriendlyByteBuf, ResourceLocation, HeroClass, Map<ResourceLocation, HeroClass>>map(
                    HashMap::new,
                    ResourceLocation.STREAM_CODEC,
                    ByteBufCodecs.fromCodec(HeroClass.CODEC)
            ).map(SyncHeroClassesPacket::new, packet -> new HashMap<>(packet.classes));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncHeroClassesPacket packet, IPayloadContext context) {

        if (context.connection().isMemoryConnection()) {
            return;
        }

        context.enqueueWork(() -> {
            HeroClassRegistry.clear();
            packet.classes.forEach(HeroClassRegistry::register);
        });
    }
}
