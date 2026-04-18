package com.darksune.althera.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HeroScreen extends Screen {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/demo_background.png");

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    public HeroScreen() {
        super(Component.literal("Status"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {

        // fundo leve sem blur
        gui.fill(0, 0, this.width, this.height, 0x55000000);

        int x = (this.width - GUI_WIDTH) / 2;
        int y = (this.height - GUI_HEIGHT) / 2;

        // painel
        gui.fill(x, y, x + GUI_WIDTH, y + GUI_HEIGHT, 0xFF2B2B2B);
        gui.fill(x + 2, y + 2, x + GUI_WIDTH - 2, y + GUI_HEIGHT - 2, 0xFF3C3C3C);

        String title = "Status";
        gui.drawString(this.font,
                title,
                this.width / 2 - this.font.width(title) / 2,
                y + 6,
                0xFFFFFF
        );

        super.render(gui, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        // bloqueia blur
    }
}