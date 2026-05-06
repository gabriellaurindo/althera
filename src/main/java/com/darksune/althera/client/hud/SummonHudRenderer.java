package com.darksune.althera.client.hud;

import com.darksune.althera.Althera;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.system.HeroStatsSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = Althera.MOD_ID, value = net.neoforged.api.distmarker.Dist.CLIENT)
public class SummonHudRenderer {

    private static final ResourceLocation HUD_EMPTY =
            ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "textures/gui/hud_empty.png");

    private static final ResourceLocation HUD_HP =
            ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "textures/gui/hud_hp.png");

    private static final ResourceLocation HUD_SAVES =
            ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "textures/gui/hud_saves.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {

        var gui = event.getGuiGraphics();
        var mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) return;

        final HeroData heroData = HeroData.get(mc.player);

        if (heroData.getHeroDefinition() == null) {
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight - 84;

        int width = 96;
        int height = 64;

        int drawX = centerX - width / 2;
        int drawY = centerY - height / 2;

        // =========================
        // BASE
        // =========================
        gui.blit(HUD_EMPTY, drawX, drawY, 0, 0, width, height, width, height);

        // =========================
        // HP
        // =========================
        float hpPercent = HeroStatsSystem.getMaxHealth(heroData) > 0
                ? (float) (heroData.getHealth() / HeroStatsSystem.getMaxHealth(heroData))
                : 0;

        int visibleHeight = Math.max(1, Math.round(height * hpPercent));
        int yOffset = height - visibleHeight;

        gui.blit(
                HUD_HP,
                drawX,
                drawY + yOffset,
                0, yOffset,
                width, visibleHeight,
                width, height
        );

        // =========================
        // SAVES (igual HP)
        // =========================
        int max = HeroStatsSystem.getMaxInterventions();
        int remaining = Math.max(0, max - heroData.getInterventions());

        float savePercent = max > 0 ? (float) remaining / max : 0;

        // mesma lógica do HP
        int saveVisibleHeight = (int)(height * savePercent);
        int saveYOffset = height - saveVisibleHeight;

        if (saveVisibleHeight > 0) {
            gui.blit(
                    HUD_SAVES,
                    drawX,
                    drawY + saveYOffset,   // move pra baixo
                    0, saveYOffset,        // UV ajustado
                    width, saveVisibleHeight,
                    width, height
            );
        }
    }
}