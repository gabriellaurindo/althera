package com.darksune.althera.common.commandseal.skill;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class OverdriveCommandSealSkill extends AbstractCommandSealSkill {

    private static final int DURATION_TICKS = 20 * 30;
    private static final int COOLDOWN_TICKS = DURATION_TICKS + 20;

    //todo criar niveis 1 nivel = 30 segundos e status +1, 2 nivel = 20s status +2, 3 nivel = 10s status + 3
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
    public void onExpire(Player player, HeroEntity heroEntity) {
        super.onExpire(player, heroEntity);
        heroEntity.kill();
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
