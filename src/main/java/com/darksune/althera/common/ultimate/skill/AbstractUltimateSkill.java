package com.darksune.althera.common.ultimate.skill;

import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.ultimate.UltimateData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractUltimateSkill implements IUltimateSkill {

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
            UltimateData.get(serverPlayer).markDirty();
        }
    }
}
