package com.darksune.althera.common.item;

import com.darksune.althera.common.academy.AcademyTravel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import com.darksune.althera.common.spell.SpellCastSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public class SpellBookItem extends Item {

    private static final int MAX_USE_TICKS = 72000;

    public SpellBookItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static boolean isHeldBy(LivingEntity entity) {
        return entity.getMainHandItem().getItem() instanceof SpellBookItem
                || entity.getOffhandItem().getItem() instanceof SpellBookItem;
    }

    public static boolean isCastingWith(LivingEntity entity) {
        return entity.isUsingItem() && entity.getUseItem().getItem() instanceof SpellBookItem;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return AcademyTravel.tryLight(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        SpellCastSystem.Cast cast = SpellCastSystem.prepareSelected(player);

        if (cast == null) {
            return InteractionResultHolder.fail(stack);
        }

        if (cast.mode().isHeld()) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            SpellCastSystem.cast(serverPlayer, cast, hand);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (entity instanceof ServerPlayer player) {
            SpellCastSystem.tickHold(player, getUseDuration(stack, entity) - remainingUseDuration + 1);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof ServerPlayer player) {
            SpellCastSystem.releaseHold(player, getUseDuration(stack, entity) - timeLeft);
        }
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        if (entity instanceof ServerPlayer player) {
            SpellCastSystem.endHold(player);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return MAX_USE_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.althera.spell_book.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
