package com.darksune.althera.common.commandseal.skill;

import com.darksune.althera.common.commandseal.CommandSealData;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractCommandSealSkill implements ICommandSealSkill {

    @Override
    public void onExpire(Player player, HeroEntity heroEntity) {
        markDirty(player);
    }

    @Override
    public void onCooldownStart(Player player) {
        markDirty(player);
    }

    @Override
    public void onCooldownExpire(Player player) {
        markDirty(player);
    }

    protected void markDirty(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            CommandSealData.get(serverPlayer).markDirty();
        }
    }
}
