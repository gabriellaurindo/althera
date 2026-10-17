package com.darksune.althera.common.system;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRegistry;
import com.darksune.althera.network.packet.OpenHeroSelectionPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public final class HeroSelectionSystem {

    private HeroSelectionSystem() {}

    public static boolean needsSelection(final Player player) {
        return HeroData.get(player).getHeroDefinition() == null && !HeroRegistry.getSelectable().isEmpty();
    }

    public static boolean canChoose(final Player player) {
        final HeroData heroData = HeroData.get(player);
        return !HeroRegistry.getSelectable().isEmpty()
                && (heroData.getHeroDefinition() == null || !heroData.isHeroChoiceDone());
    }

    public static void requestSelection(final Player player) {
        if (player instanceof ServerPlayer serverPlayer && canChoose(player)) {
            PacketDistributor.sendToPlayer(serverPlayer, new OpenHeroSelectionPacket());
        }
    }

    public static void selectHero(final ServerPlayer player, final ResourceLocation heroId) {
        if (!canChoose(player)) {
            return;
        }

        final HeroDefinition hero = HeroRegistry.get(heroId);
        if (hero == null || !hero.isRollable()) {
            return;
        }

        final HeroData heroData = HeroData.get(player);
        final HeroDefinition current = heroData.getHeroDefinition();

        if (current != null && current.getId().equals(hero.getId())) {
            heroData.setHeroChoiceDone(true);
            heroData.sync(player);
            player.sendSystemMessage(Component.literal("§aYou keep your contract with §f" + hero.getName() + "§a."));
            return;
        }

        if (current != null) {
            HeroSummonSystem.dismissSummon(player);
        }

        heroData.setHero(hero.getId());
        heroData.setHeroChoiceDone(true);
        heroData.setHealth(HeroStatsSystem.getMaxHealth(heroData));
        heroData.sync(player);

        player.sendSystemMessage(Component.literal(
                "§aYou have formed a contract with §f" + hero.getName()
                        + " §7[" + hero.getHeroClass().getDisplayName() + "] §6(" + hero.getRank().getDisplayName() + ")"
        ));

        if (current != null) {
            player.sendSystemMessage(Component.literal(
                    "§7Your hero level (" + heroData.getLevel() + ") carries over from " + current.getName() + "."
            ));
        }

        HeroSummonSystem.spawnSummon(player);
    }
}
