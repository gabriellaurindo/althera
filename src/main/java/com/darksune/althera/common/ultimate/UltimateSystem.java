package com.darksune.althera.common.ultimate;

import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.skill.SkillEndReason;
import com.darksune.althera.common.skill.SkillTimers;
import com.darksune.althera.common.system.HeroSummonSystem;
import com.darksune.althera.common.ultimate.effect.UltimateContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;


public class UltimateSystem {

    //todo block summon/dismiss while an ultimate is active
    public static void activateUltimate(Player player) {

        HeroEntity heroEntity = HeroSummonSystem.getSummon(player);

        if (heroEntity == null) {
            player.sendSystemMessage(Component.literal("Summon your hero to use its ultimate."));
            return;
        }

        if (heroEntity.isDisguised()) {
            player.sendSystemMessage(Component.literal("§eYour hero can't use its ultimate while disguised."));
            return;
        }

        HeroDefinition heroDefinition = heroEntity.getHeroDefinition();

        UltimateDefinition ultimate = heroDefinition == null ? null : UltimateRegistry.get(heroDefinition.getUltimate());

        if (ultimate == null) {
            player.sendSystemMessage(Component.literal("Your hero has no ultimate."));
            return;
        }

        ResourceLocation ultimateId = heroDefinition.getUltimate();

        UltimateData ultimateData = UltimateData.get(player);

        ManaData manaData = ManaData.get(player);

        if (!manaData.hasEnoughMana(player, ultimate.manaCost())) {
            player.sendSystemMessage(Component.literal("Not enough mana"));
            return;
        }

        if (ultimateData.wasUsedToday()) {
            player.sendSystemMessage(Component.literal("§eUltimate already used today. It recharges at the start of a new day."));
            return;
        }

        if (ultimateData.isSkillOnCooldown(ultimateId)) {
            player.sendSystemMessage(Component.literal("Ultimate on cooldown"));
            return;
        }

        activateUltimate(player, heroEntity, ultimateData, ultimateId, ultimate, manaData);
    }

    private static void activateUltimate(Player player, HeroEntity heroEntity, UltimateData data, ResourceLocation ultimateId,
                                         UltimateDefinition ultimate, ManaData manaData) {

        manaData.consumeMana(player, ultimate.manaCost());

        data.activateSkill(ultimateId, ultimate.channelTicks());

        data.markUsedToday();

        data.startSkillCooldown(ultimateId, ultimate.cooldownTicks());

        ultimate.start(UltimateContext.of(player, heroEntity));
    }

    public static void tickActiveSkills(Player player, HeroEntity heroEntity, UltimateData data) {
        SkillTimers.tick(
                data.getActiveSkills(),
                (ultimateId, remainingTicks) -> {
                    UltimateDefinition ultimate = UltimateRegistry.get(ultimateId);
                    if (ultimate != null) {
                        ultimate.tick(UltimateContext.of(player, heroEntity));
                    }
                    SkillTimers.markDirtyOnSyncTick(data, remainingTicks);
                },
                ultimateId -> endUltimate(player, heroEntity, data, ultimateId, SkillEndReason.EXPIRED)
        );
    }

    public static void tickCooldownSkills(Player player, UltimateData data) {
        SkillTimers.tick(
                data.getCooldownSkills(),
                (ultimateId, remainingTicks) -> SkillTimers.markDirtyOnSyncTick(data, remainingTicks),
                ultimateId -> data.markDirty()
        );
    }

    /**
     * Ends every active skill with the given reason; each skill decides the consequence in onEnd.
     */
    public static void endActiveSkills(Player player, @Nullable HeroEntity heroEntity, SkillEndReason reason) {
        UltimateData data = UltimateData.get(player);
        SkillTimers.endAll(
                data.getActiveSkills(),
                ultimateId -> endUltimate(player, heroEntity, data, ultimateId, reason)
        );
    }

    private static void endUltimate(Player player, @Nullable HeroEntity heroEntity, UltimateData data,
                                    ResourceLocation ultimateId, SkillEndReason reason) {

        data.markDirty();

        UltimateDefinition ultimate = UltimateRegistry.get(ultimateId);

        if (reason == SkillEndReason.EXPIRED && heroEntity != null && ultimate != null) {
            ultimate.end(UltimateContext.of(player, heroEntity));
        }
    }
}
