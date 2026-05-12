package com.darksune.althera.common.ultimate;

import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroSummonSystem;
import com.darksune.althera.common.ultimate.skill.IUltimateSkill;
import com.darksune.althera.common.ultimate.skill.UltimateSkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.Map;

public class UltimateSystem {

    //todo impedir spawn e despawn quando a ultimate foi ativa
    //todo fazer funcionar uma vez por dia só
    public static void activateSkill(Player player, UltimateSkillType ultimateSkillType) {

        HeroEntity heroEntity = HeroSummonSystem.getSummon(player);

        if (ultimateSkillType.requiresHeroEntity() && heroEntity == null || !ultimateSkillType.requiresHeroEntity() && heroEntity != null) {
            player.sendSystemMessage(Component.literal("Condicao nao atendida"));
            return;
        }

        UltimateData ultimateData = UltimateData.get(player);

        ManaData manaData = ManaData.get(player);

        IUltimateSkill ultimateSkill = ultimateSkillType.getSkill();

        if (!manaData.hasEnoughMana(ultimateSkill.getManaCost())) {
            player.sendSystemMessage(Component.literal("Not enough mana"));
            return;
        }

        if (ultimateData.isSkillOnCooldown(ultimateSkillType)) {
            player.sendSystemMessage(Component.literal("Ultimate on cooldown"));
            return;
        }

        activateSkill(player, heroEntity, ultimateData, ultimateSkillType, manaData);
    }

    private static void activateSkill(Player player, HeroEntity heroEntity, UltimateData data, UltimateSkillType ultimateSkillType, ManaData manaData) {

        IUltimateSkill ultimateSkill = ultimateSkillType.getSkill();

        manaData.consumeMana(player, ultimateSkill.getManaCost());

        data.activateSkill(ultimateSkillType, ultimateSkill.getDurationTicks());

        data.startSkillCooldown(ultimateSkillType, ultimateSkill.getCooldownTicks());

        ultimateSkill.onCooldownStart(player);

        ultimateSkill.execute(player, heroEntity);
    }

    public static void tickActiveSkills(Player player, HeroEntity heroEntity, UltimateData data) {

        Iterator<Map.Entry<UltimateSkillType, Integer>> iterator = data.getActiveSkills().entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<UltimateSkillType, Integer> entry = iterator.next();

            UltimateSkillType ultimateSkillType = entry.getKey();

            IUltimateSkill ultimateSkill = ultimateSkillType.getSkill();

            int remainingTicks = entry.getValue() - 1;

            if (remainingTicks <= 0) {

                ultimateSkill.onExpire(player, heroEntity);

                iterator.remove();

                continue;
            }

            entry.setValue(remainingTicks);

            ultimateSkill.tick(player, heroEntity, remainingTicks);
        }
    }

    public static void tickCooldownSkills(Player player, UltimateData data) {

        Iterator<Map.Entry<UltimateSkillType, Integer>> iterator = data.getCooldownSkills().entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<UltimateSkillType, Integer> entry = iterator.next();

            UltimateSkillType ultimateSkillType = entry.getKey();

            IUltimateSkill ultimateSkill = ultimateSkillType.getSkill();

            int remainingTicks = entry.getValue() - 1;

            if (remainingTicks <= 0) {

                ultimateSkill.onCooldownExpire(player);

                iterator.remove();

                continue;
            }

            entry.setValue(remainingTicks);
        }
    }
}
