package com.darksune.althera.common.item;

import com.darksune.althera.common.hero.HeroRank;
import com.darksune.althera.common.item.component.AltheraDataComponents;
import com.darksune.althera.common.item.component.HeroCatalyst;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

public class HeroCatalystItem extends Item {

    public HeroCatalystItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static ItemStack of(ResourceLocation classId, HeroRank rank) {
        ItemStack stack = new ItemStack(AltheraItems.HERO_CATALYST.get());
        stack.set(AltheraDataComponents.HERO_CATALYST.get(), new HeroCatalyst(classId, rank));
        return stack;
    }

    @Nullable
    public static HeroCatalyst getCatalyst(ItemStack stack) {
        return stack.get(AltheraDataComponents.HERO_CATALYST.get());
    }

    @Override
    public Component getName(ItemStack stack) {
        HeroCatalyst catalyst = getCatalyst(stack);
        if (catalyst == null || !catalyst.isKnownClass()) {
            return Component.translatable("item.althera.hero_catalyst.faded").withStyle(ChatFormatting.GRAY);
        }
        return Component.translatable("item.althera.hero_catalyst.named", catalyst.heroClass().getDisplayName())
                .withStyle(catalyst.rank().getColor());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        HeroCatalyst catalyst = getCatalyst(stack);
        if (catalyst == null) {
            return;
        }
        HeroRank rank = catalyst.rank();
        tooltip.add(Component.translatable("item.althera.hero_catalyst.rank",
                Component.literal(rank.getDisplayName()).withStyle(rank.getColor())).withStyle(ChatFormatting.GRAY));
        if (!catalyst.isKnownClass()) {
            tooltip.add(Component.translatable("item.althera.hero_catalyst.faded.hint").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
