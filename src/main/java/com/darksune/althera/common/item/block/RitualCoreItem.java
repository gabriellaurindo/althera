package com.darksune.althera.common.item.block;

import com.darksune.althera.common.block.AltheraBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class RitualCoreItem extends BlockItem {

    public RitualCoreItem(Item.Properties properties) {
        super(AltheraBlocks.RITUAL_CORE.get(), properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.althera.ritual_core.tooltip").withStyle(ChatFormatting.GRAY));
    }

    public static RitualCoreItem create() {
        return new RitualCoreItem(buildProperties());
    }

    private static Item.Properties buildProperties() {
        return new Item.Properties()
                .stacksTo(1);
    }
}
