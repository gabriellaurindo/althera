package com.darksune.althera.common.commandseal.skill;

import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.skill.SkillEndReason;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface ICommandSealSkill {

    /**
     * Skill-specific conditions, checked before any resource is consumed.
     * Implementations returning false are responsible for telling the player why.
     */
    default boolean canActivate(Player player, @Nullable HeroEntity heroEntity) {
        return true;
    }

    void execute(Player player, HeroEntity heroEntity);

    default void tick(Player player, HeroEntity heroEntity, int remainingTicks) {}

    default void onEnd(Player player, @Nullable HeroEntity heroEntity, SkillEndReason reason) {}

    int getCooldownTicks();

    default void onCooldownStart(Player player) {}

    default void onCooldownExpire(Player player) {}

    int getManaCost();

    int getDurationTicks();
}
