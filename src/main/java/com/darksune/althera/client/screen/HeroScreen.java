package com.darksune.althera.client.screen;

import com.darksune.althera.Althera;
import com.darksune.althera.client.AltheraClientConfig;
import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.client.hud.HudIcons;
import com.darksune.althera.client.hud.SpellSlots;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellElement;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.darksune.althera.common.spell.SpellProficiency;
import com.darksune.althera.common.commandseal.CommandSealData;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroClass;
import com.darksune.althera.common.hero.HeroClassRegistry;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.skill.SkillLimits;
import com.darksune.althera.common.system.HeroProgressionSystem;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.ultimate.UltimateData;
import com.darksune.althera.common.ultimate.UltimateDefinition;
import com.darksune.althera.common.ultimate.UltimateRegistry;
import com.darksune.althera.network.packet.HeroStatsPacket;
import com.darksune.althera.network.packet.RequestHeroStatsPacket;
import com.darksune.althera.network.packet.ToggleHeroSettingPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    private static final int SETTING_ROW_HEIGHT = 16;
    private static final int SETTING_HEADER_HEIGHT = 12;
    private static final int SETTING_NUMBER_WIDTH = 50;
    private static final int SETTING_STEP_SIZE = 10;
    private static final int SETTING_TOOLTIP_WIDTH = 180;
    private static final int OTHER_OPTIONS_WIDTH = 80;
    private static final String RESET_HUD_LABEL = "Reset";

    private static final int BUTTON_HEIGHT = 16;
    private static final int HALF_BUTTON_GAP = 4;
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

    private static final int STATS_REQUEST_INTERVAL = 10;
    private static final int EFFECT_ICON_SIZE = 12;
    private static final double STAT_EPSILON = 0.05;
    private static final double RESISTANCE_PER_LEVEL = 0.2;

    private static final String CHARGE_GLYPH = "◆";
    private static final int COLOR_CHARGE = 0xFFE03050;
    private static final int COLOR_CHARGE_EMPTY = 0xFF4A2A30;

    private static final int DETAILS_WIDTH = 140;
    private static final int DETAILS_GAP = 12;
    private static final int DETAILS_TAB_WIDTH = 10;
    private static final int DETAILS_TAB_HEIGHT = 28;

    private static final int PAGE_TAB_X = 10;
    private static final int PAGE_TAB_WIDTH = 64;
    private static final int PAGE_TAB_HEIGHT = 15;
    private static final int PAGE_TAB_GAP = 3;
    private static final int COLOR_PAGE_TAB_IDLE = 0xFF1A1A1A;

    private static final int PLAYER_PREVIEW_SCALE = 42;
    private static final float PLAYER_PREVIEW_OFFSET = 0.0625F;
    private static final SpellElement[] ELEMENTS = {SpellElement.FIRE, SpellElement.LIGHTNING, SpellElement.ICE};
    private static final float ELEMENT_ICON_SCALE = 0.75F;
    private static final int COLOR_MANA = 0xFF3FA9FF;
    private static final int COLOR_SECTION = 0xFFE0B040;

    private static final ResourceLocation GEAR_ICON =
            ResourceLocation.fromNamespaceAndPath(
                    Althera.MOD_ID,
                    "textures/gui/gear.png"
            );

    private HeroEntity previewEntity;

    private float tickAccumulator = 0;

    private int screenTicks = 0;

    private boolean detailsOpen = false;

    private Tab currentTab = Tab.STATUS;

    private static Page lastPage = Page.HERO;

    private Page page;

    private enum Page {
        PLAYER("PLAYER"),
        HERO("HERO");

        private final String label;

        Page(String label) {
            this.label = label;
        }
    }

    private enum Tab {
        STATUS,
        SETTINGS,
        ULTIMATE
    }

    private enum SettingSection {
        HERO("HERO"),
        HUD("HUD");

        private final String title;

        SettingSection(String title) {
            this.title = title;
        }
    }

    private enum SettingRow {
        DISABLE_INTERVENTIONS(SettingSection.HERO, "Disable interventions",
                "Your hero will not step in to absorb the damage you take.", 0xFFFF5555),
        HIDE_HUD(SettingSection.HUD, "Hide HUD", "Hides the hero HUD and the skill timers.", 0xFF00FF88),
        HUD_CORNER(SettingSection.HUD, "HUD corner",
                "Screen corner of the hero HUD. Right-click to go back.", 0),
        HUD_OFFSET_X(SettingSection.HUD, "Horizontal offset",
                "Distance of the hero HUD from its corner, in GUI pixels. Shift-click: 10 at a time.", 0),
        HUD_OFFSET_Y(SettingSection.HUD, "Vertical offset",
                "Distance of the hero HUD from its corner, in GUI pixels. Shift-click: 10 at a time.", 0),
        SKILL_TIMERS(SettingSection.HUD, "Skill timers",
                "Where the timers of running Command Seals and the Ultimate are shown. Right-click to go back.", 0);

        private final SettingSection section;
        private final String label;
        private final String description;
        private final int color;

        SettingRow(SettingSection section, String label, String description, int color) {
            this.section = section;
            this.label = label;
            this.description = description;
            this.color = color;
        }
    }

    public HeroScreen() {
        super(Component.literal("Status"));
        this.page = hasHero() ? lastPage : Page.PLAYER;
    }

    private static boolean hasHero() {
        final Minecraft mc = Minecraft.getInstance();
        return mc.player != null && HeroData.get(mc.player).getHeroDefinition() != null;
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
    public void tick() {
        super.tick();

        final Minecraft mc = Minecraft.getInstance();

        if (page == Page.HERO && currentTab == Tab.STATUS && mc.player != null && HeroData.get(mc.player).isSummoned()
                && screenTicks++ % STATS_REQUEST_INTERVAL == 0) {
            PacketDistributor.sendToServer(new RequestHeroStatsPacket());
        }
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

        renderPageTabs(gui, mouseX, mouseY);

        final String title = switch (currentTab) {
            case STATUS -> "STATUS";
            case SETTINGS -> "SETTINGS";
            case ULTIMATE -> "ULTIMATE";
        };

        final int centerX = x + GUI_WIDTH / 2;

        gui.drawCenteredString(this.font, title, centerX, y + 10, 0xFFFFFF);
        gui.fill(centerX - 30, y + 21, centerX + 30, y + 22, COLOR_SEPARATOR);

        renderGear(gui, mouseX, mouseY);
        renderClose(gui, mouseX, mouseY);

        if (currentTab == Tab.SETTINGS) {
            renderSettingsTab(gui, heroData, mouseX, mouseY);
            super.render(gui, mouseX, mouseY, partialTick);
            return;
        }

        if (page == Page.PLAYER) {
            renderPlayerPage(gui, mc, mouseX, mouseY);
            super.render(gui, mouseX, mouseY, partialTick);
            return;
        }

        if (currentTab == Tab.STATUS) {
            renderStatusTab(gui, heroData, mouseX, mouseY, partialTick);
            renderDetailsTab(gui, mouseX, mouseY);
        }

        if (detailsVisible()) {
            renderDetails(gui, heroData, heroData.isSummoned() ? HeroLiveStats.current() : null);
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

    private void renderPageTabs(GuiGraphics gui, int mouseX, int mouseY) {
        final int y = pageTabY();

        for (Page tab : Page.values()) {
            final int x = pageTabX(tab);
            final boolean active = tab == page;
            final boolean hovered = isOverPageTab(tab, mouseX, mouseY);
            final int bottom = y + PAGE_TAB_HEIGHT + (active ? 2 : 0);

            gui.fill(x - 2, y - 2, x + PAGE_TAB_WIDTH + 2, bottom, COLOR_BORDER);
            gui.fill(x, y, x + PAGE_TAB_WIDTH, bottom,
                    active ? COLOR_PANEL : hovered ? COLOR_BUTTON_HOVER : COLOR_PAGE_TAB_IDLE);

            if (active) {
                gui.fill(x, y - 2, x + PAGE_TAB_WIDTH, y - 1, COLOR_ACCENT);
            }

            gui.drawCenteredString(this.font, tab.label, x + PAGE_TAB_WIDTH / 2, y + (PAGE_TAB_HEIGHT - 8) / 2 + 1,
                    active ? COLOR_ACCENT : COLOR_LABEL);

            if (hovered && tab == Page.HERO && !hasHero()) {
                setTooltipForNextRenderPass(Component.literal("Choose your Hero"));
            }
        }
    }

    private void selectPage(Page target) {
        if (target == Page.HERO && !hasHero()) {
            HeroSelectionOpener.open();
            return;
        }

        page = target;
        lastPage = target;
        currentTab = Tab.STATUS;
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    private int pageTabX(Page tab) {
        return left() + PAGE_TAB_X + tab.ordinal() * (PAGE_TAB_WIDTH + PAGE_TAB_GAP);
    }

    private int pageTabY() {
        return top() - 2 - PAGE_TAB_HEIGHT;
    }

    private boolean isOverPageTab(Page tab, double mouseX, double mouseY) {
        return mouseX >= pageTabX(tab) && mouseX < pageTabX(tab) + PAGE_TAB_WIDTH
                && mouseY >= pageTabY() && mouseY < pageTabY() + PAGE_TAB_HEIGHT;
    }

    // =========================================================
    // PLAYER PAGE
    // =========================================================

    private void renderPlayerPage(GuiGraphics gui, Minecraft mc, int mouseX, int mouseY) {

        final LocalPlayer player = mc.player;
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
                PLAYER_PREVIEW_SCALE,
                PLAYER_PREVIEW_OFFSET,
                mouseX,
                mouseY,
                player
        );

        final int previewCenterX = previewLeft + PREVIEW_WIDTH / 2;
        final HeroDefinition hero = HeroData.get(player).getHeroDefinition();

        gui.drawCenteredString(this.font, player.getName(), previewCenterX, previewTop + PREVIEW_HEIGHT + 8, 0xFFFFFF);
        gui.drawCenteredString(
                this.font,
                hero == null ? "No hero yet" : hero.getName() + " · " + hero.getRank().getDisplayName(),
                previewCenterX,
                previewTop + PREVIEW_HEIGHT + 20,
                hero == null ? COLOR_LABEL : COLOR_RANK
        );

        final int infoX = x + INFO_X;
        int infoY = y + 32;

        final ManaData mana = ManaData.get(player);
        final boolean infinite = ManaData.hasInfiniteMana(player);
        final boolean exhausted = mana.isExhausted() && !infinite;

        drawStat(gui, "Mana", infinite ? "∞" : mana.getMana() + " / " + mana.getMaxMana(),
                infoX, infoY, INFO_WIDTH, exhausted ? COLOR_USED : 0xFFFFFFFF);
        infoY += 12;
        drawBar(gui, infoX, infoY, INFO_WIDTH, 6,
                infinite ? 1 : mana.getMaxMana() > 0 ? (float) mana.getMana() / mana.getMaxMana() : 0,
                exhausted ? COLOR_USED : COLOR_MANA);
        infoY += 10;

        if (exhausted) {
            gui.drawString(this.font, "Exhausted", infoX, infoY, COLOR_USED);
        }
        infoY += 14;

        final CommandSealData seals = CommandSealData.get(player);
        final int sealColor = seals.hasTattoo() ? 0xFF000000 | seals.getColor() : COLOR_CHARGE;

        drawSealCharges(gui, infoX, infoY, INFO_WIDTH, sealColor, sealColor & 0x00FFFFFF | 0x50000000);
        infoY += 14;

        gui.fill(infoX, infoY, infoX + INFO_WIDTH, infoY + 1, COLOR_SEPARATOR);
        infoY += 8;

        gui.drawString(this.font, "SPELLS", infoX, infoY, COLOR_SECTION);
        infoY += 13;

        final PlayerSpellData spells = PlayerSpellData.get(player);
        final List<ResourceLocation> trainable = SpellNodeRegistry.getTrainableIds();
        final long learned = trainable.stream().filter(spells::isLearned).count();

        drawStat(gui, "Learned", learned + " / " + trainable.size(), infoX, infoY, INFO_WIDTH, COLOR_READY);
        infoY += 12;
        drawStat(gui, "Training", String.valueOf(spells.getTraining().size()), infoX, infoY, INFO_WIDTH, COLOR_MANA);
        infoY += 12;

        final String selectedLabel = "Selected";
        final String selected = this.font.plainSubstrByWidth(
                SpellSlots.name(spells, spells.getSelectedSlot()).getString(),
                INFO_WIDTH - this.font.width(selectedLabel) - 6);

        drawStat(gui, selectedLabel, selected, infoX, infoY, INFO_WIDTH, 0xFFFF77FF);
        infoY += 13;

        renderElements(gui, spells, infoX, infoY, mouseX, mouseY);

        drawButton(gui, "Spell Tree", infoButtonX(), infoButtonY(), halfInfoWidth(), isOverSpellTreeButton(mouseX, mouseY));
        drawButton(gui, "Journal", journalButtonX(), infoButtonY(), halfInfoWidth(), isOverJournalButton(mouseX, mouseY));
    }

    private void renderElements(GuiGraphics gui, PlayerSpellData spells, int x, int y, int mouseX, int mouseY) {

        final int cellWidth = INFO_WIDTH / ELEMENTS.length;
        final int iconSize = Math.round(HudIcons.ITEM_SIZE * ELEMENT_ICON_SCALE);

        for (int i = 0; i < ELEMENTS.length; i++) {

            final SpellElement element = ELEMENTS[i];
            final int cellX = x + i * cellWidth;
            final int level = spells.getElementLevel(element);
            final int color = 0xFF000000 | Objects.requireNonNullElse(element.color().getColor(), 0xFFFFFF);

            gui.pose().pushPose();
            gui.pose().translate(cellX, y, 0.0F);
            gui.pose().scale(ELEMENT_ICON_SCALE, ELEMENT_ICON_SCALE, 1.0F);
            SpellSlots.elementIcon(element).renderFlat(gui, 0, 0, 1.0F, 1.0F);
            gui.pose().popPose();

            gui.drawString(this.font, String.valueOf(level), cellX + iconSize + 2, y + (iconSize - 8) / 2, color);

            final int barWidth = cellWidth - 6;
            drawBar(gui, cellX, y + iconSize + 2, barWidth, 2, spells.getElementLevelProgress(element), color);

            if (mouseX >= cellX && mouseX < cellX + barWidth && mouseY >= y && mouseY < y + iconSize + 4) {
                setTooltipForNextRenderPass(elementTooltip(spells, element));
            }
        }
    }

    private List<FormattedCharSequence> elementTooltip(PlayerSpellData spells, SpellElement element) {

        final int level = spells.getElementLevel(element);
        final List<Component> lines = new ArrayList<>();

        lines.add(Component.literal(element.displayName() + " mastery · Level " + level).withStyle(element.color()));
        lines.add(Component.literal(level >= SpellProficiency.MAX_LEVEL
                ? "Max level"
                : Math.round(spells.getElementLevelProgress(element) * 100.0F) + "% to level " + (level + 1))
                .withStyle(ChatFormatting.GRAY));
        lines.add(Component.literal("+" + Math.round((SpellProficiency.elementDamageMultiplier(level) - 1.0F) * 100.0F)
                + "% damage to " + element.displayName().toLowerCase(Locale.ROOT) + " spells")
                .withStyle(ChatFormatting.GREEN));
        lines.add(Component.literal("Grows with the mana you spend on them.").withStyle(ChatFormatting.DARK_GRAY));

        final List<FormattedCharSequence> split = new ArrayList<>();
        lines.forEach(line -> split.addAll(this.font.split(line, 200)));
        return split;
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

        final HeroStatsPacket live = heroData.isSummoned() ? HeroLiveStats.current() : null;

        if (live != null) {
            renderEffects(gui, live.effects(), previewLeft + 2, previewTop + 2, mouseX, mouseY);
        }

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

        final int level = heroData.getLevel();
        final double baseMaxHealth = HeroStatsSystem.getMaxHealth(definition, level, 1.0);
        final double maxHealth = live != null ? live.maxHealth() : HeroStatsSystem.getMaxHealth(heroData);
        final double health = live != null ? live.health() : heroData.getHealth();

        drawStat(
                gui,
                "Health",
                (int) Math.ceil(health) + " / " + (int) Math.ceil(maxHealth),
                infoX,
                infoY,
                INFO_WIDTH,
                statColor(maxHealth, baseMaxHealth)
        );
        infoY += 12;
        drawBar(gui, infoX, infoY, INFO_WIDTH, 6, maxHealth > 0 ? (float) (health / maxHealth) : 0, COLOR_HEALTH);
        infoY += 16;

        gui.fill(infoX, infoY, infoX + INFO_WIDTH, infoY + 1, COLOR_SEPARATOR);
        infoY += 8;

        final double baseAttack = HeroStatsSystem.getAttack(definition, level, 1.0);
        final double attack = live != null ? live.attack() : HeroStatsSystem.getAttack(heroData);

        drawStat(
                gui,
                "Damage",
                formatStat(attack, baseAttack),
                infoX,
                infoY,
                INFO_WIDTH,
                statColor(attack, baseAttack)
        );
        infoY += 12;

        final double baseArmor = HeroStatsSystem.getArmor(definition, level, 1.0);
        final double armor = live != null ? live.armor() : HeroStatsSystem.getArmor(heroData);

        drawStat(
                gui,
                "Armor",
                formatStat(armor, baseArmor),
                infoX,
                infoY,
                INFO_WIDTH,
                statColor(armor, baseArmor)
        );
        infoY += 12;

        drawSpeed(gui, heroData, live, infoX, infoY, INFO_WIDTH);
        infoY += 12;

        drawResistance(gui, live, infoX, infoY, INFO_WIDTH);

        if (ultimate(heroData) != null) {
            drawButton(gui, "Ultimate  ›", infoButtonX(), infoButtonY(), INFO_WIDTH,
                    isOverInfoButton(mouseX, mouseY));
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

    private void renderEffects(GuiGraphics gui, List<MobEffectInstance> effects, int x, int y, int mouseX, int mouseY) {

        final Minecraft mc = Minecraft.getInstance();
        final int perRow = (PREVIEW_WIDTH - 4) / (EFFECT_ICON_SIZE + 1);

        for (int i = 0; i < effects.size(); i++) {
            final MobEffectInstance effect = effects.get(i);
            final int iconX = x + (i % perRow) * (EFFECT_ICON_SIZE + 1);
            final int iconY = y + (i / perRow) * (EFFECT_ICON_SIZE + 1);
            final TextureAtlasSprite sprite = mc.getMobEffectTextures().get(effect.getEffect());

            gui.pose().pushPose();
            gui.pose().translate(0, 0, 200);
            gui.blit(iconX, iconY, 0, EFFECT_ICON_SIZE, EFFECT_ICON_SIZE, sprite);
            gui.pose().popPose();

            if (mouseX >= iconX && mouseX < iconX + EFFECT_ICON_SIZE
                    && mouseY >= iconY && mouseY < iconY + EFFECT_ICON_SIZE) {
                setTooltipForNextRenderPass(effectTooltip(mc, effect));
            }
        }
    }

    private void renderDetailsTab(GuiGraphics gui, int mouseX, int mouseY) {
        final int tabX = detailsTabX();
        final int tabY = detailsTabY();

        gui.fill(tabX, tabY - 1, tabX + DETAILS_TAB_WIDTH + 1, tabY + DETAILS_TAB_HEIGHT + 1, COLOR_BORDER);
        gui.fill(tabX, tabY, tabX + DETAILS_TAB_WIDTH, tabY + DETAILS_TAB_HEIGHT,
                isOverDetailsTab(mouseX, mouseY) ? COLOR_BUTTON_HOVER : COLOR_PANEL);
        gui.drawCenteredString(this.font, detailsOpen ? "‹" : "›", tabX + DETAILS_TAB_WIDTH / 2,
                tabY + (DETAILS_TAB_HEIGHT - 8) / 2, COLOR_ACCENT);
    }

    private void renderDetails(GuiGraphics gui, HeroData heroData, @Nullable HeroStatsPacket live) {

        final HeroDefinition definition = heroData.getHeroDefinition();

        if (definition == null) {
            return;
        }

        final int x = detailsX();
        final int y = top();
        final int innerX = x + 8;
        final int innerWidth = DETAILS_WIDTH - 16;

        gui.fill(x - 2, y - 2, x + DETAILS_WIDTH + 2, y + GUI_HEIGHT + 2, COLOR_BORDER);
        gui.fill(x, y, x + DETAILS_WIDTH, y + GUI_HEIGHT, COLOR_PANEL);

        gui.drawCenteredString(this.font, "DETAILS", x + DETAILS_WIDTH / 2, y + 10, 0xFFFFFF);
        gui.fill(x + DETAILS_WIDTH / 2 - 24, y + 21, x + DETAILS_WIDTH / 2 + 24, y + 22, COLOR_SEPARATOR);

        int rowY = y + 30;

        final double baseKnockback = HeroStatsSystem.getKnockbackResistance(definition, 1.0);
        final double knockback = live != null
                ? live.knockbackResistance()
                : HeroStatsSystem.getKnockbackResistance(heroData);

        drawStat(gui, "Knockback res.", percent(knockback), innerX, rowY, innerWidth,
                statColor(knockback * 100, baseKnockback * 100));
        rowY += 12;

        final double dodge = HeroStatsSystem.getDodgeChance(heroData);

        drawStat(gui, "Dodge", percent(dodge), innerX, rowY, innerWidth,
                statColor(dodge * 100, definition.getHeroClass().getDodgeChance() * 100));
        rowY += 12;

        final boolean spiritForm = heroData.isDefeated() || !heroData.isSummoned();
        final double regen = spiritForm
                ? HeroStatsSystem.getSpiritRegen(heroData)
                : HeroStatsSystem.getSummonedRegen(heroData);

        drawStat(gui, spiritForm ? "Spirit regen" : "Regen", String.format(Locale.ROOT, "%.2f HP/2s", regen),
                innerX, rowY, innerWidth, 0xFFFFFFFF);
        rowY += 12;

        drawSealCharges(gui, innerX, rowY, innerWidth, COLOR_CHARGE, COLOR_CHARGE_EMPTY);
        rowY += 12;

        drawUltimateReady(gui, innerX, rowY, innerWidth);
        rowY += 14;

        gui.fill(innerX, rowY, innerX + innerWidth, rowY + 1, COLOR_SEPARATOR);
        rowY += 6;

        final HeroClass heroClass = definition.getHeroClass();

        gui.drawCenteredString(this.font, heroClass.getDisplayName() + " class", x + DETAILS_WIDTH / 2, rowY, COLOR_RANK);
        rowY += 13;

        final int columnX = innerX + innerWidth / 2 + 4;

        drawMultiplier(gui, "HP", heroClass.getHealthMultiplier(), innerX, rowY);
        drawMultiplier(gui, "ATK", heroClass.getAttackMultiplier(), columnX, rowY);
        rowY += 10;
        drawMultiplier(gui, "DEF", heroClass.getArmorMultiplier(), innerX, rowY);
        drawMultiplier(gui, "SPD", heroClass.getSpeedMultiplier(), columnX, rowY);
        rowY += 14;

        final List<String> strong = heroClass.strongAgainst().stream()
                .map(id -> HeroClassRegistry.get(id).getDisplayName())
                .sorted()
                .toList();
        final List<String> weak = HeroClassRegistry.getAll().values().stream()
                .filter(heroClass::isWeakAgainst)
                .map(HeroClass::getDisplayName)
                .sorted(Comparator.naturalOrder())
                .toList();

        rowY = drawMatchup(gui, "Strong vs (+25%)", strong, COLOR_READY, innerX, rowY, innerWidth);
        drawMatchup(gui, "Weak vs (-25%)", weak, COLOR_USED, innerX, rowY + 4, innerWidth);
    }

    private void drawSpeed(GuiGraphics gui, HeroData heroData, @Nullable HeroStatsPacket live, int x, int y, int width) {
        final double baseSpeed = HeroStatsSystem.getSpeed(heroData.getHeroDefinition(), 1.0);
        final double speed = live != null ? live.speed() : HeroStatsSystem.getSpeed(heroData);
        final double speedRatio = baseSpeed > 0 ? speed / baseSpeed : 1;

        drawStat(gui, "Speed", percent(speedRatio), x, y, width, statColor(speedRatio * 100, 100));
    }

    private void drawResistance(GuiGraphics gui, @Nullable HeroStatsPacket live, int x, int y, int width) {
        final double resistance = live != null ? damageResistance(live.effects()) : 0;

        drawStat(gui, "Resistance", percent(resistance), x, y, width, statColor(resistance * 100, 0));
    }

    private void drawSealCharges(GuiGraphics gui, int x, int y, int width, int color, int emptyColor) {
        gui.drawString(this.font, "Seals", x, y, COLOR_LABEL);

        if (SkillLimits.isUnlimited(minecraft.player)) {
            gui.drawString(this.font, "∞", x + width - this.font.width("∞"), y, color);
            return;
        }

        final int charges = CommandSealData.get(minecraft.player).getCommandSealCharges();
        final int glyphWidth = this.font.width(CHARGE_GLYPH) + 1;
        int glyphX = x + width - CommandSealData.MAX_CHARGES * glyphWidth + 1;

        for (int i = 0; i < CommandSealData.MAX_CHARGES; i++) {
            gui.drawString(this.font, CHARGE_GLYPH, glyphX, y, i < charges ? color : emptyColor);
            glyphX += glyphWidth;
        }
    }

    private void drawUltimateReady(GuiGraphics gui, int x, int y, int width) {
        final boolean usedToday = UltimateData.get(minecraft.player).wasUsedToday()
                && !SkillLimits.isUnlimited(minecraft.player);

        drawStat(gui, "Ultimate", usedToday ? "Used today" : "Ready", x, y, width, usedToday ? COLOR_USED : COLOR_READY);
    }

    private void drawMultiplier(GuiGraphics gui, String label, float value, int x, int y) {
        gui.drawString(this.font, label, x, y, COLOR_LABEL);
        gui.drawString(this.font, String.format(Locale.ROOT, "x%.2f", value), x + 24, y, statColor(value, 1.0));
    }

    private int drawMatchup(GuiGraphics gui, String label, List<String> classes, int color, int x, int y, int width) {
        gui.drawString(this.font, label, x, y, color);
        y += 10;

        final String names = classes.isEmpty() ? "None" : String.join(", ", classes);

        for (FormattedCharSequence line : this.font.split(Component.literal(names), width)) {
            gui.drawString(this.font, line, x, y, 0xFFFFFFFF);
            y += 10;
        }

        return y;
    }

    private static double damageResistance(List<MobEffectInstance> effects) {
        return effects.stream()
                .filter(effect -> effect.getEffect().is(MobEffects.DAMAGE_RESISTANCE))
                .mapToDouble(effect -> Math.min(1.0, (effect.getAmplifier() + 1) * RESISTANCE_PER_LEVEL))
                .max()
                .orElse(0);
    }

    private static String percent(double ratio) {
        return String.format(Locale.ROOT, "%.0f%%", ratio * 100);
    }

    private static Component effectTooltip(Minecraft mc, MobEffectInstance effect) {

        final Component name = Component.translatable(effect.getDescriptionId());
        final Component level = effect.getAmplifier() > 0
                ? Component.literal(" ").append(Component.translatable("enchantment.level." + (effect.getAmplifier() + 1)))
                : Component.empty();
        final float tickRate = mc.level == null ? 20.0F : mc.level.tickRateManager().tickrate();

        return Component.empty().append(name).append(level)
                .append(" (").append(MobEffectUtil.formatDuration(effect, 1.0F, tickRate)).append(")");
    }

    private static String formatStat(double value, double base) {
        final double bonus = value - base;

        if (Math.abs(bonus) < STAT_EPSILON) {
            return String.format(Locale.ROOT, "%.1f", value);
        }

        return String.format(Locale.ROOT, "%.1f (%+.1f)", value, bonus);
    }

    private static int statColor(double value, double base) {
        if (value > base + STAT_EPSILON) return COLOR_READY;
        if (value < base - STAT_EPSILON) return COLOR_USED;

        return 0xFFFFFFFF;
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

        final int rowX = left() + CONTENT_X;
        SettingSection section = null;

        for (SettingRow row : SettingRow.values()) {
            final int rowY = settingRowY(row);

            if (row.section != section) {
                section = row.section;
                gui.drawString(this.font, section.title, rowX, rowY - SETTING_HEADER_HEIGHT + 2, COLOR_ACCENT);

                if (section == SettingSection.HUD) {
                    renderResetHud(gui, mouseX, mouseY);
                }
            }

            final boolean hovered = isOverSettingRow(row, mouseX, mouseY);

            gui.fill(rowX, rowY, rowX + CONTENT_WIDTH, rowY + SETTING_ROW_HEIGHT - 2,
                    hovered ? COLOR_BUTTON_HOVER : COLOR_PREVIEW);
            gui.drawString(this.font, row.label, rowX + 6, rowY + 3, 0xFFFFFF);

            renderSettingControl(gui, heroData, row, rowX + CONTENT_WIDTH - 6, rowY + 2);

            if (hovered) {
                setTooltipForNextRenderPass(this.font.split(Component.literal(row.description), SETTING_TOOLTIP_WIDTH));
            }
        }

        drawDisabledButton(gui, "Other options", otherOptionsX(), otherOptionsY(), OTHER_OPTIONS_WIDTH);

        if (isOverOtherOptions(mouseX, mouseY)) {
            setTooltipForNextRenderPass(Component.literal("Coming soon"));
        }

        final String hint = "Click the gear to go back";
        gui.drawString(this.font, hint, left() + GUI_WIDTH - CONTENT_X - this.font.width(hint),
                otherOptionsY() + (BUTTON_HEIGHT - 8) / 2, 0x777777);
    }

    private void renderResetHud(GuiGraphics gui, int mouseX, int mouseY) {
        final boolean hovered = isOverResetHud(mouseX, mouseY);

        gui.drawString(this.font, RESET_HUD_LABEL, resetHudX(), resetHudY(), hovered ? 0xFFFFFF : COLOR_LABEL);

        if (hovered) {
            setTooltipForNextRenderPass(this.font.split(
                    Component.literal("Puts the HUD corner, offsets and skill timers back to their defaults."),
                    SETTING_TOOLTIP_WIDTH));
        }
    }

    private static void resetHud() {
        AltheraClientConfig.HERO_HUD_CORNER.set(AltheraClientConfig.HERO_HUD_CORNER.getDefault());
        AltheraClientConfig.HERO_HUD_OFFSET_X.set(AltheraClientConfig.HERO_HUD_OFFSET_X.getDefault());
        AltheraClientConfig.HERO_HUD_OFFSET_Y.set(AltheraClientConfig.HERO_HUD_OFFSET_Y.getDefault());
        AltheraClientConfig.SKILL_TIMERS_POSITION.set(AltheraClientConfig.SKILL_TIMERS_POSITION.getDefault());
        AltheraClientConfig.SPEC.save();
    }

    private int resetHudX() {
        return left() + CONTENT_X + CONTENT_WIDTH - 6 - this.font.width(RESET_HUD_LABEL);
    }

    private int resetHudY() {
        return settingRowY(SettingRow.HIDE_HUD) - SETTING_HEADER_HEIGHT + 2;
    }

    private boolean isOverResetHud(double mouseX, double mouseY) {
        return mouseX >= resetHudX() && mouseX < resetHudX() + this.font.width(RESET_HUD_LABEL)
                && mouseY >= resetHudY() - 1 && mouseY < resetHudY() + 9;
    }

    private void renderSettingControl(GuiGraphics gui, HeroData heroData, SettingRow row, int right, int y) {
        switch (row) {
            case DISABLE_INTERVENTIONS -> drawCheckbox(gui, right, y, heroData.isSaveDisabled(), row.color);
            case HIDE_HUD -> drawCheckbox(gui, right, y, heroData.isHiddenHud(), row.color);
            case HUD_CORNER -> drawCycle(gui, right, y, AltheraClientConfig.HERO_HUD_CORNER.get().displayName());
            case SKILL_TIMERS -> drawCycle(gui, right, y, AltheraClientConfig.SKILL_TIMERS_POSITION.get().displayName());
            case HUD_OFFSET_X -> drawNumber(gui, right, y, AltheraClientConfig.HERO_HUD_OFFSET_X.get());
            case HUD_OFFSET_Y -> drawNumber(gui, right, y, AltheraClientConfig.HERO_HUD_OFFSET_Y.get());
        }
    }

    private void drawCheckbox(GuiGraphics gui, int right, int y, boolean checked, int color) {
        final int boxX = right - CHECKBOX_SIZE;

        gui.fill(boxX - 1, y - 1, right + 1, y + CHECKBOX_SIZE + 1, COLOR_SEPARATOR);
        gui.fill(boxX, y, right, y + CHECKBOX_SIZE, 0xFF111111);

        if (checked) {
            gui.fill(boxX + 2, y + 2, right - 2, y + CHECKBOX_SIZE - 2, color);
        }
    }

    private void drawCycle(GuiGraphics gui, int right, int y, String value) {
        final String text = "‹ " + value + " ›";
        gui.drawString(this.font, text, right - this.font.width(text), y + 1, COLOR_ACCENT);
    }

    private void drawNumber(GuiGraphics gui, int right, int y, int value) {
        final int left = right - SETTING_NUMBER_WIDTH;

        drawStepButton(gui, left, y, "-");
        drawStepButton(gui, right - SETTING_STEP_SIZE, y, "+");
        gui.drawCenteredString(this.font, String.valueOf(value), left + SETTING_NUMBER_WIDTH / 2, y + 1, 0xFFFFFF);
    }

    private void drawStepButton(GuiGraphics gui, int x, int y, String label) {
        gui.fill(x, y, x + SETTING_STEP_SIZE, y + SETTING_STEP_SIZE, COLOR_SEPARATOR);
        gui.drawCenteredString(this.font, label, x + SETTING_STEP_SIZE / 2 + 1, y + 1, 0xFFFFFF);
    }

    private boolean clickSetting(double mouseX, double mouseY, int button) {
        for (SettingRow row : SettingRow.values()) {
            if (!isOverSettingRow(row, mouseX, mouseY)) {
                continue;
            }

            final int step = button == 1 ? -1 : 1;

            switch (row) {
                case DISABLE_INTERVENTIONS -> PacketDistributor.sendToServer(
                        new ToggleHeroSettingPacket(ToggleHeroSettingPacket.Setting.SAVE_DISABLED));
                case HIDE_HUD -> PacketDistributor.sendToServer(
                        new ToggleHeroSettingPacket(ToggleHeroSettingPacket.Setting.HIDDEN_HUD));
                case HUD_CORNER -> cycle(AltheraClientConfig.HERO_HUD_CORNER, step);
                case SKILL_TIMERS -> cycle(AltheraClientConfig.SKILL_TIMERS_POSITION, step);
                case HUD_OFFSET_X -> adjustOffset(AltheraClientConfig.HERO_HUD_OFFSET_X, mouseX);
                case HUD_OFFSET_Y -> adjustOffset(AltheraClientConfig.HERO_HUD_OFFSET_Y, mouseX);
            }

            return true;
        }

        return false;
    }

    private static <T extends Enum<T>> void cycle(ModConfigSpec.EnumValue<T> value, int step) {
        final T[] values = value.get().getDeclaringClass().getEnumConstants();
        value.set(values[Math.floorMod(value.get().ordinal() + step, values.length)]);
        AltheraClientConfig.SPEC.save();
    }

    private void adjustOffset(ModConfigSpec.IntValue value, double mouseX) {
        final int right = left() + CONTENT_X + CONTENT_WIDTH - 6;
        final int left = right - SETTING_NUMBER_WIDTH;
        final int direction;

        if (mouseX >= right - SETTING_STEP_SIZE && mouseX < right) {
            direction = 1;
        } else if (mouseX >= left && mouseX < left + SETTING_STEP_SIZE) {
            direction = -1;
        } else {
            return;
        }

        final int amount = hasShiftDown() ? 10 : 1;
        value.set(Mth.clamp(value.get() + direction * amount, 0, AltheraClientConfig.MAX_HUD_OFFSET));
        AltheraClientConfig.SPEC.save();
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

    private void drawDisabledButton(GuiGraphics gui, String label, int x, int y, int width) {
        gui.fill(x - 1, y - 1, x + width + 1, y + BUTTON_HEIGHT + 1, COLOR_SEPARATOR);
        gui.fill(x, y, x + width, y + BUTTON_HEIGHT, COLOR_BUTTON);
        gui.drawCenteredString(this.font, label, x + width / 2, y + (BUTTON_HEIGHT - 8) / 2, 0x666666);
    }

    private void drawButton(GuiGraphics gui, String label, int x, int y, int width, boolean hovered) {
        gui.fill(x - 1, y - 1, x + width + 1, y + BUTTON_HEIGHT + 1, COLOR_SEPARATOR);
        gui.fill(x, y, x + width, y + BUTTON_HEIGHT, hovered ? COLOR_BUTTON_HOVER : COLOR_BUTTON);
        gui.drawCenteredString(this.font, label, x + width / 2, y + (BUTTON_HEIGHT - 8) / 2, 0xFFFFFF);
    }

    private int infoButtonX() {
        return left() + INFO_X;
    }

    private int infoButtonY() {
        return top() + GUI_HEIGHT - BUTTON_HEIGHT - 12;
    }

    private int backButtonX() {
        return left() + CONTENT_X;
    }

    private int backButtonY() {
        return top() + GUI_HEIGHT - BUTTON_HEIGHT - 10;
    }

    private int halfInfoWidth() {
        return (INFO_WIDTH - HALF_BUTTON_GAP) / 2;
    }

    private int journalButtonX() {
        return infoButtonX() + INFO_WIDTH - halfInfoWidth();
    }

    private boolean isOverSpellTreeButton(double mouseX, double mouseY) {
        return mouseX >= infoButtonX() && mouseX <= infoButtonX() + halfInfoWidth()
                && mouseY >= infoButtonY() && mouseY <= infoButtonY() + BUTTON_HEIGHT;
    }

    private boolean isOverJournalButton(double mouseX, double mouseY) {
        return mouseX >= journalButtonX() && mouseX <= journalButtonX() + halfInfoWidth()
                && mouseY >= infoButtonY() && mouseY <= infoButtonY() + BUTTON_HEIGHT;
    }

    private boolean isOverInfoButton(double mouseX, double mouseY) {
        return mouseX >= infoButtonX() && mouseX <= infoButtonX() + INFO_WIDTH
                && mouseY >= infoButtonY() && mouseY <= infoButtonY() + BUTTON_HEIGHT;
    }

    private boolean isOverBackButton(double mouseX, double mouseY) {
        return mouseX >= backButtonX() && mouseX <= backButtonX() + CONTENT_WIDTH
                && mouseY >= backButtonY() && mouseY <= backButtonY() + BUTTON_HEIGHT;
    }

    private int left() {
        return (this.width - GUI_WIDTH - (detailsVisible() ? DETAILS_GAP + DETAILS_WIDTH : 0)) / 2;
    }

    private boolean detailsVisible() {
        return detailsOpen && page == Page.HERO && currentTab == Tab.STATUS;
    }

    private int detailsX() {
        return left() + GUI_WIDTH + DETAILS_GAP;
    }

    private int detailsTabX() {
        return left() + GUI_WIDTH + 1;
    }

    private int detailsTabY() {
        return top() + (GUI_HEIGHT - DETAILS_TAB_HEIGHT) / 2;
    }

    private boolean isOverDetailsTab(double mouseX, double mouseY) {
        return mouseX >= detailsTabX() && mouseX < detailsTabX() + DETAILS_TAB_WIDTH
                && mouseY >= detailsTabY() && mouseY < detailsTabY() + DETAILS_TAB_HEIGHT;
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

    private int settingRowY(SettingRow target) {
        int y = top() + 30;
        SettingSection section = null;

        for (SettingRow row : SettingRow.values()) {
            if (row.section != section) {
                section = row.section;
                y += SETTING_HEADER_HEIGHT;
            }

            if (row == target) {
                return y;
            }

            y += SETTING_ROW_HEIGHT;
        }

        return y;
    }

    private boolean isOverOtherOptions(double mouseX, double mouseY) {
        return mouseX >= otherOptionsX() && mouseX < otherOptionsX() + OTHER_OPTIONS_WIDTH
                && mouseY >= otherOptionsY() && mouseY < otherOptionsY() + BUTTON_HEIGHT;
    }

    private int otherOptionsX() {
        return left() + CONTENT_X;
    }

    private int otherOptionsY() {
        return top() + GUI_HEIGHT - BUTTON_HEIGHT - 8;
    }


    private boolean isOverGear(double mouseX, double mouseY) {
        return mouseX >= gearX() && mouseX <= gearX() + 16
                && mouseY >= gearY() && mouseY <= gearY() + 16;
    }

    private boolean isOverSettingRow(SettingRow row, double mouseX, double mouseY) {
        final int rowX = left() + CONTENT_X;
        final int rowY = settingRowY(row);

        return mouseX >= rowX && mouseX < rowX + CONTENT_WIDTH
                && mouseY >= rowY && mouseY < rowY + SETTING_ROW_HEIGHT - 2;
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

        for (Page tab : Page.values()) {
            if ((tab != page || currentTab != Tab.STATUS) && isOverPageTab(tab, mouseX, mouseY)) {
                selectPage(tab);
                return true;
            }
        }

        if (isOverGear(mouseX, mouseY)) {
            currentTab = currentTab == Tab.SETTINGS
                    ? Tab.STATUS
                    : Tab.SETTINGS;

            return true;
        }

        if (page == Page.PLAYER && currentTab == Tab.STATUS) {
            if (isOverSpellTreeButton(mouseX, mouseY)) {
                minecraft.setScreen(new SpellTreeScreen(this));
                return true;
            }

            if (isOverJournalButton(mouseX, mouseY)) {
                minecraft.setScreen(new JournalScreen(this));
                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (page == Page.HERO && currentTab == Tab.STATUS && isOverDetailsTab(mouseX, mouseY)) {
            detailsOpen = !detailsOpen;
            return true;
        }

        final Minecraft mc = Minecraft.getInstance();

        if (currentTab == Tab.STATUS && mc.player != null && ultimate(HeroData.get(mc.player)) != null
                && isOverInfoButton(mouseX, mouseY)) {
            currentTab = Tab.ULTIMATE;
            return true;
        }

        if (currentTab == Tab.ULTIMATE && isOverBackButton(mouseX, mouseY)) {
            currentTab = Tab.STATUS;
            return true;
        }

        if (currentTab == Tab.SETTINGS) {
            if (isOverResetHud(mouseX, mouseY)) {
                resetHud();
                return true;
            }

            if (clickSetting(mouseX, mouseY, button)) {
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
