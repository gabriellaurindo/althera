package com.darksune.althera.client.screen;

import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.common.academy.teacher.TeacherDefinition;
import com.darksune.althera.common.academy.teacher.TeacherRegistry;
import com.darksune.althera.common.quest.Quest;
import com.darksune.althera.common.quest.QuestData;
import com.darksune.althera.common.quest.QuestRegistry;
import com.darksune.althera.common.quest.QuestReward;
import com.darksune.althera.common.quest.QuestSystem;
import com.darksune.althera.common.quest.QuestTask;
import com.darksune.althera.network.packet.ClaimQuestPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JournalScreen extends Screen {

    private static final int PANEL_WIDTH = 330;
    private static final int PANEL_HEIGHT = 210;
    private static final int LIST_WIDTH = 112;
    private static final int ROW_HEIGHT = 14;
    private static final int PADDING = 8;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 18;

    private static final int COLOR_BACKGROUND = 0xE0101018;
    private static final int COLOR_PANEL = 0xF01A1622;
    private static final int COLOR_BORDER = 0xFF6A4E8C;
    private static final int COLOR_ROW_SELECTED = 0xFF3A2C50;
    private static final int COLOR_ROW_HOVER = 0xFF2A2238;
    private static final int COLOR_TITLE = 0xFFE0B040;
    private static final int COLOR_TEXT = 0xFFDDDDDD;
    private static final int COLOR_MUTED = 0xFF888888;
    private static final int COLOR_DONE = 0xFF55FF55;
    private static final int COLOR_CLAIMABLE = 0xFFFFD447;
    private static final int COLOR_REWARD = 0xFF55FFFF;
    private static final int COLOR_HEADER = 0xFFD47FFF;

    @Nullable
    private final Screen parent;

    @Nullable
    private final ResourceLocation teacher;

    @Nullable
    private ResourceLocation selected;

    private Button claimButton;

    public JournalScreen(@Nullable Screen parent) {
        this(parent, null);
    }

    public JournalScreen(@Nullable Screen parent, @Nullable ResourceLocation teacher) {
        super(Component.literal(teacherTitle(teacher)));
        this.parent = parent;
        this.teacher = teacher;
    }

    private static String teacherTitle(@Nullable ResourceLocation teacher) {
        TeacherDefinition definition = TeacherRegistry.get(teacher);
        return definition == null ? "Journal" : definition.displayName();
    }

    private List<ResourceLocation> visible(Player player) {

        List<ResourceLocation> visible = QuestSystem.visible(player);

        if (teacher == null) {
            return visible;
        }

        String giver = teacher.toString();

        return visible.stream()
                .filter(id -> QuestRegistry.get(id).giver().filter(giver::equals).isPresent())
                .toList();
    }

    @Override
    protected void init() {

        if (selected == null && minecraft.player != null) {
            selected = initialSelection(minecraft.player, visible(minecraft.player), teacher != null);
        }

        claimButton = addRenderableWidget(Button.builder(Component.literal("Accept"), button -> claim())
                .bounds(left() + PANEL_WIDTH - PADDING - BUTTON_WIDTH, top() + PANEL_HEIGHT - PADDING - BUTTON_HEIGHT,
                        BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
    }

    @Nullable
    private static ResourceLocation initialSelection(Player player, List<ResourceLocation> visible, boolean greetFirst) {

        ResourceLocation claimable = visible.stream().filter(id -> QuestSystem.isClaimable(player, id)).findFirst().orElse(null);

        if (claimable != null || greetFirst) {
            return claimable;
        }

        return visible.stream().filter(id -> QuestSystem.isAvailable(player, id)).findFirst()
                .orElse(visible.isEmpty() ? null : visible.getFirst());
    }

    private int left() {
        return (width - PANEL_WIDTH) / 2;
    }

    private int top() {
        return (height - PANEL_HEIGHT) / 2;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (AltheraKeybinds.JOURNAL.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {

        int x = left();
        int y = top();

        gui.fill(0, 0, width, height, COLOR_BACKGROUND);
        gui.fill(x - 1, y - 1, x + PANEL_WIDTH + 1, y + PANEL_HEIGHT + 1, COLOR_BORDER);
        gui.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, COLOR_PANEL);
        gui.fill(x + LIST_WIDTH + PADDING, y + PADDING, x + LIST_WIDTH + PADDING + 1, y + PANEL_HEIGHT - PADDING, COLOR_BORDER);
        gui.drawCenteredString(font, title, width / 2, y - 12, COLOR_TITLE);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {

        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        updateButton(player);
        super.render(gui, mouseX, mouseY, partialTick);

        List<ResourceLocation> visible = visible(player);

        if (visible.isEmpty()) {
            gui.drawString(font, teacher == null ? "Nothing here yet." : "No lessons for now.", left() + PADDING,
                    top() + PADDING, COLOR_MUTED);
        }

        renderList(gui, player, visible, mouseX, mouseY);

        Quest quest = selected == null ? null : QuestRegistry.get(selected);

        if (quest != null) {
            renderDetails(gui, player, selected, quest);
        } else {
            renderGreeting(gui);
        }
    }

    private void renderList(GuiGraphics gui, Player player, List<ResourceLocation> visible, int mouseX, int mouseY) {

        QuestData data = QuestData.get(player);
        int x = left() + PADDING;
        int y = top() + PADDING;

        for (ResourceLocation id : visible) {

            boolean claimed = data.isClaimed(id);
            boolean claimable = QuestSystem.isClaimable(player, id);
            boolean hovered = mouseX >= x && mouseX < x + LIST_WIDTH && mouseY >= y && mouseY < y + ROW_HEIGHT;

            if (id.equals(selected)) {
                gui.fill(x, y, x + LIST_WIDTH, y + ROW_HEIGHT, COLOR_ROW_SELECTED);
            } else if (hovered) {
                gui.fill(x, y, x + LIST_WIDTH, y + ROW_HEIGHT, COLOR_ROW_HOVER);
            }

            String marker = claimed ? "✔" : claimable ? "!" : "•";
            int color = claimed ? COLOR_MUTED : claimable ? COLOR_CLAIMABLE : COLOR_TEXT;

            gui.drawString(font, marker, x + 3, y + 3, color);
            gui.drawString(font, font.plainSubstrByWidth(QuestRegistry.get(id).title(), LIST_WIDTH - 16), x + 13, y + 3, color);

            y += ROW_HEIGHT;
        }
    }

    private void renderGreeting(GuiGraphics gui) {

        TeacherDefinition definition = TeacherRegistry.get(teacher);

        if (definition == null || definition.greeting().isEmpty()) {
            return;
        }

        int x = left() + LIST_WIDTH + PADDING * 2 + 1;
        int width = PANEL_WIDTH - LIST_WIDTH - PADDING * 3 - 1;
        int y = top() + PADDING;

        for (FormattedCharSequence line : font.split(Component.literal("“" + definition.greeting() + "”"), width)) {
            gui.drawString(font, line, x, y, COLOR_TEXT);
            y += 10;
        }
    }

    private void renderDetails(GuiGraphics gui, Player player, ResourceLocation id, Quest quest) {

        int x = left() + LIST_WIDTH + PADDING * 2 + 1;
        int width = PANEL_WIDTH - LIST_WIDTH - PADDING * 3 - 1;
        int y = top() + PADDING;

        for (FormattedCharSequence line : font.split(Component.literal(quest.title()), width)) {
            gui.drawString(font, line, x, y, COLOR_TITLE);
            y += 10;
        }

        y += 1;

        if (quest.giver().isPresent()) {
            for (FormattedCharSequence line : font.split(Component.literal("From: " + TeacherRegistry.giverName(quest.giver().get())), width)) {
                gui.drawString(font, line, x, y, COLOR_MUTED);
                y += 10;
            }
        }

        y += 3;

        for (FormattedCharSequence line : font.split(Component.literal(quest.description()), width)) {
            gui.drawString(font, line, x, y, COLOR_TEXT);
            y += 10;
        }

        if (!quest.tasks().isEmpty()) {
            y += 5;
            gui.drawString(font, "Tasks", x, y, COLOR_HEADER);
            y += 11;

            for (int i = 0; i < quest.tasks().size(); i++) {

                QuestTask task = quest.tasks().get(i);
                boolean claimed = QuestData.get(player).isClaimed(id);
                int progress = claimed ? task.required() : QuestSystem.progress(player, id, i);
                boolean done = progress >= task.required();
                String count = task.required() > 1 ? " (" + progress + "/" + task.required() + ")" : "";

                for (FormattedCharSequence line : font.split(Component.literal((done ? "✔ " : "☐ ") + task.describe() + count), width)) {
                    gui.drawString(font, line, x, y, done ? COLOR_DONE : COLOR_TEXT);
                    y += 10;
                }
            }
        }

        if (!quest.rewards().isEmpty()) {
            y += 5;
            gui.drawString(font, "Rewards", x, y, COLOR_HEADER);
            y += 11;

            for (QuestReward reward : quest.rewards()) {
                for (FormattedCharSequence line : font.split(Component.literal("• " + reward.describe()), width)) {
                    gui.drawString(font, line, x, y, COLOR_REWARD);
                    y += 10;
                }
            }
        }
    }

    private void updateButton(Player player) {

        Quest quest = selected == null ? null : QuestRegistry.get(selected);

        claimButton.visible = quest != null;

        if (quest == null) {
            return;
        }

        boolean claimed = QuestData.get(player).isClaimed(selected);

        claimButton.active = !claimed && QuestSystem.isClaimable(player, selected);
        claimButton.setMessage(Component.literal(claimed ? "Completed" : quest.tasks().isEmpty() ? "Accept" : "Claim"));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (minecraft.player != null && button == 0) {

            int x = left() + PADDING;
            int y = top() + PADDING;

            for (ResourceLocation id : visible(minecraft.player)) {

                if (mouseX >= x && mouseX < x + LIST_WIDTH && mouseY >= y && mouseY < y + ROW_HEIGHT) {
                    selected = id;
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
                    return true;
                }

                y += ROW_HEIGHT;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void claim() {
        if (selected != null && minecraft.getConnection() != null) {
            minecraft.getConnection().send(new ClaimQuestPacket(selected));
        }
    }
}
