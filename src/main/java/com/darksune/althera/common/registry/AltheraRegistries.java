package com.darksune.althera.common.registry;

import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.item.AltheraItems;
import net.neoforged.bus.api.IEventBus;

public final class AltheraRegistries {

    public static void register(final IEventBus modEventBus) {
        AltheraItems.register(modEventBus);
        AltheraEntities.register(modEventBus);
        AltheraAttachments.register(modEventBus);
    }
}
