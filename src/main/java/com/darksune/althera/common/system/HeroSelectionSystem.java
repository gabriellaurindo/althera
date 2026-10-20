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

    public static void requestSelection(final Player player) {
        if (player instanceof ServerPlayer serverPlayer && needsSelection(player)) {
            PacketDistributor.sendToPlayer(serverPlayer, new OpenHeroSelectionPacket());
        }
    }

    public static void selectHero(final ServerPlayer player, final ResourceLocation heroId) {
        if (!needsSelection(player)) {
            return;
        }

        final HeroDefinition hero = HeroRegistry.get(heroId);
        if (hero == null || !hero.isRollable()) {
            return;
        }

        final HeroData heroData = HeroData.get(player);
        heroData.setHero(hero.getId());
        heroData.setHealth(HeroStatsSystem.getMaxHealth(heroData));
        heroData.sync(player);

        player.sendSystemMessage(Component.literal(
                "§aYou have formed a contract with §f" + hero.getName()
                        + " §7[" + hero.getHeroClass().getDisplayName() + "] §6(" + hero.getRank().getDisplayName() + ")"
        ));

        HeroSummonSystem.spawnSummon(player);
    }
}
