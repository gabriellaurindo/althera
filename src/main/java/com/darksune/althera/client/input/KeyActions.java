package com.darksune.althera.client.input;

import com.darksune.althera.client.screen.HeroScreen;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.darksune.althera.network.packet.SummonPacket;
import com.darksune.althera.network.packet.UseCommandSealSkillPacket;
import net.minecraft.client.Minecraft;

public class KeyActions {

    public static void handleSummon() {
        Minecraft.getInstance().getConnection().send(new SummonPacket());
    }

    public static void handleHero() {
        Minecraft mc = Minecraft.getInstance();
        HeroData heroData = HeroData.get(mc.player);
        if (mc.screen == null && heroData.getHeroDefinition() != null) {
            mc.setScreen(new HeroScreen());
        } else {
            mc.setScreen(null);
        }
    }

    public static void handleCommandSealSkill(final CommandSealSkillType skillType) {
        Minecraft.getInstance().getConnection().send(new UseCommandSealSkillPacket(skillType));
    }
}
