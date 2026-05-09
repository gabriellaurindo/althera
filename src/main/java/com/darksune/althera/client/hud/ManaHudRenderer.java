package com.darksune.althera.client.hud;

import com.darksune.althera.Althera;
import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.ManaData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class ManaHudRenderer {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        final ManaData manaData = player.getData(AltheraAttachments.MANA.get());

        int mana = manaData.getMana();
        int manaMax = manaData.getMaxMana();

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int barWidth = 182;
        int barHeight = 5;

        int x = (width - barWidth) / 2;
        int y = height - 48;

        // evita divisão por zero (importante)
        float ratio = manaMax > 0 ? (float) mana / manaMax : 0;
        int filled = (int) (barWidth * ratio);

        GuiGraphics gui = event.getGuiGraphics();

        // fundo
        gui.fill(x, y, x + barWidth, y + barHeight, 0xFF000000);

        // barra
        gui.fill(x, y, x + filled, y + barHeight, 0xFF00BFFF);

        // texto
        gui.drawString(mc.font, mana + "/" + manaMax, x, y - 10, 0xFFFFFF);
    }
}