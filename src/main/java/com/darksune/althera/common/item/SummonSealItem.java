package com.darksune.althera.common.item;

import net.minecraft.world.item.Item;

public final class SummonSealItem extends Item {

    private SummonSealItem(Properties properties) {
        super(properties);
    }

    public static SummonSealItem create() {
        return new SummonSealItem(buildProperties());
    }

    private static Properties buildProperties() {
        return new Properties()
                .stacksTo(1);
    }
}
