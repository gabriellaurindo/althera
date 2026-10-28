package com.darksune.althera.client.input;

import com.darksune.althera.Althera;
import com.darksune.althera.client.screen.HeroScreen;
import com.darksune.althera.client.screen.HeroSelectionOpener;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.darksune.althera.network.packet.DisguisePacket;
import com.darksune.althera.network.packet.SummonPacket;
import com.darksune.althera.network.packet.UseCommandSealSkillPacket;
import com.darksune.althera.network.packet.UseUltimateSkillPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class KeyActions {

    public static void handleSummon() {
        Minecraft.getInstance().getConnection().send(new SummonPacket());
    }

    public static void handleDisguise() {
        Minecraft.getInstance().getConnection().send(new DisguisePacket());
    }

    public static void handleHero() {
        Minecraft mc = Minecraft.getInstance();
        HeroData heroData = HeroData.get(mc.player);
        if (mc.screen == null && heroData.getHeroDefinition() != null) {
            mc.setScreen(new HeroScreen());
        } else if (mc.screen == null) {
            HeroSelectionOpener.open();
        } else {
            mc.setScreen(null);
        }
    }

    public static void handleConfig() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        ModList.get().getModContainerById(Althera.MOD_ID)
                .ifPresent(container -> mc.setScreen(new ConfigurationScreen(container, null)));
    }

    public static void handleCommandSealSkill(final CommandSealSkillType skillType) {
        Minecraft.getInstance().getConnection().send(new UseCommandSealSkillPacket(skillType));
    }

    public static void handleUltimateSkill() {
        Minecraft.getInstance().getConnection().send(new UseUltimateSkillPacket());
    }
}
