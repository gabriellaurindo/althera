package com.darksune.althera.client.screen;

import com.darksune.althera.Althera;
import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.system.HeroProgressionSystem;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.ultimate.UltimateData;
import com.darksune.althera.common.ultimate.UltimateDefinition;
import com.darksune.althera.common.ultimate.UltimateRegistry;
import com.darksune.althera.network.packet.ToggleHeroSettingPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class HeroScreen extends Screen {

    private static final int GUI_WIDTH = 250;
    private static final int GUI_HEIGHT = 190;

    private static final int PREVIEW_X = 12;
    private static final int PREVIEW_Y = 32;
    private static final int PREVIEW_WIDTH = 100;
    private static final int PREVIEW_HEIGHT = 112;

    private static final int INFO_X = 126;
    private static final int INFO_WIDTH = GUI_WIDTH - INFO_X - 12;

    private static final int CHECKBOX_SIZE = 10;
    private static final int SETTING_ROW_HEIGHT = 44;

    private static final int BUTTON_HEIGHT = 16;
    private static final int CONTENT_X = 14;
    private static final int CONTENT_WIDTH = GUI_WIDTH - CONTENT_X * 2;

    private static final int COLOR_BORDER = 0xFF1E1E1E;
    private static final int COLOR_PANEL = 0xFF2A2A2A;
    private static final int COLOR_PREVIEW = 0xFF202020;
    private static final int COLOR_LABEL = 0xFFAAAAAA;
    private static final int COLOR_RANK = 0xFFFFAA00;
    private static final int COLOR_ACCENT = 0xFFE0B040;
    private static final int COLOR_XP = 0xFF00FF88;
    private static final int COLOR_HEALTH = 0xFFFF5555;
    private static final int COLOR_SEPARATOR = 0xFF444444;
    private static final int COLOR_BUTTON = 0xFF202020;
    private static final int COLOR_BUTTON_HOVER = 0xFF353535;
    private static final int COLOR_READY = 0xFF55FF55;
    private static final int COLOR_USED = 0xFFFF5555;

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
        SETTINGS,
        ULTIMATE
    }

    private enum SettingRow {
        HIDE_HUD("Hide HUD", "Hides the hero HUD and the skill slots.", 0xFF00FF88),
        DISABLE_INTERVENTIONS(
                "Disable interventions",
                "Your hero will not step in to absorb the damage you take.",
                0xFFFF5555
        );

        private final String label;
        private final String description;
        private final int color;

        SettingRow(String label, String description, int color) {
            this.label = label;
            this.description = description;
            this.color = color;
        }
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

        gui.fill(0, 0, this.width, this.height, 0x55000000);

        final int x = left();
        final int y = top();

        gui.fill(x - 2, y - 2, x + GUI_WIDTH + 2, y + GUI_HEIGHT + 2, COLOR_BORDER);
        gui.fill(x, y, x + GUI_WIDTH, y + GUI_HEIGHT, COLOR_PANEL);

        final String title = switch (currentTab) {
            case STATUS -> "STATUS";
            case SETTINGS -> "SETTINGS";
            case ULTIMATE -> "ULTIMATE";
        };

        gui.drawCenteredString(this.font, title, this.width / 2, y + 10, 0xFFFFFF);
        gui.fill(this.width / 2 - 30, y + 21, this.width / 2 + 30, y + 22, COLOR_SEPARATOR);

        renderGear(gui, mouseX, mouseY);
        renderClose(gui, mouseX, mouseY);

        if (currentTab == Tab.STATUS) {
            renderStatusTab(gui, heroData, mouseX, mouseY, partialTick);
        }

        if (currentTab == Tab.SETTINGS) {
            renderSettingsTab(gui, heroData, mouseX, mouseY);
        }

        if (currentTab == Tab.ULTIMATE) {
            renderUltimateTab(gui, mc, heroData, mouseX, mouseY);
        }

        super.render(gui, mouseX, mouseY, partialTick);
    }

    private void renderGear(GuiGraphics gui, int mouseX, int mouseY) {
        final int gearX = gearX();
        final int gearY = gearY();

        if (isOverGear(mouseX, mouseY) || currentTab == Tab.SETTINGS) {
            gui.fill(gearX - 2, gearY - 2, gearX + 18, gearY + 18, 0x33FFFFFF);
        }

        gui.blit(GEAR_ICON, gearX, gearY, 0, 0, 16, 16, 16, 16);
    }

    private void renderClose(GuiGraphics gui, int mouseX, int mouseY) {
        final int closeX = closeX();
        final int closeY = closeY();

        gui.fill(closeX - 2, closeY - 2, closeX + 14, closeY + 14, isOverClose(mouseX, mouseY) ? 0x88FF5555 : 0x33FFFFFF);
        gui.drawCenteredString(this.font, "x", closeX + 6, closeY + 2, 0xFFFFFF);
    }

    // =========================================================
    // STATUS TAB
    // =========================================================

    private void renderStatusTab(GuiGraphics gui, HeroData heroData, int mouseX, int mouseY, float partialTick) {

        final HeroEntity entity = this.previewEntity;
        final HeroDefinition definition = heroData.getHeroDefinition();

        if (entity == null || definition == null) {
            return;
        }

        tickAccumulator += partialTick;

        while (tickAccumulator >= 1.0F) {
            entity.tickCount++;
            tickAccumulator -= 1.0F;
        }

        final int x = left();
        final int y = top();

        final int previewLeft = x + PREVIEW_X;
        final int previewTop = y + PREVIEW_Y;

        gui.fill(previewLeft, previewTop, previewLeft + PREVIEW_WIDTH, previewTop + PREVIEW_HEIGHT, COLOR_PREVIEW);

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                gui,
                previewLeft,
                previewTop,
                previewLeft + PREVIEW_WIDTH,
                previewTop + PREVIEW_HEIGHT,
                Math.round(34 / entity.getModelScale()),
                0.0F,
                mouseX,
                mouseY,
                entity
        );

        final int previewCenterX = previewLeft + PREVIEW_WIDTH / 2;

        gui.drawCenteredString(this.font, definition.getName(), previewCenterX, previewTop + PREVIEW_HEIGHT + 8, 0xFFFFFF);
        gui.drawCenteredString(
                this.font,
                definition.getRank().getDisplayName() + " · " + definition.getHeroClass().getDisplayName(),
                previewCenterX,
                previewTop + PREVIEW_HEIGHT + 20,
                COLOR_RANK
        );

        final int infoX = x + INFO_X;
        int infoY = y + 32;

        drawStat(gui, "Level", String.valueOf(heroData.getLevel()), infoX, infoY, INFO_WIDTH, COLOR_ACCENT);
        infoY += 14;

        final long currentXp = heroData.getXp();
        final long requiredXp = HeroProgressionSystem.getXpToNextLevel(heroData);
        final boolean maxLevel = HeroProgressionSystem.isMaxLevel(heroData);

        drawBar(gui, infoX, infoY, INFO_WIDTH, 6,
                maxLevel ? 1 : requiredXp > 0 ? (float) currentXp / requiredXp : 0, COLOR_XP);
        infoY += 9;
        drawStat(gui, "XP", maxLevel ? "MAX" : currentXp + " / " + requiredXp, infoX, infoY, INFO_WIDTH, 0xFFFFFFFF);
        infoY += 20;

        final double maxHealth = HeroStatsSystem.getMaxHealth(heroData);

        drawStat(
                gui,
                "Health",
                (int) heroData.getHealth() + " / " + (int) maxHealth,
                infoX,
                infoY,
                INFO_WIDTH,
                0xFFFFFFFF
        );
        infoY += 12;
        drawBar(gui, infoX, infoY, INFO_WIDTH, 6, maxHealth > 0 ? (float) (heroData.getHealth() / maxHealth) : 0, COLOR_HEALTH);
        infoY += 16;

        gui.fill(infoX, infoY, infoX + INFO_WIDTH, infoY + 1, COLOR_SEPARATOR);
        infoY += 8;

        drawStat(
                gui,
                "Damage",
                String.format(Locale.ROOT, "%.1f", HeroStatsSystem.getAttack(heroData)),
                infoX,
                infoY,
                INFO_WIDTH,
                0xFFFFFFFF
        );
        infoY += 12;

        drawStat(
                gui,
                "Armor",
                String.format(Locale.ROOT, "%.1f", HeroStatsSystem.getArmor(heroData)),
                infoX,
                infoY,
                INFO_WIDTH,
                0xFFFFFFFF
        );
        infoY += 12;

        drawStat(
                gui,
                "Dodge",
                String.format(Locale.ROOT, "%.0f%%", HeroStatsSystem.getDodgeChance(heroData) * 100),
                infoX,
                infoY,
                INFO_WIDTH,
                0xFFFFFFFF
        );
        infoY += 12;

        final int interventionsLeft = HeroStatsSystem.getMaxInterventions() - heroData.getInterventions();

        drawStat(
                gui,
                "Interventions",
                interventionsLeft + " / " + HeroStatsSystem.getMaxInterventions(),
                infoX,
                infoY,
                INFO_WIDTH,
                0xFFFFFFFF
        );

        if (ultimate(heroData) != null) {
            drawButton(gui, "Ultimate  ›", ultimateButtonX(), ultimateButtonY(), INFO_WIDTH,
                    isOverUltimateButton(mouseX, mouseY));
        }
    }

    // =========================================================
    // ULTIMATE TAB
    // =========================================================

    private void renderUltimateTab(GuiGraphics gui, Minecraft mc, HeroData heroData, int mouseX, int mouseY) {

        final UltimateDefinition ultimate = ultimate(heroData);

        if (ultimate == null) {
            currentTab = Tab.STATUS;
            return;
        }

        final int x = left() + CONTENT_X;
        int y = top() + 32;

        gui.drawString(this.font, ultimate.name(), x, y, COLOR_ACCENT);
        final String key = "Key " + AltheraKeybinds.ULTIMATE_SKILL.getTranslatedKeyMessage().getString();
        gui.drawString(this.font, key, x + CONTENT_WIDTH - this.font.width(key), y, COLOR_LABEL);
        y += 14;

        gui.fill(x, y, x + CONTENT_WIDTH, y + 1, COLOR_SEPARATOR);
        y += 8;

        drawStat(gui, "Mana cost", String.valueOf(ultimate.manaCost()), x, y, CONTENT_WIDTH, 0xFFFFFFFF);
        y += 12;

        drawStat(gui, "Channel", formatSeconds(ultimate.channelTicks()), x, y, CONTENT_WIDTH, 0xFFFFFFFF);
        y += 12;

        final double damage = ultimate.damage(HeroStatsSystem.getAttack(heroData));
        if (damage > 0) {
            drawStat(gui, "Damage", formatDamage(ultimate, damage), x, y, CONTENT_WIDTH, 0xFFFFFFFF);
            y += 12;
        }

        final boolean usedToday = UltimateData.get(mc.player).wasUsedToday();
        drawStat(gui, "Once per day", usedToday ? "Used" : "Ready", x, y, CONTENT_WIDTH, usedToday ? COLOR_USED : COLOR_READY);
        y += 14;

        gui.fill(x, y, x + CONTENT_WIDTH, y + 1, COLOR_SEPARATOR);
        y += 8;

        final int bottom = backButtonY() - 4;

        for (FormattedCharSequence line : this.font.split(Component.literal(ultimate.description()), CONTENT_WIDTH)) {
            if (y + this.font.lineHeight > bottom) {
                break;
            }
            gui.drawString(this.font, line, x, y, 0xFFDDDDDD);
            y += 10;
        }

        drawButton(gui, "‹  Back", backButtonX(), backButtonY(), CONTENT_WIDTH, isOverBackButton(mouseX, mouseY));
    }

    @Nullable
    private static UltimateDefinition ultimate(HeroData heroData) {
        final HeroDefinition definition = heroData.getHeroDefinition();
        return definition == null ? null : UltimateRegistry.get(definition.getUltimate());
    }

    private static String formatDamage(UltimateDefinition ultimate, double damage) {
        final double flat = ultimate.damage(0);
        final double attackPercent = (ultimate.damage(1) - flat) * 100;

        if (attackPercent <= 0) {
            return String.format(Locale.ROOT, "%.1f", damage);
        }
        if (flat <= 0) {
            return String.format(Locale.ROOT, "%.0f%% ATK (%.1f)", attackPercent, damage);
        }
        return String.format(Locale.ROOT, "%.0f + %.0f%% ATK (%.1f)", flat, attackPercent, damage);
    }

    private static String formatSeconds(int ticks) {
        return ticks % 20 == 0
                ? ticks / 20 + " s"
                : String.format(Locale.ROOT, "%.1f s", ticks / 20.0);
    }

    // =========================================================
    // SETTINGS TAB
    // =========================================================

    private void renderSettingsTab(GuiGraphics gui, HeroData heroData, int mouseX, int mouseY) {

        final SettingRow[] rows = SettingRow.values();

        for (int i = 0; i < rows.length; i++) {
            final SettingRow row = rows[i];
            final int rowX = left() + 14;
            final int rowY = rowTop(i);
            final int rowWidth = GUI_WIDTH - 28;

            final boolean hovered = isOverRow(i, mouseX, mouseY);

            gui.fill(rowX, rowY, rowX + rowWidth, rowY + SETTING_ROW_HEIGHT - 4, hovered ? 0xFF353535 : COLOR_PREVIEW);

            final int boxX = rowX + 8;
            final int boxY = rowY + 6;

            gui.fill(boxX - 1, boxY - 1, boxX + CHECKBOX_SIZE + 1, boxY + CHECKBOX_SIZE + 1, COLOR_SEPARATOR);
            gui.fill(boxX, boxY, boxX + CHECKBOX_SIZE, boxY + CHECKBOX_SIZE, 0xFF111111);

            if (isEnabled(heroData, row)) {
                gui.fill(boxX + 2, boxY + 2, boxX + CHECKBOX_SIZE - 2, boxY + CHECKBOX_SIZE - 2, row.color);
            }

            gui.drawString(this.font, row.label, boxX + CHECKBOX_SIZE + 8, boxY + 1, 0xFFFFFF);

            final List<FormattedCharSequence> lines =
                    this.font.split(Component.literal(row.description), rowWidth - CHECKBOX_SIZE - 32);
            for (int line = 0; line < Math.min(lines.size(), 2); line++) {
                gui.drawString(this.font, lines.get(line), boxX + CHECKBOX_SIZE + 8, boxY + 13 + line * 10, COLOR_LABEL);
            }
        }

        gui.drawCenteredString(
                this.font,
                "Click the gear to go back",
                this.width / 2,
                top() + GUI_HEIGHT - 16,
                0x777777
        );
    }

    private boolean isEnabled(HeroData heroData, SettingRow row) {
        return switch (row) {
            case HIDE_HUD -> heroData.isHiddenHud();
            case DISABLE_INTERVENTIONS -> heroData.isSaveDisabled();
        };
    }

    private ToggleHeroSettingPacket.Setting packetSetting(SettingRow row) {
        return switch (row) {
            case HIDE_HUD -> ToggleHeroSettingPacket.Setting.HIDDEN_HUD;
            case DISABLE_INTERVENTIONS -> ToggleHeroSettingPacket.Setting.SAVE_DISABLED;
        };
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private void drawStat(GuiGraphics gui,
                          String label,
                          String value,
                          int x,
                          int y,
                          int width,
                          int valueColor) {

        gui.drawString(this.font, label, x, y, COLOR_LABEL);

        gui.drawString(
                this.font,
                value,
                x + width - this.font.width(value),
                y,
                valueColor
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

        gui.fill(x - 1, y - 1, x + width + 1, y + height + 1, COLOR_BORDER);
        gui.fill(x, y, x + width, y + height, 0xFF333333);

        gui.fill(
                x,
                y,
                x + (int) (width * progress),
                y + height,
                color
        );
    }

    private void drawButton(GuiGraphics gui, String label, int x, int y, int width, boolean hovered) {
        gui.fill(x - 1, y - 1, x + width + 1, y + BUTTON_HEIGHT + 1, COLOR_SEPARATOR);
        gui.fill(x, y, x + width, y + BUTTON_HEIGHT, hovered ? COLOR_BUTTON_HOVER : COLOR_BUTTON);
        gui.drawCenteredString(this.font, label, x + width / 2, y + (BUTTON_HEIGHT - 8) / 2, 0xFFFFFF);
    }

    private int ultimateButtonX() {
        return left() + INFO_X;
    }

    private int ultimateButtonY() {
        return top() + GUI_HEIGHT - BUTTON_HEIGHT - 12;
    }

    private int backButtonX() {
        return left() + CONTENT_X;
    }

    private int backButtonY() {
        return top() + GUI_HEIGHT - BUTTON_HEIGHT - 10;
    }

    private boolean isOverUltimateButton(double mouseX, double mouseY) {
        return mouseX >= ultimateButtonX() && mouseX <= ultimateButtonX() + INFO_WIDTH
                && mouseY >= ultimateButtonY() && mouseY <= ultimateButtonY() + BUTTON_HEIGHT;
    }

    private boolean isOverBackButton(double mouseX, double mouseY) {
        return mouseX >= backButtonX() && mouseX <= backButtonX() + CONTENT_WIDTH
                && mouseY >= backButtonY() && mouseY <= backButtonY() + BUTTON_HEIGHT;
    }

    private int left() {
        return (this.width - GUI_WIDTH) / 2;
    }

    private int top() {
        return (this.height - GUI_HEIGHT) / 2;
    }

    private int gearX() {
        return left() + 8;
    }

    private int gearY() {
        return top() + 6;
    }

    private int closeX() {
        return left() + GUI_WIDTH - 22;
    }

    private int closeY() {
        return top() + 6;
    }

    private boolean isOverClose(double mouseX, double mouseY) {
        return mouseX >= closeX() && mouseX <= closeX() + 12
                && mouseY >= closeY() && mouseY <= closeY() + 12;
    }

    private int rowTop(int index) {
        return top() + 34 + index * SETTING_ROW_HEIGHT;
    }

    private boolean isOverGear(double mouseX, double mouseY) {
        return mouseX >= gearX() && mouseX <= gearX() + 16
                && mouseY >= gearY() && mouseY <= gearY() + 16;
    }

    private boolean isOverRow(int index, double mouseX, double mouseY) {
        final int rowX = left() + 14;
        final int rowY = rowTop(index);

        return mouseX >= rowX && mouseX <= rowX + GUI_WIDTH - 28
                && mouseY >= rowY && mouseY <= rowY + SETTING_ROW_HEIGHT - 4;
    }

    // =========================================================
    // MOUSE
    // =========================================================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (isOverClose(mouseX, mouseY)) {
            this.onClose();
            return true;
        }

        if (isOverGear(mouseX, mouseY)) {
            currentTab = currentTab == Tab.SETTINGS
                    ? Tab.STATUS
                    : Tab.SETTINGS;

            return true;
        }

        final Minecraft mc = Minecraft.getInstance();

        if (currentTab == Tab.STATUS && mc.player != null && ultimate(HeroData.get(mc.player)) != null
                && isOverUltimateButton(mouseX, mouseY)) {
            currentTab = Tab.ULTIMATE;
            return true;
        }

        if (currentTab == Tab.ULTIMATE && isOverBackButton(mouseX, mouseY)) {
            currentTab = Tab.STATUS;
            return true;
        }

        if (currentTab == Tab.SETTINGS) {
            final SettingRow[] rows = SettingRow.values();

            for (int i = 0; i < rows.length; i++) {
                if (isOverRow(i, mouseX, mouseY)) {
                    PacketDistributor.sendToServer(new ToggleHeroSettingPacket(packetSetting(rows[i])));
                    return true;
                }
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
