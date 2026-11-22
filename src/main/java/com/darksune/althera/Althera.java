package com.darksune.althera;

import com.darksune.althera.common.command.AltheraCommands;
import com.darksune.althera.common.enemy.EnemyMageLoader;
import com.darksune.althera.common.enemy.EnemyMageRegistry;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.entity.EnemyMageEntity;
import com.darksune.althera.common.entity.SummonedEntity;
import com.darksune.althera.common.hero.HeroClassLoader;
import com.darksune.althera.common.hero.HeroClassRegistry;
import com.darksune.althera.common.hero.HeroLoader;
import com.darksune.althera.common.hero.HeroRegistry;
import com.darksune.althera.common.registry.AltheraRegistries;
import com.darksune.althera.common.spell.SpellLoader;
import com.darksune.althera.common.spell.SpellNodeLoader;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.darksune.althera.common.spell.SpellRegistry;
import com.darksune.althera.common.ultimate.UltimateLoader;
import com.darksune.althera.common.ultimate.UltimateRegistry;
import com.darksune.althera.network.AltheraNetwork;
import com.darksune.althera.network.packet.SyncEnemyMagesPacket;
import com.darksune.althera.network.packet.SyncHeroClassesPacket;
import com.darksune.althera.network.packet.SyncHeroesPacket;
import com.darksune.althera.network.packet.SyncSpellNodesPacket;
import com.darksune.althera.network.packet.SyncSpellsPacket;
import com.darksune.althera.network.packet.SyncUltimatesPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.config.ModConfig;
import com.darksune.althera.common.AltheraServerConfig;
import com.darksune.althera.common.quest.QuestLoader;
import com.darksune.althera.common.quest.QuestRegistry;
import com.darksune.althera.network.packet.SyncQuestsPacket;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
@Mod(Althera.MOD_ID)
public final class Althera {

    public static final String MOD_ID = "althera";

    public Althera(final IEventBus modEventBus, final ModContainer modContainer) {
        AltheraRegistries.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.SERVER, AltheraServerConfig.SPEC);
        AltheraNetwork.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(this::onReloadListeners);
    }

    private void onReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new HeroClassLoader());
        event.addListener(new HeroLoader(event.getRegistryAccess()));
        event.addListener(new UltimateLoader(event.getRegistryAccess()));
        event.addListener(new SpellLoader(event.getRegistryAccess()));
        event.addListener(new SpellNodeLoader());
        event.addListener(new EnemyMageLoader(event.getRegistryAccess()));
        event.addListener(new QuestLoader(event.getRegistryAccess()));
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        SyncHeroClassesPacket classes = new SyncHeroClassesPacket(HeroClassRegistry.getAll());
        SyncHeroesPacket heroes = SyncHeroesPacket.of(HeroRegistry.getAll());
        SyncUltimatesPacket ultimates = new SyncUltimatesPacket(UltimateRegistry.getAll());
        SyncSpellsPacket spells = new SyncSpellsPacket(SpellRegistry.getAll());
        SyncSpellNodesPacket spellNodes = new SyncSpellNodesPacket(SpellNodeRegistry.getAll());
        SyncEnemyMagesPacket mages = new SyncEnemyMagesPacket(EnemyMageRegistry.getAll());
        SyncQuestsPacket quests = new SyncQuestsPacket(QuestRegistry.getAll());
        event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, classes, heroes, ultimates, spells, spellNodes, mages, quests));
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        AltheraCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(
                AltheraEntities.HERO.get(),
                HeroEntity.createAttributes().build()
        );
        event.put(
                AltheraEntities.SUMMONED.get(),
                SummonedEntity.createAttributes().build()
        );
        event.put(
                AltheraEntities.ENEMY_MAGE.get(),
                EnemyMageEntity.createAttributes().build()
        );
    }
}
