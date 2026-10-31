package com.darksune.althera.client.hud;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.commandseal.CommandSealData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.darksune.althera.common.commandseal.skill.ICommandSealSkill;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.skill.SkillLimits;
import com.darksune.althera.common.ultimate.UltimateData;
import com.darksune.althera.common.ultimate.UltimateDefinition;
import com.darksune.althera.common.ultimate.UltimateRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record SkillSlotState(int timerTicks, int totalTicks, boolean active, @Nullable String blocker,
                             boolean usedToday) {

    public boolean available() {
        return blocker == null;
    }

    public boolean onCooldown() {
        return !active && timerTicks > 0;
    }

    public boolean ready() {
        return available() && !onCooldown();
    }

    public static SkillSlotState seal(Player player, CommandSealSkillType type) {

        HeroData heroData = HeroData.get(player);
        CommandSealData data = CommandSealData.get(player);
        ICommandSealSkill skill = type.getSkill();

        return of(data.getActiveSkills().get(type), skill.getDurationTicks(),
                data.getCooldownSkills().get(type), skill.getCooldownTicks(),
                sealBlocker(type, skill, heroData, data, mana(player), SkillLimits.isUnlimited(player)), false);
    }

    public static SkillSlotState ultimate(Player player) {

        HeroData heroData = HeroData.get(player);
        UltimateData data = UltimateData.get(player);
        boolean usedToday = data.wasUsedToday() && !SkillLimits.isUnlimited(player);
        UltimateDefinition ultimate = ultimateOf(player);

        if (ultimate == null) {
            return of(null, 0, null, 0, heroData.getHeroDefinition() == null ? "No hero" : "No ultimate", usedToday);
        }

        ResourceLocation ultimateId = heroData.getHeroDefinition().getUltimate();

        return of(data.getActiveSkills().get(ultimateId), ultimate.channelTicks(),
                data.getCooldownSkills().get(ultimateId), ultimate.cooldownTicks(),
                ultimateBlocker(ultimate, heroData, usedToday, mana(player)), usedToday);
    }

    @Nullable
    private static UltimateDefinition ultimateOf(Player player) {
        HeroDefinition hero = HeroData.get(player).getHeroDefinition();
        return hero == null ? null : UltimateRegistry.get(hero.getUltimate());
    }

    private static int mana(Player player) {
        return ManaData.hasInfiniteMana(player) ? Integer.MAX_VALUE : ManaData.get(player).getMana();
    }

    @Nullable
    private static String sealBlocker(CommandSealSkillType type, ICommandSealSkill skill, HeroData heroData,
                                      CommandSealData data, int mana, boolean unlimited) {

        if (type.requiresHeroEntity() && !heroData.isSummoned()) return "Summon your hero";
        if (!type.requiresHeroEntity() && heroData.isSummoned()) return "Hero must be away";
        if (!unlimited && !data.hasCharges()) return "No charges";
        if (mana < skill.getManaCost()) return "Not enough mana";

        return skill.unavailableReason(heroData);
    }

    @Nullable
    private static String ultimateBlocker(UltimateDefinition ultimate, HeroData heroData, boolean usedToday, int mana) {

        if (usedToday) return "Used today";
        if (heroData.isDisguised()) return "Disguised";
        if (!heroData.isSummoned()) return "Summon your hero";
        if (mana < ultimate.manaCost()) return "Not enough mana";

        return null;
    }

    private static SkillSlotState of(@Nullable Integer activeTicks, int durationTicks,
                                     @Nullable Integer cooldownTicks, int cooldownTotalTicks,
                                     @Nullable String blocker, boolean usedToday) {

        if (activeTicks != null) {
            return new SkillSlotState(activeTicks, durationTicks, true, blocker, usedToday);
        }

        if (cooldownTicks != null) {
            return new SkillSlotState(cooldownTicks, cooldownTotalTicks, false, blocker, usedToday);
        }

        return new SkillSlotState(0, 0, false, blocker, usedToday);
    }
}
