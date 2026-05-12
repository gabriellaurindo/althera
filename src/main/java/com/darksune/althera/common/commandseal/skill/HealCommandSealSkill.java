package com.darksune.althera.common.commandseal.skill;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroStatsSystem;
import net.minecraft.world.entity.player.Player;

public class HealCommandSealSkill extends AbstractCommandSealSkill {

    private static final int DURATION_TICKS = 20;
    private static final int COOLDOWN_TICKS = DURATION_TICKS + 20;

    //todo criar uma skill de Heal, gasta 50 de mana e recupera toda a vida do summon, se tiver vivo claro
    @Override
    public void execute(Player player, HeroEntity heroEntity) {
        final HeroData heroData = HeroData.get(player);
        heroEntity.setHealth((float) HeroStatsSystem.getMaxHealth(heroData));
    }

    @Override
    public int getCooldownTicks() {
        return COOLDOWN_TICKS;
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    public int getDurationTicks() {
        return DURATION_TICKS;
    }
}
