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

    //todo criar niveis 1 nivel = 30 segundos e status +1, 2 nivel = 20s status +2, 3 nivel = 10s status + 3
    @Override
    public void execute(Player player, HeroEntity heroEntity) {

        final HeroData heroData = HeroData.get(player);

        if (!heroData.canResurrect()) {
            player.sendSystemMessage(Component.literal("deu ruim"));
            return;
        }

        heroData.setHealth(HeroStatsSystem.getMaxHealth(heroData));
        heroData.setDefeated(false);
        HeroSummonSystem.spawnSummon(player);
    }

    @Override
    public int getCooldownTicks() {
        return COOLDOWN_TICKS;
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public int getDurationTicks() {
        return DURATION_TICKS;
    }
}
