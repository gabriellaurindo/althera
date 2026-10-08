package com.darksune.althera.network;

import com.darksune.althera.network.packet.SummonPacket;
import com.darksune.althera.network.packet.ToggleHeroSettingPacket;
import com.darksune.althera.network.packet.UseCommandSealSkillPacket;
import com.darksune.althera.network.packet.UseUltimateSkillPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class AltheraNetwork {

    private AltheraNetwork() {}

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(AltheraNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {

        var registrar = event.registrar("1");

        registrar.playToServer(
                SummonPacket.TYPE,
                SummonPacket.STREAM_CODEC,
                SummonPacket::handle
        );

        registrar.playToServer(
                ToggleHeroSettingPacket.TYPE,
                ToggleHeroSettingPacket.STREAM_CODEC,
                ToggleHeroSettingPacket::handle
        );

        registrar.playToServer(
                UseCommandSealSkillPacket.TYPE,
                UseCommandSealSkillPacket.STREAM_CODEC,
                UseCommandSealSkillPacket::handle
        );

        registrar.playToServer(
                UseUltimateSkillPacket.TYPE,
                UseUltimateSkillPacket.STREAM_CODEC,
                UseUltimateSkillPacket::handle
        );
    }
}
