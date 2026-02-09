package com.darksune.althera.common.item.definition;

import com.darksune.althera.common.item.SummonSealItem;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

import static com.darksune.althera.common.item.AltheraItemName.SUMMON_SEAL;

public final class SummonSealDefinition implements AltheraItemDefinition {


    @Override
    public void register(final Items items) {
        items.register(SUMMON_SEAL, SummonSealItem::create);
    }
}
