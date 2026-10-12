package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.hero.HeroClass;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroNature;
import com.darksune.althera.common.hero.HeroRank;
import com.darksune.althera.common.hero.HeroRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Collection;
import java.util.List;

public record SyncHeroesPacket(List<HeroDefinition> heroes) implements CustomPacketPayload {

    public static final Type<SyncHeroesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "sync_heroes"));

    public static final StreamCodec<FriendlyByteBuf, SyncHeroesPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeCollection(packet.heroes, SyncHeroesPacket::writeHero),
                    buf -> new SyncHeroesPacket(buf.readList(SyncHeroesPacket::readHero))
            );

    public static SyncHeroesPacket of(Collection<HeroDefinition> heroes) {
        return new SyncHeroesPacket(List.copyOf(heroes));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void writeHero(FriendlyByteBuf buf, HeroDefinition hero) {
        buf.writeResourceLocation(hero.getId());
        buf.writeUtf(hero.getName());
        buf.writeUtf(hero.getDescription());
        buf.writeEnum(hero.getHeroClass());
        buf.writeEnum(hero.getRank());
        buf.writeEnum(hero.getNature());
        buf.writeNullable(hero.getModel(), FriendlyByteBuf::writeResourceLocation);
        buf.writeNullable(hero.getTexture(), FriendlyByteBuf::writeResourceLocation);
        buf.writeNullable(hero.getAnimations(), FriendlyByteBuf::writeResourceLocation);
        buf.writeUtf(hero.getPersonality());
    }

    private static HeroDefinition readHero(FriendlyByteBuf buf) {
        return new HeroDefinition(
                buf.readResourceLocation(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readEnum(HeroClass.class),
                buf.readEnum(HeroRank.class),
                buf.readEnum(HeroNature.class),
                buf.readNullable(FriendlyByteBuf::readResourceLocation),
                buf.readNullable(FriendlyByteBuf::readResourceLocation),
                buf.readNullable(FriendlyByteBuf::readResourceLocation),
                buf.readUtf()
        );
    }

    public static void handle(SyncHeroesPacket packet, IPayloadContext context) {

        if (context.connection().isMemoryConnection()) {
            return;
        }

        context.enqueueWork(() -> {
            HeroRegistry.clear();
            packet.heroes.forEach(HeroRegistry::register);
        });
    }
}
