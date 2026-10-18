package com.darksune.althera.common.ultimate.effect;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public record UltimateContext(Player owner, HeroEntity hero) {

    public ServerLevel level() {
        return (ServerLevel) hero.level();
    }
}
