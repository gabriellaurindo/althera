package com.darksune.althera.common.attachment;

import com.darksune.althera.Althera;
import com.darksune.althera.common.commandseal.CommandSealData;
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

    public static void register(final IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}