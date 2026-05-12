package com.darksune.althera.common.ultimate.skill;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.entity.player.Player;

public interface IUltimateSkill {

    void execute(Player player, HeroEntity heroEntity);

    default void tick(Player player, HeroEntity heroEntity, int remainingTicks) {}

    default void onExpire(Player player, HeroEntity heroEntity) {}

    int getCooldownTicks();

    default void onCooldownStart(Player player) {}

    default void onCooldownExpire(Player player) {}

    int getManaCost();

    int getDurationTicks();
}
