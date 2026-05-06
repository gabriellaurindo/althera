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
import net.minecraft.world.entity.LivingEntity;

public class HeroScreen extends Screen {

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    private LivingEntity previewEntity;
    private float tickAccumulator = 0;

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

        tickAccumulator += partialTick;
        while (tickAccumulator >= 1.0F) {
            entity.tickCount++;
            tickAccumulator -= 1.0F;
        }

        final HeroData heroData = HeroData.get(mc.player);

        // Background overlay
        gui.fill(0, 0, this.width, this.height, 0x55000000);

        int x = (this.width - GUI_WIDTH) / 2;
        int y = (this.height - GUI_HEIGHT) / 2;

        // ===== PANEL (DEPTH) =====
        gui.fill(x - 2, y - 2, x + GUI_WIDTH + 2, y + GUI_HEIGHT + 2, 0x88000000);
        gui.fill(x, y, x + GUI_WIDTH, y + GUI_HEIGHT, 0xFF1E1E1E);
        gui.fill(x + 2, y + 2, x + GUI_WIDTH - 2, y + GUI_HEIGHT - 2, 0xFF2A2A2A);

        // ===== TITLE =====
        String title = "STATUS";
        gui.drawString(this.font,
                title,
                this.width / 2 - this.font.width(title) / 2,
                y + 8,
                0xFFFFFF
        );

        gui.fill(this.width / 2 - 30, y + 18, this.width / 2 + 30, y + 19, 0xFF666666);

        // ===== ENTITY (LEFT) =====
        int entityX = x + 45;
        int entityY = y + 185; // moved DOWN

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                gui,
                entityX - 25,
                entityY - 150, // less aggressive top offset
                entityX + 25,
                entityY,
                35,
                0.0F,
                mouseX,
                mouseY,
                entity
        );

        // ===== STATS (RIGHT) =====
        int textX = x + 90;
        int textY = y + 30;
        int statWidth = 70;

        // CLASS
        drawStat(gui,
                "Class",
                heroData.getHeroDefinition().getHeroClass().getDisplayName(),
                textX,
                textY,
                statWidth
        );

        // RANK
        drawStat(gui,
                "Rank",
                heroData.getHeroDefinition().getRank().getDisplayName(),
                textX,
                textY + 12,
                statWidth
        );

        // Separator
        drawSeparator(gui, x, textY + 22);

        // LEVEL
        drawStat(gui,
                "Level",
                String.valueOf(heroData.getLevel()),
                textX,
                textY + 30,
                statWidth
        );

        // XP BAR
        long currentXp = heroData.getXp();
        long requiredXp = HeroProgressionSystem.getXpToNextLevel(heroData);
        float xpProgress = (float) currentXp / (float) requiredXp;

        drawBar(gui,
                textX,
                textY + 45,
                80,
                6,
                xpProgress,
                0xFF00FF88
        );

        gui.drawString(this.font,
                currentXp + "/" + requiredXp,
                textX,
                textY + 55,
                0xAAAAAA
        );

        // HEALTH BAR
        float hpPercent = (float) ((float) heroData.getHealth() / HeroStatsSystem.getMaxHealth(heroData));

        drawBar(gui,
                textX,
                textY + 70,
                80,
                6,
                hpPercent,
                0xFFFF5555
        );

        gui.drawString(this.font,
                (int) heroData.getHealth() + "/" + (int) HeroStatsSystem.getMaxHealth(heroData),
                textX,
                textY + 80,
                0xAAAAAA
        );

        // DAMAGE
        drawStat(gui,
                "Damage",
                String.format("%.1f", HeroStatsSystem.getAttack(heroData)),
                textX,
                textY + 95,
                statWidth
        );

//        // ===== SAVES =====
//        int max = HeroStatsSystem.getMaxInterventions();
//        int used = heroData.getInterventions();
//        int remaining = Math.max(0, max - used);
//
//        int startX = x + GUI_WIDTH - 70; // move left to fit horizontal layout
//        int yPos = y + 140;
//
//        // Label
//        gui.drawString(this.font, "Saves", startX, yPos - 12, 0xAAAAAA);
//
//        int size = 6;
//        int spacing = 4;
//
//        for (int i = 0; i < max; i++) {
//
//            int xPos = startX + i * (size + spacing);
//
//            boolean active = i < remaining;
//
//            int color = active ? 0xFF00FF88 : 0xFF555555;
//
//            // simple square (clean)
//            gui.fill(xPos, yPos, xPos + size, yPos + size, color);
//        }



        super.render(gui, mouseX, mouseY, partialTick);
    }

    // ===== HELPERS =====

    private void drawStat(GuiGraphics gui, String label, String value, int x, int y, int width) {
        gui.drawString(this.font, label, x, y, 0xAAAAAA);

        int valueWidth = this.font.width(value);
        gui.drawString(this.font, value, x + width - valueWidth, y, 0xFFFFFF);
    }

    private void drawBar(GuiGraphics gui, int x, int y, int width, int height, float progress, int color) {
        progress = Math.max(0, Math.min(1, progress));

        gui.fill(x, y, x + width, y + height, 0xFF333333);
        gui.fill(x, y, x + (int)(width * progress), y + height, color);
    }

    private void drawSeparator(GuiGraphics gui, int x, int y) {
        gui.fill(x + 8, y, x + GUI_WIDTH - 8, y + 1, 0xFF444444);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        // no blur
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