package com.darksune.althera.common.attachment;

import com.darksune.althera.common.academy.AcademyData;
import com.darksune.althera.common.quest.QuestData;
import com.darksune.althera.Althera;
import com.darksune.althera.common.commandseal.CommandSealData;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.ultimate.UltimateData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AltheraAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Althera.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ManaData>> MANA =
            ATTACHMENTS.register("mana", () ->
                    AttachmentType.builder(ManaData::new)
                            .serialize(ManaData.CODEC)
                            .copyOnDeath()
                            .sync(ManaData.STREAM_CODEC)
                            .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HeroData>> HERO =
            ATTACHMENTS.register("hero", () ->
                    AttachmentType.builder(HeroData::new)
                            .serialize(HeroData.CODEC)
                            .copyOnDeath()
                            .sync(HeroData.STREAM_CODEC)
                            .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CommandSealData>> COMMAND_SEAL =
            ATTACHMENTS.register("command_seal", () ->
                    AttachmentType.builder(CommandSealData::new)
                            .serialize(CommandSealData.CODEC)
                            .copyOnDeath()
                            .sync(CommandSealData.STREAM_CODEC)
                            .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<UltimateData>> ULTIMATE =
            ATTACHMENTS.register("ultimate", () ->
                    AttachmentType.builder(UltimateData::new)
                            .serialize(UltimateData.CODEC)
                            .copyOnDeath()
                            .sync(UltimateData.STREAM_CODEC)
                            .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerSpellData>> SPELLS =
            ATTACHMENTS.register("spells", () ->
                    AttachmentType.builder(PlayerSpellData::new)
                            .serialize(PlayerSpellData.CODEC)
                            .copyOnDeath()
                            .sync(PlayerSpellData.STREAM_CODEC)
                            .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AcademyData>> ACADEMY =
            ATTACHMENTS.register("academy", () ->
                    AttachmentType.builder(AcademyData::new)
                            .serialize(AcademyData.CODEC)
                            .copyOnDeath()
                            .sync(AcademyData.STREAM_CODEC)
                            .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<QuestData>> QUESTS =
            ATTACHMENTS.register("quests", () ->
                    AttachmentType.builder(QuestData::new)
                            .serialize(QuestData.CODEC)
                            .copyOnDeath()
                            .sync(QuestData.STREAM_CODEC)
                            .build());

    public static void register(final IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}