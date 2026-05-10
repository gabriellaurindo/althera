package com.darksune.althera.common.commandseal.skill;

import com.darksune.althera.common.entity.HeroEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class OverdriveCommandSealSkill extends AbstractCommandSealSkill implements ICommandSealSkill {

    @Override
    public void execute(Player player, HeroEntity heroEntity) {

        heroEntity.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_BOOST,
                getDurationTicks(),
                1
        ));

        heroEntity.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                getDurationTicks(),
                1
        ));

        heroEntity.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                getDurationTicks(),
                1
        ));
    }

    @Override
    public void onExpire(Player player, HeroEntity heroEntity) {
        super.onExpire(player, heroEntity);
        heroEntity.kill();
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 60;
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public int getDurationTicks() {
        return 20 * 30;
    }
}
