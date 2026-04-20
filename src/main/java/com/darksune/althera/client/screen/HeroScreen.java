package com.darksune.althera.client.screen;

import com.darksune.althera.client.keybind.AltheraKeybinds;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.system.HeroProgressionSystem;
import com.darksune.althera.common.system.HeroStatsSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class HeroScreen extends Screen {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/demo_background.png");

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    private LivingEntity previewEntity;

    public HeroScreen() {
        super(Component.literal("Status"));
    }

    @Override
    protected void init() {
        super.init();
        final Minecraft mc = Minecraft.getInstance();
        this.previewEntity = AltheraEntities.HERO.get().create(mc.level);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        final Minecraft mc = Minecraft.getInstance();
        final LivingEntity entity = this.previewEntity;
        entity.tickCount++;

        final HeroData heroData = HeroData.get(mc.player);

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

        int entityX = x + GUI_WIDTH / 2;
        int entityY = y + GUI_HEIGHT / 2;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                gui,
                entityX - 25,
                entityY - 50,
                entityX + 25,
                entityY + 50,
                40,
                0.0F,
                mouseX,
                mouseY,
                entity
        );

        int textX = x + 10;     // margem esquerda do painel
        int textY = y + 30;     // abaixo do título

        gui.drawString(this.font,
                "Health",
                textX,
                textY,
                0xFFD700 // dourado
        );

        gui.drawString(this.font,
                (int) heroData.getHealth() + " / " + (int) HeroStatsSystem.getMaxHealth(heroData.getLevel()),
                textX,
                textY + 12,
                0xFFFFFF
        );

        // LEVEL
        gui.drawString(this.font,
                "Level",
                textX,
                textY + 24,
                0x00FFAA
        );

        gui.drawString(this.font,
                String.valueOf(heroData.getLevel()),
                textX,
                textY + 36,
                0xFFFFFF
        );

        // XP
        long currentXp = heroData.getXp();
        long requiredXp = HeroProgressionSystem.getXpToNextLevel(heroData);

        gui.drawString(this.font,
                "XP",
                textX,
                textY + 48,
                0x00AAFF
        );

        gui.drawString(this.font,
                currentXp + " / " + requiredXp,
                textX,
                textY + 60,
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (AltheraKeybinds.HERO_SCREEN.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}