package com.darksune.althera.common.commandseal.skill;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.skill.SkillEndReason;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class OverdriveCommandSealSkill extends AbstractCommandSealSkill {

    private static final int DURATION_TICKS = 20 * 30;
    private static final int COOLDOWN_TICKS = DURATION_TICKS + 20;

    //todo add levels: level 1 = 30s and stats +1, level 2 = 20s and stats +2, level 3 = 10s and stats +3
    @Override
    public void execute(Player player, HeroEntity heroEntity) {

        HeroData heroData = HeroData.get(player);

        heroData.setCanResurrect(false);

        heroEntity.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_BOOST,
                DURATION_TICKS,
                0
        ));

        heroEntity.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                DURATION_TICKS,
                0
        ));

        heroEntity.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                DURATION_TICKS,
                0
        ));
    }

    @Override
    public void onEnd(Player player, HeroEntity heroEntity, SkillEndReason reason) {
        super.onEnd(player, heroEntity, reason);
        // overdrive's price: the hero falls when it ends, even if it was dismissed earlier
        if (reason != SkillEndReason.HERO_DEFEATED) {
            HeroSummonSystem.defeatSummon(player);
        }
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
