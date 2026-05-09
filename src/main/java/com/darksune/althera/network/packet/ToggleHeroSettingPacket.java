package com.darksune.althera.network.packet;

import com.darksune.althera.Althera;
import com.darksune.althera.common.attachment.HeroData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleHeroSettingPacket(Setting setting) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ToggleHeroSettingPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Althera.MOD_ID,
                            "toggle_hero_setting"
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Setting {
        HIDDEN_HUD,
        SAVE_DISABLED
    }

    public static final StreamCodec<FriendlyByteBuf, ToggleHeroSettingPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeEnum(packet.setting),
                    buf -> new ToggleHeroSettingPacket(buf.readEnum(Setting.class))
            );

    public static void handle(ToggleHeroSettingPacket packet, IPayloadContext context) {

        context.enqueueWork(() -> {

            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            HeroData heroData = HeroData.get(player);

            switch (packet.setting) {

                case HIDDEN_HUD -> {
                    heroData.setHiddenHud(!heroData.isHiddenHud());
                }

                case SAVE_DISABLED -> {
                    heroData.setSaveDisabled(!heroData.isSaveDisabled());
                }
            }

            heroData.sync(player);
        });
    }
}