package com.darksune.althera.client.screen;

import com.darksune.althera.client.hud.SkillSlotState;
import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.client.input.KeyActions;
import com.darksune.althera.common.commandseal.CommandSealData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class SealWheelScreen extends Screen {

    private static final int MAX_OUTER_RADIUS = 90;
    private static final int SCREEN_MARGIN = 24;
    private static final float INNER_RATIO = 0.45f;
    private static final int SEGMENTS_PER_ENTRY = 24;
    private static final float GAP_DEGREES = 3f;
    private static final float FIRST_ENTRY_DEGREES = -90f;
    private static final int LABEL_GAP = 6;
    private static final int LINE_HEIGHT = 10;

    private static final int COLOR_SECTOR = 0xA0101010;
    private static final int COLOR_SECTOR_HOVERED = 0xC0A02040;
    private static final int COLOR_SECTOR_UNAVAILABLE = 0xA0000000;
    private static final int COLOR_SECTOR_UNAVAILABLE_HOVERED = 0xC0484048;
    private static final int COLOR_CENTRE = 0x80000000;
    private static final int COLOR_ICON_DIM = 0x90000000;

    private static final int COLOR_NAME = 0xFFFFFFFF;
    private static final int COLOR_NAME_HOVERED = 0xFFFFAA00;
    private static final int COLOR_NAME_UNAVAILABLE = 0xFF888888;
    private static final int COLOR_MANA = 0xFF55FFFF;
    private static final int COLOR_READY = 0xFF55FF55;
    private static final int COLOR_ACTIVE = 0xFFFFAA00;
    private static final int COLOR_BLOCKED = 0xFFFF5555;
    private static final int COLOR_CANCEL = 0xFFAAAAAA;
    private static final int COLOR_CHARGE = 0xFFE03050;
    private static final int COLOR_CHARGE_EMPTY = 0xFF4A2A30;

    private static final String CHARGE_GLYPH = "◆";

    private record Entry(Component name, ItemStack icon, int manaCost, SkillSlotState state, Runnable cast) {
    }

    private int hovered = -1;

    public SealWheelScreen() {
        super(Component.literal("Command Seals"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        InputConstants.Key key = AltheraKeybinds.SEAL.getKey();
        if (minecraft.player == null || key.getType() == InputConstants.Type.KEYSYM
                && !InputConstants.isKeyDown(minecraft.getWindow().getWindow(), key.getValue())) {
            onClose();
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (AltheraKeybinds.SEAL.matches(keyCode, scanCode)) {
            castHovered();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (AltheraKeybinds.SEAL.matchesMouse(button)) {
            castHovered();
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

        renderCentre(gui, centerX, centerY, hovered >= 0 ? entries.get(hovered) : null,
                CommandSealData.get(player).getCommandSealCharges());
    }

    private void castHovered() {
        Player player = minecraft.player;
        if (player != null && hovered >= 0) {
            List<Entry> entries = entries(player);
            if (hovered < entries.size()) {
                entries.get(hovered).cast().run();
            }
        }
        onClose();
    }

    private static List<Entry> entries(Player player) {

        List<Entry> entries = new ArrayList<>();

        for (CommandSealSkillType type : CommandSealSkillType.values()) {
            entries.add(new Entry(
                    Component.translatable("althera.commandseal." + type.getId()),
                    sealIcon(type),
                    type.getSkill().getManaCost(),
                    SkillSlotState.seal(player, type),
                    () -> KeyActions.handleCommandSealSkill(type)));
        }
        return entries;
    }

    private static ItemStack sealIcon(CommandSealSkillType type) {
        return new ItemStack(switch (type) {
            case OVERDRIVE -> Items.BLAZE_POWDER;
            case REVIVE -> Items.TOTEM_OF_UNDYING;
            case HEAL -> Items.GLISTERING_MELON_SLICE;
        });
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

            int color = sectorColor(entries.get(i).state().ready(), i == hovered);
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

    private static int sectorColor(boolean ready, boolean hovered) {
        if (ready) {
            return hovered ? COLOR_SECTOR_HOVERED : COLOR_SECTOR;
        }
        return hovered ? COLOR_SECTOR_UNAVAILABLE_HOVERED : COLOR_SECTOR_UNAVAILABLE;
    }

    private void renderEntry(GuiGraphics gui, int centerX, int centerY, float inner, float outer, Entry entry,
                             int index, int count) {

        double angle = Math.toRadians(centerDegrees(index, count));
        float dirX = (float) Math.cos(angle);
        float dirY = (float) Math.sin(angle);
        float middle = (inner + outer) / 2f;

        int iconX = Math.round(centerX + dirX * middle) - 8;
        int iconY = Math.round(centerY + dirY * middle) - 8;
        gui.renderItem(entry.icon(), iconX, iconY);

        if (!entry.state().ready()) {
            gui.pose().pushPose();
            gui.pose().translate(0, 0, 200);
            gui.fill(iconX, iconY, iconX + 16, iconY + 16, COLOR_ICON_DIM);
            gui.pose().popPose();
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
        return hovered ? COLOR_NAME_HOVERED : COLOR_NAME;
    }

    private void renderCentre(GuiGraphics gui, int centerX, int centerY, Entry entry, int charges) {

        List<Component> lines = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        if (entry == null) {
            lines.add(Component.literal("Cancel"));
            colors.add(COLOR_CANCEL);
        } else {
            SkillSlotState state = entry.state();
            lines.add(entry.name());
            colors.add(state.ready() ? COLOR_NAME_HOVERED : COLOR_NAME_UNAVAILABLE);

            if (entry.manaCost() > 0) {
                lines.add(Component.literal(entry.manaCost() + " mana"));
                colors.add(COLOR_MANA);
            }

            lines.add(Component.literal(stateText(state)));
            colors.add(stateColor(state));
        }

        int top = centerY - (lines.size() + 1) * LINE_HEIGHT / 2;

        for (int i = 0; i < lines.size(); i++) {
            gui.drawCenteredString(font, lines.get(i), centerX, top + i * LINE_HEIGHT, colors.get(i));
        }

        renderCharges(gui, centerX, top + lines.size() * LINE_HEIGHT, charges);
    }

    private void renderCharges(GuiGraphics gui, int centerX, int y, int charges) {

        int glyphWidth = font.width(CHARGE_GLYPH) + 1;
        int x = centerX - CommandSealData.MAX_CHARGES * glyphWidth / 2;

        for (int i = 0; i < CommandSealData.MAX_CHARGES; i++) {
            gui.drawString(font, CHARGE_GLYPH, x, y, i < charges ? COLOR_CHARGE : COLOR_CHARGE_EMPTY);
            x += glyphWidth;
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
