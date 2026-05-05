package com.darksune.althera.client.action;

import com.darksune.althera.client.screen.HeroScreen;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.network.SummonPayload;
import net.minecraft.client.Minecraft;

public class ClientActions {

    public static void handleSummon() {
        Minecraft.getInstance().getConnection().send(new SummonPayload());
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
}