package com.darksune.althera.common.attachment;

import com.darksune.althera.Althera;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber(modid = Althera.MOD_ID)
public class AttachmentSyncSystem {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        List<ISyncableAttachment> attachments = List.of(
//                player.getData(AltheraAttachments.HERO.get()),
                player.getData(AltheraAttachments.COMMAND_SEAL.get()),
                player.getData(AltheraAttachments.ULTIMATE.get())
        );

        for (ISyncableAttachment attachment : attachments) {

            if (!attachment.isDirty()) {
                continue;
            }

            attachment.sync(player);
        }
    }
}