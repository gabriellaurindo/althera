package com.darksune.althera.client.item;

import com.darksune.althera.Althera;
import com.darksune.althera.common.item.SpellTomeItem;
import com.darksune.althera.common.item.component.SpellTome;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.darksune.althera.common.spell.SpellProficiency;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class SpellTomeTooltip {

    private SpellTomeTooltip() {
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {

        if (!(event.getItemStack().getItem() instanceof SpellTomeItem)) {
            return;
        }

        SpellTome tome = SpellTomeItem.getTome(event.getItemStack());
        SpellNode node = tome == null ? null : tome.node();
        Player player = Minecraft.getInstance().player;

        if (node == null || player == null || event.getToolTip().isEmpty()) {
            return;
        }

        event.getToolTip().add(1, status(PlayerSpellData.get(player), tome.nodeId(), node));
    }

    private static Component status(PlayerSpellData data, ResourceLocation id, SpellNode node) {

        if (!data.isDiscovered(id)) {
            return Component.literal("Unknown to you").withStyle(ChatFormatting.LIGHT_PURPLE);
        }

        if (!data.isLearned(id)) {
            return Component.literal("Discovered · train it in the spell tree").withStyle(ChatFormatting.BLUE);
        }

        int level = data.getSpellLevel(id);

        if (level >= SpellProficiency.MAX_LEVEL) {
            return Component.literal("Mastered").withStyle(ChatFormatting.GOLD);
        }

        if (data.isSpellLevelCapped(id)) {
            String parent = node.parent().map(SpellNodeRegistry::get).map(SpellNode::displayName).orElse("?");
            return Component.literal("Learned · level " + level + " · held back by " + parent)
                    .withStyle(ChatFormatting.RED);
        }

        return Component.literal("Learned · level " + level).withStyle(ChatFormatting.GREEN);
    }
}
