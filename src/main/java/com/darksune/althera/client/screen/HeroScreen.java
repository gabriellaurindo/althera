package com.darksune.althera.client.screen;

import com.darksune.althera.Althera;
import com.darksune.althera.client.keybind.AltheraKeybinds;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroProgressionSystem;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.network.packet.ToggleHeroSettingPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class HeroScreen extends Screen {

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    private static final ResourceLocation GEAR_ICON =
            ResourceLocation.fromNamespaceAndPath(
                    Althera.MOD_ID,
                    "textures/gui/gear.png"
            );

    private HeroEntity previewEntity;

    private float tickAccumulator = 0;

    private Tab currentTab = Tab.STATUS;

    private enum Tab {
        STATUS,
        SETTINGS
    }

    public HeroScreen() {
        super(Component.literal("Status"));
    }

    @Override
    protected void init() {
        super.init();

        final Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) {
            return;
        }

        final HeroData heroData = HeroData.get(mc.player);

        HeroEntity entity = AltheraEntities.HERO.get().create(mc.level);

        if (entity != null && heroData.getHeroDefinition() != null) {
            entity.setHeroId(heroData.getHeroDefinition().getId());
        }

        this.previewEntity = entity;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {

        final Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            return;
        }

        final HeroData heroData =
                HeroData.get(mc.player);

        // =====================================
        // SYNC PREVIEW ENTITY
        // =====================================

        if (previewEntity != null
                && heroData.getHeroDefinition() != null) {

            ResourceLocation current =
                    previewEntity.getHeroId();

            ResourceLocation expected =
                    heroData.getHeroDefinition().getId();

            if (current == null
                    || !expected.equals(current)) {

                previewEntity.setHeroId(expected);
            }
        }

        // ===== BACKGROUND =====
        gui.fill(0, 0, this.width, this.height, 0x55000000);

        int x = (this.width - GUI_WIDTH) / 2;
        int y = (this.height - GUI_HEIGHT) / 2;

        // ===== PANEL =====
        gui.fill(x - 2, y - 2, x + GUI_WIDTH + 2, y + GUI_HEIGHT + 2, 0x88000000);
        gui.fill(x, y, x + GUI_WIDTH, y + GUI_HEIGHT, 0xFF1E1E1E);
        gui.fill(x + 2, y + 2, x + GUI_WIDTH - 2, y + GUI_HEIGHT - 2, 0xFF2A2A2A);

        // ===== TITLE =====
        String title = currentTab == Tab.STATUS
                ? "STATUS"
                : "SETTINGS";

        gui.drawString(
                this.font,
                title,
                this.width / 2 - this.font.width(title) / 2,
                y + 8,
                0xFFFFFF
        );

        gui.fill(
                this.width / 2 - 30,
                y + 18,
                this.width / 2 + 30,
                y + 19,
                0xFF666666
        );

        // ===== GEAR ICON =====

        int gearX = x + GUI_WIDTH - 20;
        int gearY = y + 6;

        boolean hovered =
                mouseX >= gearX &&
                        mouseX <= gearX + 16 &&
                        mouseY >= gearY &&
                        mouseY <= gearY + 16;

        // Hover effect
        if (hovered) {
            gui.fill(
                    gearX - 1,
                    gearY - 1,
                    gearX + 17,
                    gearY + 17,
                    0x33FFFFFF
            );
        }

        gui.blit(
                GEAR_ICON,
                gearX,
                gearY,
                0,
                0,
                16,
                16,
                16,
                16
        );

        // ===== TAB CONTENT =====

        if (currentTab == Tab.STATUS) {
            renderStatusTab(gui, heroData, mouseX, mouseY, partialTick);
        }

        if (currentTab == Tab.SETTINGS) {
            renderSettingsTab(gui);
        }

        super.render(gui, mouseX, mouseY, partialTick);
    }

    // =========================================================
    // STATUS TAB
    // =========================================================

    private void renderStatusTab(GuiGraphics gui, HeroData heroData, int mouseX, int mouseY, float partialTick) {

        final HeroEntity entity = this.previewEntity;

        if (entity == null || heroData.getHeroDefinition() == null) {
            return;
        }

        tickAccumulator += partialTick;

        while (tickAccumulator >= 1.0F) {
            entity.tickCount++;
            tickAccumulator -= 1.0F;
        }

        int x = (this.width - GUI_WIDTH) / 2;
        int y = (this.height - GUI_HEIGHT) / 2;

        // ===== HERO NAME =====

        String heroName = heroData.getHeroDefinition().getName();

        int nameCenterX = x + 45;
        int nameY = y + 30;

        gui.drawString(
                this.font,
                "Name:",
                nameCenterX - this.font.width("Name:") / 2,
                nameY,
                0xAAAAAA
        );

        gui.drawString(
                this.font,
                heroName,
                nameCenterX - this.font.width(heroName) / 2,
                nameY + 12,
                0xFFFFFF
        );

        // ===== ENTITY =====

        int entityX = x + 45;
        int entityY = y + 185;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                gui,
                entityX - 25,
                entityY - 150,
                entityX + 25,
                entityY,
                35,
                0.0F,
                mouseX,
                mouseY,
                entity
        );

        // ===== STATS =====

        int textX = x + 90;
        int textY = y + 30;
        int statWidth = 70;

        // CLASS
        drawStat(
                gui,
                "Class",
                heroData.getHeroDefinition().getHeroClass().getDisplayName(),
                textX,
                textY,
                statWidth
        );

        // RANK
        drawStat(
                gui,
                "Rank",
                heroData.getHeroDefinition().getRank().getDisplayName(),
                textX,
                textY + 12,
                statWidth
        );

        // Separator
        drawSeparator(gui, x, textY + 22);

        // LEVEL
        drawStat(
                gui,
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

        drawBar(
                gui,
                textX,
                textY + 45,
                80,
                6,
                xpProgress,
                0xFF00FF88
        );

        gui.drawString(
                this.font,
                currentXp + "/" + requiredXp,
                textX,
                textY + 55,
                0xAAAAAA
        );

        // HEALTH BAR

        float hpPercent =
                (float) ((float) heroData.getHealth()
                                        / HeroStatsSystem.getMaxHealth(heroData));

        drawBar(
                gui,
                textX,
                textY + 70,
                80,
                6,
                hpPercent,
                0xFFFF5555
        );

        gui.drawString(
                this.font,
                (int) heroData.getHealth() + "/" +
                        (int) HeroStatsSystem.getMaxHealth(heroData),
                textX,
                textY + 80,
                0xAAAAAA
        );

        // DAMAGE

        drawStat(
                gui,
                "Damage",
                String.format("%.1f", HeroStatsSystem.getAttack(heroData)),
                textX,
                textY + 95,
                statWidth
        );
    }

    // =========================================================
    // SETTINGS TAB
    // =========================================================

    private void renderSettingsTab(GuiGraphics gui) {

        final Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            return;
        }

        HeroData heroData = HeroData.get(mc.player);

        int x = (this.width - GUI_WIDTH) / 2;
        int y = (this.height - GUI_HEIGHT) / 2;

        int startX = x + 16;
        int startY = y + 38;

        gui.drawString(
                this.font,
                "Settings",
                startX,
                startY,
                0xFFFFFF
        );

        // =====================================
        // HIDE HUD
        // =====================================

        int checkboxSize = 10;

        int hideHudY = startY + 24;

        // box
        gui.fill(
                startX,
                hideHudY,
                startX + checkboxSize,
                hideHudY + checkboxSize,
                0xFF444444
        );

        // checked
        if (heroData.isHiddenHud()) {

            gui.fill(
                    startX + 2,
                    hideHudY + 2,
                    startX + checkboxSize - 2,
                    hideHudY + checkboxSize - 2,
                    0xFF00FF88
            );
        }

        gui.drawString(
                this.font,
                "Hide HUD",
                startX + 16,
                hideHudY + 1,
                0xFFFFFF
        );

        // =====================================
        // DISABLE SAVES
        // =====================================

        int savesY = hideHudY + 18;

        gui.fill(
                startX,
                savesY,
                startX + checkboxSize,
                savesY + checkboxSize,
                0xFF444444
        );

        if (heroData.isSaveDisabled()) {

            gui.fill(
                    startX + 2,
                    savesY + 2,
                    startX + checkboxSize - 2,
                    savesY + checkboxSize - 2,
                    0xFFFF5555
            );
        }

        gui.drawString(
                this.font,
                "Disable Saves",
                startX + 16,
                savesY + 1,
                0xFFFFFF
        );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private void drawStat(GuiGraphics gui,
                          String label,
                          String value,
                          int x,
                          int y,
                          int width) {

        gui.drawString(this.font, label, x, y, 0xAAAAAA);

        int valueWidth = this.font.width(value);

        gui.drawString(
                this.font,
                value,
                x + width - valueWidth,
                y,
                0xFFFFFF
        );
    }

    private void drawBar(GuiGraphics gui,
                         int x,
                         int y,
                         int width,
                         int height,
                         float progress,
                         int color) {

        progress = Math.max(0, Math.min(1, progress));

        gui.fill(x, y, x + width, y + height, 0xFF333333);

        gui.fill(
                x,
                y,
                x + (int) (width * progress),
                y + height,
                color
        );
    }

    private void drawSeparator(GuiGraphics gui, int x, int y) {

        gui.fill(
                x + 8,
                y,
                x + GUI_WIDTH - 8,
                y + 1,
                0xFF444444
        );
    }

    // =========================================================
    // MOUSE
    // =========================================================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        final Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            return false;
        }

        HeroData heroData = HeroData.get(mc.player);

        int x = (this.width - GUI_WIDTH) / 2;
        int y = (this.height - GUI_HEIGHT) / 2;

        // =====================================
        // GEAR BUTTON
        // =====================================

        int gearX = x + GUI_WIDTH - 20;
        int gearY = y + 6;

        if (mouseX >= gearX &&
                mouseX <= gearX + 16 &&
                mouseY >= gearY &&
                mouseY <= gearY + 16) {

            currentTab = currentTab == Tab.STATUS
                    ? Tab.SETTINGS
                    : Tab.STATUS;

            return true;
        }

        // =====================================
        // SETTINGS CHECKBOXES
        // =====================================

        if (currentTab == Tab.SETTINGS) {

            int startX = x + 16;
            int startY = y + 38;

            int checkboxSize = 10;

            // ---------------------------------
            // HIDE HUD
            // ---------------------------------

            int hideHudY = startY + 24;

            if (mouseX >= startX &&
                    mouseX <= startX + checkboxSize &&
                    mouseY >= hideHudY &&
                    mouseY <= hideHudY + checkboxSize) {

                PacketDistributor.sendToServer(
                        new ToggleHeroSettingPacket(
                                ToggleHeroSettingPacket.Setting.HIDDEN_HUD
                        )
                );
                return true;
            }

            // ---------------------------------
            // DISABLE SAVES
            // ---------------------------------

            int savesY = hideHudY + 18;

            if (mouseX >= startX &&
                    mouseX <= startX + checkboxSize &&
                    mouseY >= savesY &&
                    mouseY <= savesY + checkboxSize) {

                PacketDistributor.sendToServer(
                        new ToggleHeroSettingPacket(
                                ToggleHeroSettingPacket.Setting.SAVE_DISABLED
                        )
                );
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics gui,
                                 int mouseX,
                                 int mouseY,
                                 float partialTick) {
        // no blur
    }

    @Override
    public boolean keyPressed(int keyCode,
                              int scanCode,
                              int modifiers) {

        if (AltheraKeybinds.HERO_SCREEN.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}