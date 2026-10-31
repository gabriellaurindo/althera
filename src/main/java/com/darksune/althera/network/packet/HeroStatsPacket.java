package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.client.screen.HeroLiveStats;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record HeroStatsPacket(float health, float maxHealth, double attack, double armor, double speed,
                              double knockbackResistance, List<MobEffectInstance> effects)
        implements CustomPacketPayload {

    public static final Type<HeroStatsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "hero_stats"));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<MobEffectInstance>> EFFECTS_CODEC =
            MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final StreamCodec<RegistryFriendlyByteBuf, HeroStatsPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeFloat(packet.health);
                        buf.writeFloat(packet.maxHealth);
                        buf.writeDouble(packet.attack);
                        buf.writeDouble(packet.armor);
                        buf.writeDouble(packet.speed);
                        buf.writeDouble(packet.knockbackResistance);
                        EFFECTS_CODEC.encode(buf, packet.effects);
                    },

                    buf -> new HeroStatsPacket(
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readDouble(),
                            EFFECTS_CODEC.decode(buf)
                    )
            );

    public static HeroStatsPacket of(HeroEntity hero) {
        return new HeroStatsPacket(
                hero.getHealth(),
                hero.getMaxHealth(),
                hero.getAttributeValue(Attributes.ATTACK_DAMAGE),
                hero.getAttributeValue(Attributes.ARMOR),
                hero.getAttributeValue(Attributes.MOVEMENT_SPEED),
                hero.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE),
                List.copyOf(hero.getActiveEffects())
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HeroStatsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> HeroLiveStats.update(packet));
    }
}
