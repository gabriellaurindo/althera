package com.darksune.althera.common.skill;

import net.minecraft.world.entity.player.Player;

public final class SkillLimits {

    private SkillLimits() {
    }

    public static boolean isUnlimited(final Player player) {
        return player.getAbilities().instabuild;
    }
}
