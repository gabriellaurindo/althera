package com.darksune.althera.common.item;

import com.darksune.althera.common.item.component.AltheraDataComponents;
import com.darksune.althera.common.item.component.SpellTome;
import com.darksune.althera.common.spell.PlayerSpell;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellCastSystem;
import com.darksune.althera.common.spell.SpellElement;
import com.darksune.althera.common.spell.SpellNode;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

public class SpellTomeItem extends Item {

    public SpellTomeItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    public static ItemStack of(ResourceLocation nodeId) {
        ItemStack stack = new ItemStack(AltheraItems.SPELL_TOME.get());
        stack.set(AltheraDataComponents.SPELL_TOME.get(), new SpellTome(nodeId));
        return stack;
    }

    @Nullable
    public static SpellTome getTome(ItemStack stack) {
        return stack.get(AltheraDataComponents.SPELL_TOME.get());
    }

    @Nullable
    private static SpellNode nodeOf(ItemStack stack) {
        SpellTome tome = getTome(stack);
        return tome == null ? null : tome.node();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        SpellTome tome = getTome(stack);
        SpellNode node = nodeOf(stack);

        if (tome == null || node == null) {
            notify(player, "The words in this tome have faded.");
            return InteractionResultHolder.fail(stack);
        }

        PlayerSpellData data = PlayerSpellData.get(player);

        if (data.isLearned(tome.nodeId())) {
            notify(player, "You already know " + node.displayName() + ".");
            return InteractionResultHolder.fail(stack);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            study(serverPlayer, data, tome.nodeId(), node);
        }

        stack.consume(1, player);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static void study(ServerPlayer player, PlayerSpellData data, ResourceLocation nodeId, SpellNode node) {

        player.serverLevel().sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.2D, player.getZ(),
                30, 0.5D, 0.6D, 0.5D, 0.8D);
        player.level().playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.6F, 1.2F);

        if (data.addProgress(nodeId, SpellTome.progressFor(node))) {
            SpellCastSystem.onLearned(player, node);
            return;
        }

        int percent = (int) (data.getProgress(nodeId) * 100.0F);
        player.displayClientMessage(Component.literal("§bStudied " + node.displayName() + ": " + percent + "%"), true);
    }

    private static void notify(Player player, String message) {
        if (!player.level().isClientSide) {
            player.displayClientMessage(Component.literal("§c" + message), true);
        }
    }

    @Override
    public Component getName(ItemStack stack) {

        SpellNode node = nodeOf(stack);

        if (node == null) {
            return Component.translatable("item.althera.spell_tome.faded").withStyle(ChatFormatting.GRAY);
        }

        return Component.translatable("item.althera.spell_tome.named", node.displayName()).withStyle(color(node.element()));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        SpellNode node = nodeOf(stack);

        if (node == null) {
            if (getTome(stack) != null) {
                tooltip.add(Component.translatable("item.althera.spell_tome.faded.hint").withStyle(ChatFormatting.DARK_GRAY));
            }
            return;
        }

        tooltip.add(Component.translatable("item.althera.spell_tome.details",
                StringUtils.capitalize(node.element().getSerializedName()), node.tier()).withStyle(ChatFormatting.DARK_GRAY));

        node.spellDefinition().player().map(PlayerSpell::description).filter(text -> !text.isEmpty())
                .ifPresent(text -> tooltip.add(Component.literal(text).withStyle(ChatFormatting.GRAY)));

        tooltip.add(Component.translatable("item.althera.spell_tome.progress",
                Math.round(SpellTome.progressFor(node) * 100.0F)).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.althera.spell_tome.tooltip").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return nodeOf(stack) != null;
    }

    private static ChatFormatting color(SpellElement element) {
        return switch (element) {
            case FIRE -> ChatFormatting.GOLD;
            case LIGHTNING -> ChatFormatting.YELLOW;
            case ICE -> ChatFormatting.AQUA;
            case NONE -> ChatFormatting.LIGHT_PURPLE;
        };
    }
}
