package com.darksune.althera.network;

import com.darksune.althera.network.packet.DisguisePacket;
import com.darksune.althera.network.packet.OpenHeroSelectionPacket;
import com.darksune.althera.network.packet.SelectHeroPacket;
import com.darksune.althera.network.packet.SummonPacket;
import com.darksune.althera.network.packet.SyncHeroClassesPacket;
import com.darksune.althera.network.packet.SyncHeroesPacket;
import com.darksune.althera.network.packet.SyncUltimatesPacket;
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

        var registrar = event.registrar("6");

        registrar.playToServer(
                SummonPacket.TYPE,
                SummonPacket.STREAM_CODEC,
                SummonPacket::handle
        );

        registrar.playToServer(
                DisguisePacket.TYPE,
                DisguisePacket.STREAM_CODEC,
                DisguisePacket::handle
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

        registrar.playToServer(
                SelectHeroPacket.TYPE,
                SelectHeroPacket.STREAM_CODEC,
                SelectHeroPacket::handle
        );

        registrar.playToClient(
                SyncHeroesPacket.TYPE,
                SyncHeroesPacket.STREAM_CODEC,
                SyncHeroesPacket::handle
        );

        registrar.playToClient(
                SyncHeroClassesPacket.TYPE,
                SyncHeroClassesPacket.STREAM_CODEC,
                SyncHeroClassesPacket::handle
        );

        registrar.playToClient(
                SyncUltimatesPacket.TYPE,
                SyncUltimatesPacket.STREAM_CODEC,
                SyncUltimatesPacket::handle
        );

        registrar.playToClient(
                OpenHeroSelectionPacket.TYPE,
                OpenHeroSelectionPacket.STREAM_CODEC,
                OpenHeroSelectionPacket::handle
        );
    }
}
