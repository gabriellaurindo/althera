package com.darksune.althera.client.item;

import com.darksune.althera.common.item.HeroCatalystItem;
import com.darksune.althera.common.item.component.HeroCatalyst;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

import static com.darksune.althera.Althera.MOD_ID;

public final class CatalystClassProperty {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "catalyst_class");

    private CatalystClassProperty() {
    }

    public static float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        HeroCatalyst catalyst = HeroCatalystItem.getCatalyst(stack);
        return catalyst == null ? 0 : HeroCatalyst.BUILT_IN_CLASSES.indexOf(catalyst.classId()) + 1;
    }
}
