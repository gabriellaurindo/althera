package com.darksune.althera.common.item;

import com.darksune.althera.common.item.component.AltheraDataComponents;
import com.darksune.althera.common.item.component.SpellTome;
import com.darksune.althera.common.spell.PlayerSpell;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellAdvancements;
import com.darksune.althera.common.spell.SpellCastSystem;
import com.darksune.althera.common.spell.SpellElement;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.darksune.althera.common.spell.SpellProficiency;
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

        String refusal = refusal(data, tome.nodeId(), node);

        if (refusal != null) {
            notify(player, refusal);
            return InteractionResultHolder.fail(stack);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            if (data.isLearned(tome.nodeId())) {
                practice(serverPlayer, data, tome.nodeId(), node);
            } else {
                study(serverPlayer, data, tome.nodeId(), node);
            }
        }

        stack.consume(1, player);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Nullable
    private static String refusal(PlayerSpellData data, ResourceLocation nodeId, SpellNode node) {

        if (!data.isDiscovered(nodeId)) {
            return null;
        }

        if (!data.isLearned(nodeId)) {
            return "You already know " + node.displayName() + ": train it to learn it.";
        }

        if (data.getSpellLevel(nodeId) >= SpellProficiency.MAX_LEVEL) {
            return "You have mastered " + node.displayName() + ".";
        }

        if (data.isSpellLevelCapped(nodeId)) {
            return node.displayName() + " can't level up past " + parentName(data, node) + ".";
        }

        return null;
    }

    private static void study(ServerPlayer player, PlayerSpellData data, ResourceLocation nodeId, SpellNode node) {

        data.discover(nodeId);
        playStudyEffects(player);

        boolean ready = node.parent().map(data::isLearned).orElse(true);

        player.displayClientMessage(Component.literal(ready
                ? "§bDiscovered " + node.displayName() + "! §7Train it in the spell tree."
                : "§bDiscovered " + node.displayName() + "! §7Learn " + parentName(data, node) + " to start training it."), true);
    }

    private static void practice(ServerPlayer player, PlayerSpellData data, ResourceLocation nodeId, SpellNode node) {

        playStudyEffects(player);

        int levelUp = data.addXp(nodeId, SpellElement.NONE, SpellTome.proficiencyXp(data.getSpellLevel(nodeId))).spellLevel();

        if (levelUp > 0) {
            SpellCastSystem.onLevelUp(player, node.displayName(), levelUp);
            SpellAdvancements.check(player);
            return;
        }

        player.displayClientMessage(Component.literal("§6Studied " + node.displayName() + ": "
                + Math.round(data.getSpellLevelProgress(nodeId) * 100.0F) + "% to level "
                + (data.getSpellLevel(nodeId) + 1)), true);
    }

    private static void playStudyEffects(ServerPlayer player) {
        player.serverLevel().sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.2D, player.getZ(),
                30, 0.5D, 0.6D, 0.5D, 0.8D);
        player.level().playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.6F, 1.2F);
    }

    private static String parentName(PlayerSpellData data, SpellNode node) {
        return node.parent().filter(data::isDiscovered).map(SpellNodeRegistry::get)
                .map(SpellNode::displayName).orElse("the spell before it");
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

        return Component.translatable("item.althera.spell_tome.named", node.displayName()).withStyle(node.element().color());
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

        tooltip.add(Component.translatable("item.althera.spell_tome.unlock").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.althera.spell_tome.proficiency",
                Math.round(SpellTome.PROFICIENCY_SHARE * 100.0F)).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.althera.spell_tome.tooltip").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return nodeOf(stack) != null;
    }
}
