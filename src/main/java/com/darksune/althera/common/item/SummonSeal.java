package com.darksune.althera.common.item;

import net.neoforged.neoforge.registries.DeferredRegister;

public final class SummonSeal implements ItemBase {


    @Override
    public void register(final DeferredRegister.Items items) {
        items.registerSimpleItem("summon_seal", props -> props.stacksTo(1));
    }
}
