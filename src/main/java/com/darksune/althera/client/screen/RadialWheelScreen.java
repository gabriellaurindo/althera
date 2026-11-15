package com.darksune.althera.client.screen;

import com.darksune.althera.client.hud.SkillSlotState;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public abstract class RadialWheelScreen extends Screen {

    private static final int MAX_OUTER_RADIUS = 90;
    private static final int SCREEN_MARGIN = 24;
    private static final float INNER_RATIO = 0.45f;
    private static final int SEGMENTS_PER_ENTRY = 24;
    private static final float GAP_DEGREES = 3f;
    private static final float FIRST_ENTRY_DEGREES = -90f;
    private static final int LABEL_GAP = 6;
    protected static final int LINE_HEIGHT = 10;

    private static final int COLOR_SECTOR = 0xA0101010;
    private static final int COLOR_SECTOR_HOVERED = 0xC0A02040;
    private static final int COLOR_SECTOR_UNAVAILABLE = 0xA0000000;
    private static final int COLOR_SECTOR_UNAVAILABLE_HOVERED = 0xC0484048;
    private static final int COLOR_SECTOR_CURRENT = 0xB0203858;
    private static final int COLOR_CENTRE = 0x80000000;
    private static final int COLOR_ICON_DIM = 0x90000000;

    private static final int COLOR_NAME = 0xFFFFFFFF;
    private static final int COLOR_NAME_HOVERED = 0xFFFFAA00;
    private static final int COLOR_NAME_UNAVAILABLE = 0xFF888888;
    private static final int COLOR_MANA = 0xFF55FFFF;
    private static final int COLOR_NOTE = 0xFF6FA8FF;
    private static final int COLOR_READY = 0xFF55FF55;
    private static final int COLOR_ACTIVE = 0xFFFFAA00;
    private static final int COLOR_BLOCKED = 0xFFFF5555;
    private static final int COLOR_CANCEL = 0xFFAAAAAA;

    protected record Entry(Component name, ItemStack icon, @Nullable Component mana, @Nullable Component note,
                           SkillSlotState state, boolean current, Runnable pick) {
    }

    private int hovered = -1;

    protected RadialWheelScreen(Component title) {
        super(title);
    }

    protected abstract KeyMapping key();

    protected abstract List<Entry> entries(Player player);

    protected int centreExtraLines(Player player) {
        return 0;
    }

    protected void renderCentreExtra(GuiGraphics gui, Player player, int centerX, int y) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        InputConstants.Key key = key().getKey();
        if (minecraft.player == null || key.getType() == InputConstants.Type.KEYSYM
                && !InputConstants.isKeyDown(minecraft.getWindow().getWindow(), key.getValue())) {
            onClose();
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (key().matches(keyCode, scanCode)) {
            pickHovered();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (key().matchesMouse(button)) {
            pickHovered();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {

        Player player = minecraft.player;
        if (player == null) return;

        List<Entry> entries = entries(player);
        int centerX = width / 2;
        int centerY = height / 2;
        float outer = Math.min(MAX_OUTER_RADIUS, Math.min(width, height) / 2f - SCREEN_MARGIN);
        float inner = outer * INNER_RATIO;

        hovered = hoveredIndex(mouseX - centerX, mouseY - centerY, inner, entries.size());

        renderRing(gui, centerX, centerY, inner, outer, entries);
        gui.flush();

        for (int i = 0; i < entries.size(); i++) {
            renderEntry(gui, centerX, centerY, inner, outer, entries.get(i), i, entries.size());
        }

        renderCentre(gui, player, centerX, centerY, hovered >= 0 ? entries.get(hovered) : null);
    }

    private void pickHovered() {
        Player player = minecraft.player;
        if (player != null && hovered >= 0) {
            List<Entry> entries = entries(player);
            if (hovered < entries.size()) {
                entries.get(hovered).pick().run();
            }
        }
        onClose();
    }

    private static float stepDegrees(int count) {
        return 360f / count;
    }

    private static float centerDegrees(int index, int count) {
        return FIRST_ENTRY_DEGREES + index * stepDegrees(count);
    }

    private static int hoveredIndex(float dx, float dy, float inner, int count) {

        if (count == 0 || dx * dx + dy * dy < inner * inner) return -1;

        float step = stepDegrees(count);
        float degrees = (float) Math.toDegrees(Math.atan2(dy, dx)) - FIRST_ENTRY_DEGREES + step / 2f;

        return Mth.floor(Mth.positiveModulo(degrees, 360f) / step) % count;
    }

    private void renderRing(GuiGraphics gui, int centerX, int centerY, float inner, float outer, List<Entry> entries) {

        VertexConsumer buffer = gui.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix = gui.pose().last().pose();
        int count = entries.size();
        float step = stepDegrees(count);

        for (int i = 0; i < count; i++) {

            int color = sectorColor(entries.get(i), i == hovered);
            float start = centerDegrees(i, count) - step / 2f + GAP_DEGREES / 2f;
            float sweep = (step - GAP_DEGREES) / SEGMENTS_PER_ENTRY;

            for (int segment = 0; segment < SEGMENTS_PER_ENTRY; segment++) {

                double from = Math.toRadians(start + segment * sweep);
                double to = Math.toRadians(start + (segment + 1) * sweep);

                vertex(buffer, matrix, centerX, centerY, outer, from, color);
                vertex(buffer, matrix, centerX, centerY, inner, from, color);
                vertex(buffer, matrix, centerX, centerY, inner, to, color);
                vertex(buffer, matrix, centerX, centerY, outer, to, color);
            }
        }

        int centreSegments = SEGMENTS_PER_ENTRY * 2;
        float centreRadius = inner - GAP_DEGREES;

        for (int segment = 0; segment < centreSegments; segment++) {

            double from = Math.toRadians(360.0 * segment / centreSegments);
            double to = Math.toRadians(360.0 * (segment + 1) / centreSegments);

            vertex(buffer, matrix, centerX, centerY, centreRadius, from, COLOR_CENTRE);
            vertex(buffer, matrix, centerX, centerY, 0, from, COLOR_CENTRE);
            vertex(buffer, matrix, centerX, centerY, 0, to, COLOR_CENTRE);
            vertex(buffer, matrix, centerX, centerY, centreRadius, to, COLOR_CENTRE);
        }
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, int centerX, int centerY, float radius,
                               double angle, int color) {
        buffer.addVertex(matrix, centerX + (float) Math.cos(angle) * radius, centerY + (float) Math.sin(angle) * radius, 0)
                .setColor(color);
    }

    private static int sectorColor(Entry entry, boolean hovered) {
        boolean ready = entry.state().ready();
        if (hovered) {
            return ready ? COLOR_SECTOR_HOVERED : COLOR_SECTOR_UNAVAILABLE_HOVERED;
        }
        if (entry.current()) {
            return COLOR_SECTOR_CURRENT;
        }
        return ready ? COLOR_SECTOR : COLOR_SECTOR_UNAVAILABLE;
    }

    private void renderEntry(GuiGraphics gui, int centerX, int centerY, float inner, float outer, Entry entry,
                             int index, int count) {

        double angle = Math.toRadians(centerDegrees(index, count));
        float dirX = (float) Math.cos(angle);
        float dirY = (float) Math.sin(angle);
        float middle = (inner + outer) / 2f;

        int iconX = Math.round(centerX + dirX * middle) - 8;
        int iconY = Math.round(centerY + dirY * middle) - 8;

        if (!entry.icon().isEmpty()) {
            gui.renderItem(entry.icon(), iconX, iconY);

            if (!entry.state().ready()) {
                gui.pose().pushPose();
                gui.pose().translate(0, 0, 200);
                gui.fill(iconX, iconY, iconX + 16, iconY + 16, COLOR_ICON_DIM);
                gui.pose().popPose();
            }
        }

        Font font = this.font;
        int labelWidth = font.width(entry.name());
        float labelDistance = outer + LABEL_GAP;
        int labelX = Math.round(centerX + dirX * labelDistance + dirX * labelWidth / 2f) - labelWidth / 2;
        int labelY = Math.round(centerY + dirY * labelDistance + dirY * font.lineHeight / 2f) - font.lineHeight / 2;

        gui.drawString(font, entry.name(), labelX, labelY, nameColor(entry, index == hovered));
    }

    private static int nameColor(Entry entry, boolean hovered) {
        if (!entry.state().ready()) return COLOR_NAME_UNAVAILABLE;
        return hovered || entry.current() ? COLOR_NAME_HOVERED : COLOR_NAME;
    }

    private void renderCentre(GuiGraphics gui, Player player, int centerX, int centerY, @Nullable Entry entry) {

        List<Component> lines = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        if (entry == null) {
            lines.add(Component.literal("Cancel"));
            colors.add(COLOR_CANCEL);
        } else {
            SkillSlotState state = entry.state();
            lines.add(entry.name());
            colors.add(state.ready() ? COLOR_NAME_HOVERED : COLOR_NAME_UNAVAILABLE);

            if (entry.mana() != null) {
                lines.add(entry.mana());
                colors.add(COLOR_MANA);
            }

            if (entry.note() != null) {
                lines.add(entry.note());
                colors.add(COLOR_NOTE);
            }

            lines.add(Component.literal(stateText(state)));
            colors.add(stateColor(state));
        }

        int extraLines = centreExtraLines(player);
        int top = centerY - (lines.size() + extraLines) * LINE_HEIGHT / 2;

        for (int i = 0; i < lines.size(); i++) {
            gui.drawCenteredString(font, lines.get(i), centerX, top + i * LINE_HEIGHT, colors.get(i));
        }

        if (extraLines > 0) {
            renderCentreExtra(gui, player, centerX, top + lines.size() * LINE_HEIGHT);
        }
    }

    private static String stateText(SkillSlotState state) {
        if (state.active()) return "Active " + seconds(state.timerTicks()) + "s";
        if (state.blocker() != null) return state.blocker();
        if (state.onCooldown()) return "Cooldown " + seconds(state.timerTicks()) + "s";
        return "Ready";
    }

    private static int stateColor(SkillSlotState state) {
        if (state.active()) return COLOR_ACTIVE;
        if (state.blocker() != null || state.onCooldown()) return COLOR_BLOCKED;
        return COLOR_READY;
    }

    private static int seconds(int ticks) {
        return (ticks + 19) / 20;
    }
}
