package com.darksune.althera.common.item.block;

import com.darksune.althera.common.block.AltheraBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class RitualCoreItem extends BlockItem {

    public RitualCoreItem(Item.Properties properties) {
        super(AltheraBlocks.RITUAL_CORE.get(), properties);
    }

    public static RitualCoreItem create() {
        return new RitualCoreItem(buildProperties());
    }

    private static Item.Properties buildProperties() {
        return new Item.Properties()
                .stacksTo(1);
    }
}
