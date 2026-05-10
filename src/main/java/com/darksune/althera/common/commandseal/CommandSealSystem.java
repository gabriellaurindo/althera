package com.darksune.althera.common.commandseal;

import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.darksune.althera.common.commandseal.skill.ICommandSealSkill;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroSummonSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.Map;

import static com.darksune.althera.common.commandseal.skill.CommandSealSkillType.OVERDRIVE;

public class CommandSealSystem {

    public static void activateSkill(final Player player) {
        final HeroEntity hero = HeroSummonSystem.getSummon(player);
        if (hero == null) {
            player.sendSystemMessage(Component.literal("Só pra testar"));
            return;
        }
        final ManaData manaData = ManaData.get(player);
        if (!manaData.consumeMana(player, OVERDRIVE.getSkill().getManaCost())) {
            player.sendSystemMessage(Component.literal("Sem manaaa"));
            return;
        }

        final CommandSealData commandSealData = CommandSealData.get(player);
        activateSkill(player, hero, commandSealData, OVERDRIVE);
    }

    public static void activateSkill(Player player, HeroEntity heroEntity, CommandSealData data, CommandSealSkillType commandSealSkillType) {

        if (data.isSkillOnCooldown(commandSealSkillType)) {
            return;
        }

        ICommandSealSkill commandSealSkill = commandSealSkillType.getSkill();

        data.activateSkill(commandSealSkillType, commandSealSkill.getDurationTicks());

        data.startSkillCooldown(commandSealSkillType, commandSealSkill.getCooldownTicks());

        commandSealSkill.onCooldownStart(player);

        commandSealSkill.execute(player, heroEntity);
    }

    public static void tickActiveSkills(Player player, HeroEntity heroEntity, CommandSealData data) {

        Iterator<Map.Entry<CommandSealSkillType, Integer>> iterator = data.getActiveSkills().entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<CommandSealSkillType, Integer> entry = iterator.next();

            CommandSealSkillType commandSealSkillType = entry.getKey();

            ICommandSealSkill commandSealSkill = commandSealSkillType.getSkill();

            int remainingTicks = entry.getValue() - 1;

            if (remainingTicks <= 0) {

                commandSealSkill.onExpire(player, heroEntity);

                iterator.remove();

                continue;
            }

            entry.setValue(remainingTicks);

            commandSealSkill.tick(player, heroEntity, remainingTicks);
        }
    }

    public static void tickCooldownSkills(Player player, CommandSealData data) {

        Iterator<Map.Entry<CommandSealSkillType, Integer>> iterator =
                data.getCooldownSkills().entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<CommandSealSkillType, Integer> entry =
                    iterator.next();

            CommandSealSkillType commandSealSkillType =
                    entry.getKey();

            ICommandSealSkill commandSealSkill =
                    commandSealSkillType.getSkill();

            int remainingTicks = entry.getValue() - 1;

            if (remainingTicks <= 0) {

                commandSealSkill.onCooldownExpire(player);

                iterator.remove();

                continue;
            }

            entry.setValue(remainingTicks);
        }
    }
}
