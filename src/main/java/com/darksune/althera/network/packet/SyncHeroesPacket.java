package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.hero.HeroAttack;
import com.darksune.althera.common.hero.HeroClass;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroNature;
import com.darksune.althera.common.hero.HeroRank;
import com.darksune.althera.common.hero.HeroRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record SyncHeroesPacket(List<HeroDefinition> heroes) implements CustomPacketPayload {

    public static final Type<SyncHeroesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "sync_heroes"));

    private static final StreamCodec<RegistryFriendlyByteBuf, HeroAttack> ATTACK_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(HeroAttack.CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncHeroesPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeVarInt(packet.heroes.size());
                        packet.heroes.forEach(hero -> writeHero(buf, hero));
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        List<HeroDefinition> heroes = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            heroes.add(readHero(buf));
                        }
                        return new SyncHeroesPacket(heroes);
                    }
            );

    public static SyncHeroesPacket of(Collection<HeroDefinition> heroes) {
        return new SyncHeroesPacket(List.copyOf(heroes));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void writeHero(RegistryFriendlyByteBuf buf, HeroDefinition hero) {
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
        buf.writeFloat(hero.getScale());
        buf.writeResourceLocation(hero.getUltimate());
        writeAttack(buf, hero.getAttack());
    }

    private static void writeAttack(RegistryFriendlyByteBuf buf, HeroAttack attack) {
        ATTACK_CODEC.encode(buf, attack);
    }

    private static HeroAttack readAttack(RegistryFriendlyByteBuf buf) {
        return ATTACK_CODEC.decode(buf);
    }

    private static HeroDefinition readHero(RegistryFriendlyByteBuf buf) {
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
                buf.readUtf(),
                buf.readFloat(),
                buf.readResourceLocation(),
                readAttack(buf)
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
