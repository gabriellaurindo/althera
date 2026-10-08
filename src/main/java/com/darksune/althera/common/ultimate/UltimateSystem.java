package com.darksune.althera.common.ultimate;

import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.skill.SkillEndReason;
import com.darksune.althera.common.skill.SkillTimers;
import com.darksune.althera.common.system.HeroSummonSystem;
import com.darksune.althera.common.ultimate.skill.IUltimateSkill;
import com.darksune.althera.common.ultimate.skill.UltimateSkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;


public class UltimateSystem {

    //todo block summon/dismiss while an ultimate is active
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

        if (ultimateData.wasUsedToday(ultimateSkillType)) {
            player.sendSystemMessage(Component.literal("§eUltimate already used today. It recharges at the start of a new day."));
            return;
        }

        if (ultimateData.isSkillOnCooldown(ultimateSkillType)) {
            player.sendSystemMessage(Component.literal("Ultimate on cooldown"));
            return;
        }

        if (!ultimateSkill.canActivate(player, heroEntity)) {
            return;
        }

        activateSkill(player, heroEntity, ultimateData, ultimateSkillType, manaData);
    }

    private static void activateSkill(Player player, HeroEntity heroEntity, UltimateData data, UltimateSkillType ultimateSkillType, ManaData manaData) {

        IUltimateSkill ultimateSkill = ultimateSkillType.getSkill();

        manaData.consumeMana(player, ultimateSkill.getManaCost());

        data.activateSkill(ultimateSkillType, ultimateSkill.getDurationTicks());

        data.markUsedToday(ultimateSkillType);

        data.startSkillCooldown(ultimateSkillType, ultimateSkill.getCooldownTicks());

        ultimateSkill.onCooldownStart(player);

        ultimateSkill.execute(player, heroEntity);
    }

    public static void tickActiveSkills(Player player, HeroEntity heroEntity, UltimateData data) {
        SkillTimers.tick(
                data.getActiveSkills(),
                (skillType, remainingTicks) -> skillType.getSkill().tick(player, heroEntity, remainingTicks),
                skillType -> skillType.getSkill().onEnd(player, heroEntity, SkillEndReason.EXPIRED)
        );
    }

    public static void tickCooldownSkills(Player player, UltimateData data) {
        SkillTimers.tick(
                data.getCooldownSkills(),
                (skillType, remainingTicks) -> {},
                skillType -> skillType.getSkill().onCooldownExpire(player)
        );
    }

    /**
     * Ends every active skill with the given reason; each skill decides the consequence in onEnd.
     */
    public static void endActiveSkills(Player player, @Nullable HeroEntity heroEntity, SkillEndReason reason) {
        SkillTimers.endAll(
                UltimateData.get(player).getActiveSkills(),
                skillType -> skillType.getSkill().onEnd(player, heroEntity, reason)
        );
    }
}
