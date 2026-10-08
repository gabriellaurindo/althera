package com.darksune.althera.common.commandseal.skill;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ReviveCommandSealSkill extends AbstractCommandSealSkill {

    private static final int DURATION_TICKS = 20;
    private static final int COOLDOWN_TICKS = DURATION_TICKS + 20;

    @Override
    public boolean canActivate(Player player, HeroEntity heroEntity) {
        final HeroData heroData = HeroData.get(player);

        if (!heroData.isDefeated()) {
            player.sendSystemMessage(Component.literal("§eYour summon is not defeated."));
            return false;
        }

        if (!heroData.canResurrect()) {
            player.sendSystemMessage(Component.literal("§cYour summon cannot be revived until it fully recovers."));
            return false;
        }

        return true;
    }

    @Override
    public void execute(Player player, HeroEntity heroEntity) {

        final HeroData heroData = HeroData.get(player);

        heroData.setHealth(HeroStatsSystem.getMaxHealth(heroData));
        heroData.setDefeated(false);
        // the skill already charged its own cost; summoning requires no extra mana
        HeroSummonSystem.spawnSummon(player, true, false);
    }

    @Override
    public int getCooldownTicks() {
        return COOLDOWN_TICKS;
    }

    @Override
    public int getManaCost() {
        return 150;
    }

    @Override
    public int getDurationTicks() {
        return DURATION_TICKS;
    }
}
