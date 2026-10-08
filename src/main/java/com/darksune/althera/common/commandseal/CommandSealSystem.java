package com.darksune.althera.common.commandseal;

import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.darksune.althera.common.commandseal.skill.ICommandSealSkill;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.skill.SkillEndReason;
import com.darksune.althera.common.skill.SkillTimers;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;


public class CommandSealSystem {

    public static void activateSkill(final Player player, final CommandSealSkillType commandSealSkillType) {
        final HeroEntity hero = HeroSummonSystem.getSummon(player);
        if (commandSealSkillType.requiresHeroEntity() && hero == null || !commandSealSkillType.requiresHeroEntity() && hero != null) {
            player.sendSystemMessage(Component.literal("Condicao nao atendida"));
            return;
        }
        final CommandSealData commandSealData = CommandSealData.get(player);
        if (!commandSealData.hasCharges()) {
            player.sendSystemMessage(Component.literal("Sem charges"));
            return;
        }
        final ManaData manaData = ManaData.get(player);
        if (!manaData.hasEnoughMana(commandSealSkillType.getSkill().getManaCost())) {
            player.sendSystemMessage(Component.literal("Sem manaaa"));
            return;
        }
        if (commandSealData.isSkillOnCooldown(commandSealSkillType)) {
            player.sendSystemMessage(Component.literal("Skill em cooldown"));
            return;
        }
        if (!commandSealSkillType.getSkill().canActivate(player, hero)) {
            return;
        }
        activateSkill(player, hero, commandSealData, commandSealSkillType, manaData);
    }

    private static void activateSkill(Player player, HeroEntity heroEntity, CommandSealData data, CommandSealSkillType commandSealSkillType, ManaData manaData) {

        ICommandSealSkill commandSealSkill = commandSealSkillType.getSkill();

        manaData.consumeMana(player, commandSealSkill.getManaCost());
        data.consumeCharge();

        data.activateSkill(commandSealSkillType, commandSealSkill.getDurationTicks());

        data.startSkillCooldown(commandSealSkillType, commandSealSkill.getCooldownTicks());

        commandSealSkill.onCooldownStart(player);

        commandSealSkill.execute(player, heroEntity);
    }

    public static void tickActiveSkills(Player player, HeroEntity heroEntity, CommandSealData data) {
        SkillTimers.tick(
                data.getActiveSkills(),
                (skillType, remainingTicks) -> skillType.getSkill().tick(player, heroEntity, remainingTicks),
                skillType -> skillType.getSkill().onEnd(player, heroEntity, SkillEndReason.EXPIRED)
        );
    }

    public static void tickCooldownSkills(Player player, CommandSealData data) {
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
                CommandSealData.get(player).getActiveSkills(),
                skillType -> skillType.getSkill().onEnd(player, heroEntity, reason)
        );
    }
}
