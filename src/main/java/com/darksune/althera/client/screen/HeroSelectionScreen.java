package com.darksune.althera.client.screen;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroClass;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRegistry;
import com.darksune.althera.network.packet.SelectHeroPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HeroSelectionScreen extends Screen {

    private static final int CARD_WIDTH = 132;
    private static final int CARD_HEIGHT = 224;
    private static final int CARD_GAP = 14;
    private static final int PREVIEW_HEIGHT = 112;
    private static final int DESCRIPTION_LINES = 4;

    private static final int COLOR_CARD_BORDER = 0xFF1E1E1E;
    private static final int COLOR_CARD = 0xFF2A2A2A;
    private static final int COLOR_PREVIEW = 0xFF202020;
    private static final int COLOR_HOVERED = 0xFF888888;
    private static final int COLOR_SELECTED = 0xFFE0B040;
    private static final int COLOR_RANK = 0xFFFFAA00;

    private final List<HeroDefinition> heroes = new ArrayList<>();
    private final List<HeroEntity> previews = new ArrayList<>();

    private int selected = -1;
    private float tickAccumulator = 0;
    private Button chooseButton;

    public HeroSelectionScreen() {
        super(Component.literal("Choose your Hero"));
    }

    @Override
    protected void init() {
        super.init();

        heroes.clear();
        previews.clear();
        heroes.addAll(HeroRegistry.getSelectable());

        final Minecraft mc = Minecraft.getInstance();

        for (HeroDefinition hero : heroes) {
            final HeroEntity entity = mc.level != null ? AltheraEntities.HERO.get().create(mc.level) : null;
            if (entity != null) {
                entity.setHeroId(hero.getId());
            }
            previews.add(entity);
        }

        if (selected >= heroes.size()) {
            selected = -1;
        }

        if (selected < 0) {
            selected = currentHeroIndex();
        }

        chooseButton = addRenderableWidget(
                Button.builder(Component.literal("Choose"), button -> confirm())
                        .bounds(this.width / 2 - 70, cardsTop() + CARD_HEIGHT + 10, 140, 20)
                        .build()
        );
        updateChooseButton();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        tickAccumulator += partialTick;
        while (tickAccumulator >= 1.0F) {
            previews.forEach(entity -> {
                if (entity != null) {
                    entity.tickCount++;
                }
            });
            tickAccumulator -= 1.0F;
        }

        final int top = cardsTop();

        gui.drawCenteredString(this.font, this.title, this.width / 2, top - 26, 0xFFFFFF);
        gui.drawCenteredString(
                this.font,
                currentHeroIndex() >= 0
                        ? "This version only: switch your hero once, your level carries over"
                        : "Pick the hero who will answer your call",
                this.width / 2,
                top - 14,
                0xAAAAAA
        );

        for (int i = 0; i < heroes.size(); i++) {
            renderCard(gui, i, cardLeft(i), top, mouseX, mouseY);
        }
    }

    private void renderCard(GuiGraphics gui, int index, int x, int y, int mouseX, int mouseY) {
        final HeroDefinition hero = heroes.get(index);
        final boolean hovered = isInCard(index, mouseX, mouseY);

        final int border = index == selected
                ? COLOR_SELECTED
                : hovered ? COLOR_HOVERED : COLOR_CARD_BORDER;

        gui.fill(x - 2, y - 2, x + CARD_WIDTH + 2, y + CARD_HEIGHT + 2, border);
        gui.fill(x, y, x + CARD_WIDTH, y + CARD_HEIGHT, COLOR_CARD);
        gui.fill(x + 4, y + 4, x + CARD_WIDTH - 4, y + 4 + PREVIEW_HEIGHT, COLOR_PREVIEW);

        final HeroEntity entity = previews.get(index);
        if (entity != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    gui,
                    x + 4,
                    y + 4,
                    x + CARD_WIDTH - 4,
                    y + 4 + PREVIEW_HEIGHT,
                    Math.round(32 / entity.getModelScale()),
                    0.0F,
                    mouseX,
                    mouseY,
                    entity
            );
        }

        final int centerX = x + CARD_WIDTH / 2;
        int textY = y + PREVIEW_HEIGHT + 10;

        if (index == currentHeroIndex()) {
            gui.fill(x + 4, y + 4, x + 48, y + 16, 0xCC000000);
            gui.drawString(this.font, "Current", x + 7, y + 6, COLOR_SELECTED);
        }

        gui.drawCenteredString(this.font, hero.getName(), centerX, textY, 0xFFFFFF);
        textY += 12;

        final HeroClass heroClass = hero.getHeroClass();
        gui.drawCenteredString(
                this.font,
                hero.getRank().getDisplayName() + " · " + heroClass.getDisplayName(),
                centerX,
                textY,
                COLOR_RANK
        );
        textY += 14;

        final List<FormattedCharSequence> lines =
                this.font.split(Component.literal(hero.getDescription()), CARD_WIDTH - 12);
        for (int i = 0; i < Math.min(lines.size(), DESCRIPTION_LINES); i++) {
            gui.drawString(this.font, lines.get(i), x + 6, textY, 0xAAAAAA);
            textY += 10;
        }

        final int statsY = y + CARD_HEIGHT - 24;
        gui.fill(x + 6, statsY - 4, x + CARD_WIDTH - 6, statsY - 3, 0xFF444444);
        drawMultiplier(gui, "HP", heroClass.getHealthMultiplier(), x + 6, statsY);
        drawMultiplier(gui, "ATK", heroClass.getAttackMultiplier(), centerX + 2, statsY);
        drawMultiplier(gui, "DEF", heroClass.getArmorMultiplier(), x + 6, statsY + 10);
        drawMultiplier(gui, "SPD", heroClass.getSpeedMultiplier(), centerX + 2, statsY + 10);
    }

    private void drawMultiplier(GuiGraphics gui, String label, float value, int x, int y) {
        final int color = value > 1.0F ? 0x55FF55 : value < 1.0F ? 0xFF5555 : 0xFFFFFF;

        gui.drawString(this.font, label, x, y, 0xAAAAAA);
        gui.drawString(
                this.font,
                "x" + String.format(Locale.ROOT, "%.2f", value),
                x + 26,
                y,
                color
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < heroes.size(); i++) {
            if (isInCard(i, mouseX, mouseY)) {
                selected = i;
                updateChooseButton();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void updateChooseButton() {
        if (chooseButton == null) {
            return;
        }

        chooseButton.active = selected >= 0;

        final String action = selected >= 0 && selected == currentHeroIndex() ? "Keep " : "Choose ";
        chooseButton.setMessage(Component.literal(
                selected >= 0 ? action + heroes.get(selected).getName() : "Choose"
        ));
    }

    private int currentHeroIndex() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return -1;
        }

        final HeroDefinition current = HeroData.get(mc.player).getHeroDefinition();
        if (current == null) {
            return -1;
        }

        for (int i = 0; i < heroes.size(); i++) {
            if (heroes.get(i).getId().equals(current.getId())) {
                return i;
            }
        }
        return -1;
    }

    private void confirm() {
        if (selected < 0) {
            return;
        }

        PacketDistributor.sendToServer(new SelectHeroPacket(heroes.get(selected).getId()));
        onClose();
    }

    private boolean isInCard(int index, double mouseX, double mouseY) {
        final int x = cardLeft(index);
        final int y = cardsTop();

        return mouseX >= x && mouseX <= x + CARD_WIDTH && mouseY >= y && mouseY <= y + CARD_HEIGHT;
    }

    private int cardLeft(int index) {
        final int totalWidth = heroes.size() * CARD_WIDTH + Math.max(0, heroes.size() - 1) * CARD_GAP;
        return (this.width - totalWidth) / 2 + index * (CARD_WIDTH + CARD_GAP);
    }

    private int cardsTop() {
        return Math.max(34, (this.height - CARD_HEIGHT - 30) / 2);
    }
}
