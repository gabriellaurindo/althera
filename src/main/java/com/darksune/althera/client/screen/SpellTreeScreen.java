package com.darksune.althera.client.screen;

import com.darksune.althera.client.hud.SpellSlots;
import com.darksune.althera.client.input.AltheraKeybinds;
import com.darksune.althera.client.input.KeyActions;
import com.darksune.althera.common.attachment.ManaData;
import com.darksune.althera.common.spell.PlayerSpell;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellBranch;
import com.darksune.althera.common.spell.SpellCastMode;
import com.darksune.althera.common.spell.SpellDefinition;
import com.darksune.althera.common.spell.SpellElement;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.darksune.althera.common.spell.SpellProficiency;
import com.darksune.althera.common.spell.SpellScale;
import com.darksune.althera.common.spell.SpellNodeState;
import com.darksune.althera.common.spell.SpellTraining;
import com.darksune.althera.network.packet.EquipSpellPacket;
import com.darksune.althera.network.packet.SeenSpellNodesPacket;
import com.darksune.althera.network.packet.TrainSpellNodePacket;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SpellTreeScreen extends Screen {

    private static final float SPACING = 34.0F;
    private static final int NODE_SIZE = 22;
    private static final int NODE_HALF = NODE_SIZE / 2;
    private static final int BORDER = 2;
    private static final float LINE_WIDTH = 2.0F;
    private static final int PROGRESS_GAP = 2;
    private static final int PROGRESS_HEIGHT = 2;
    private static final int LABEL_GAP = 6;
    private static final int LABEL_SPACE = 24;

    private static final float MIN_ZOOM = 0.5F;
    private static final float MAX_ZOOM = 2.0F;
    private static final float FIT_MAX_ZOOM = 1.0F;
    private static final float ZOOM_STEP = 1.15F;

    private static final int MARGIN = 12;
    private static final int HEADER_HEIGHT = 20;
    private static final int FOOTER_HEIGHT = 14;
    private static final int SLOT_SIZE = 20;
    private static final int SLOT_GAP = 2;
    private static final int SLOT_BAR_GAP = 4;
    private static final int DRAG_THRESHOLD = 3;
    private static final int TOOLTIP_WIDTH = 200;
    private static final int Z_OVERLAY = 200;
    private static final int Z_CARRIED = 300;

    private static final int COLOR_BACKGROUND = 0xE0101018;
    private static final int COLOR_NODE = 0xFF1A1A24;
    private static final int COLOR_LEARNED = 0xFF55FF55;
    private static final int COLOR_MASTERED = 0xFFFFD43B;
    private static final int COLOR_STAR_OUTLINE = 0xFF3A2A08;
    private static final String COMING_SOON_MARK = "X";
    private static final int COLOR_HIGHLIGHT = 0xE6C8FF;
    private static final int HIGHLIGHT_SIZE = 4;
    private static final long HIGHLIGHT_MILLIS = 6000L;
    private static final long HIGHLIGHT_PULSE_MILLIS = 900L;
    private static final String[] MASTERED_STAR = {"..#..", ".###.", "#####", ".###.", ".#.#."};
    private static final int COLOR_TRAINING = 0xFF5599FF;
    private static final int COLOR_AVAILABLE = 0xFFFFDD55;
    private static final int COLOR_LOCKED = 0xFFCC4444;
    private static final int COLOR_PLACEHOLDER = 0xFF555560;
    private static final int COLOR_UNUSABLE = 0xFFFFAA44;
    private static final int COLOR_HOVER = 0xFFFFFFFF;
    private static final int COLOR_FADE = 0xA0000000;
    private static final int COLOR_LINE_OFF = 0xFF3A3A44;
    private static final int LINE_ALPHA = 0x90000000;
    private static final int COLOR_PROGRESS_BACK = 0xFF202030;
    private static final int COLOR_PROGRESS_PAUSED = 0xFF6A7A99;
    private static final int COLOR_TITLE = 0xFFE0B040;
    private static final int COLOR_MANA = 0xFF55FFFF;
    private static final int COLOR_HINT = 0xFF888888;
    private static final int COLOR_SLOT = 0xFF202028;
    private static final int COLOR_SLOT_BORDER = 0xFF444450;
    private static final int COLOR_SLOT_SELECTED = 0xFFE0B040;
    private static final int COLOR_SLOT_TARGET = 0xFF55FF55;
    private static final int COLOR_SLOT_NUMBER = 0xFFAAAAAA;
    private static final int COLOR_PLACEHOLDER_TEXT = 0xFF888888;

    private static final Map<SpellBranch, Integer> BRANCH_COLORS = new EnumMap<>(Map.of(
            SpellBranch.DAMAGE, 0xFFFF7755,
            SpellBranch.DEFENSE, 0xFF55AAFF,
            SpellBranch.UTILITY, 0xFF77DD77
    ));

    private static final String HINT = "Click: train · Right-click or drag: equip · Scroll: zoom";

    private float zoom = FIT_MAX_ZOOM;
    private float panX;
    private float panY;
    private boolean positioned;

    private Set<ResourceLocation> highlighted = Set.of();

    private long highlightStart;

    @Nullable
    private ResourceLocation pressedNode;
    private int pressedSlot = -1;
    private double pressX;
    private double pressY;
    private boolean dragging;
    private boolean panning;

    @Nullable
    private ResourceLocation carried;
    private int carriedFromSlot = -1;

    @Nullable
    private final Screen parent;

    public SpellTreeScreen() {
        this(null);
    }

    public SpellTreeScreen(@Nullable Screen parent) {
        super(Component.literal("Spell Tree"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    protected void init() {
        if (!positioned) {
            fit();
            positioned = true;
            highlightFresh();
        }
    }

    private void highlightFresh() {

        if (minecraft.player == null) {
            return;
        }

        highlighted = PlayerSpellData.get(minecraft.player).getFresh();
        highlightStart = Util.getMillis();

        if (!highlighted.isEmpty()) {
            send(SeenSpellNodesPacket.INSTANCE);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.fill(0, 0, width, height, COLOR_BACKGROUND);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (AltheraKeybinds.SPELL_TREE.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private Map<ResourceLocation, SpellNode> visibleNodes() {
        return minecraft.player == null ? Map.of() : PlayerSpellData.get(minecraft.player).visibleNodes();
    }

    private void fit() {

        Map<ResourceLocation, SpellNode> nodes = visibleNodes();

        if (nodes.isEmpty()) {
            return;
        }

        float minX = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        for (SpellNode node : nodes.values()) {
            minX = Math.min(minX, node.x());
            maxX = Math.max(maxX, node.x());
            minY = Math.min(minY, node.y());
            maxY = Math.max(maxY, node.y());
        }

        float graphWidth = (maxX - minX) * SPACING + NODE_SIZE + LABEL_SPACE * 2;
        float graphHeight = (maxY - minY) * SPACING + NODE_SIZE + LABEL_SPACE * 2;
        float areaWidth = width - MARGIN * 2;
        float areaHeight = graphBottom() - graphTop() - MARGIN * 2;

        zoom = Mth.clamp(Math.min(areaWidth / graphWidth, areaHeight / graphHeight), MIN_ZOOM, FIT_MAX_ZOOM);

        float centreX = (minX + maxX) / 2.0F * SPACING;
        float centreY = -(minY + maxY) / 2.0F * SPACING;

        panX = -centreX * zoom;
        panY = (graphTop() + graphBottom()) / 2.0F - height / 2.0F - centreY * zoom;
    }

    private int graphTop() {
        return HEADER_HEIGHT;
    }

    private int graphBottom() {
        return slotBarY() - SLOT_BAR_GAP;
    }

    private int slotBarY() {
        return height - FOOTER_HEIGHT - SLOT_SIZE;
    }

    private int slotBarX() {
        return (width - (PlayerSpellData.SLOT_COUNT * SLOT_SIZE + (PlayerSpellData.SLOT_COUNT - 1) * SLOT_GAP)) / 2;
    }

    private int slotX(int slot) {
        return slotBarX() + slot * (SLOT_SIZE + SLOT_GAP);
    }

    private float originX() {
        return width / 2.0F + panX;
    }

    private float originY() {
        return height / 2.0F + panY;
    }

    private static int nodeX(SpellNode node) {
        return Math.round(node.x() * SPACING);
    }

    private static int nodeY(SpellNode node) {
        return Math.round(-node.y() * SPACING);
    }

    private boolean inGraph(double mouseY) {
        return mouseY >= graphTop() && mouseY < graphBottom();
    }

    @Nullable
    private ResourceLocation nodeAt(double mouseX, double mouseY) {

        if (!inGraph(mouseY)) {
            return null;
        }

        double graphX = (mouseX - originX()) / zoom;
        double graphY = (mouseY - originY()) / zoom;

        for (Map.Entry<ResourceLocation, SpellNode> entry : visibleNodes().entrySet()) {

            SpellNode node = entry.getValue();

            if (Math.abs(graphX - nodeX(node)) <= NODE_HALF && Math.abs(graphY - nodeY(node)) <= NODE_HALF) {
                return entry.getKey();
            }
        }

        return null;
    }

    private int slotAt(double mouseX, double mouseY) {

        int y = slotBarY();

        if (mouseY < y || mouseY >= y + SLOT_SIZE) {
            return -1;
        }

        for (int slot = 0; slot < PlayerSpellData.SLOT_COUNT; slot++) {
            int x = slotX(slot);
            if (mouseX >= x && mouseX < x + SLOT_SIZE) {
                return slot;
            }
        }

        return -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        Player player = minecraft.player;

        if (player == null) {
            return false;
        }

        if (button == 0) {
            pressedSlot = slotAt(mouseX, mouseY);
            pressedNode = pressedSlot < 0 ? nodeAt(mouseX, mouseY) : null;
            pressX = mouseX;
            pressY = mouseY;
            dragging = false;
            panning = false;
            return true;
        }

        if (button == 1) {
            PlayerSpellData data = PlayerSpellData.get(player);
            int slot = slotAt(mouseX, mouseY);

            if (slot >= 0) {
                if (data.getSlot(slot) != null) {
                    send(new EquipSpellPacket(slot, Optional.empty()));
                    playClick();
                }
                return true;
            }

            ResourceLocation node = nodeAt(mouseX, mouseY);

            if (node != null && data.canEquip(node)) {
                toggleEquip(data, node);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {

        Player player = minecraft.player;

        if (button != 0 || player == null) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        if (!dragging && Math.abs(mouseX - pressX) + Math.abs(mouseY - pressY) > DRAG_THRESHOLD) {
            dragging = true;
            startDrag(PlayerSpellData.get(player));
        }

        if (panning) {
            panX += (float) dragX;
            panY += (float) dragY;
        }

        return true;
    }

    private void startDrag(PlayerSpellData data) {

        if (pressedSlot >= 0) {
            carried = data.getSlot(pressedSlot);
            carriedFromSlot = carried == null ? -1 : pressedSlot;
            return;
        }

        if (pressedNode != null && data.canEquip(pressedNode)) {
            carried = pressedNode;
            return;
        }

        panning = true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {

        Player player = minecraft.player;

        if (button != 0 || player == null) {
            return super.mouseReleased(mouseX, mouseY, button);
        }

        PlayerSpellData data = PlayerSpellData.get(player);

        if (carried != null) {
            drop(slotAt(mouseX, mouseY));
        } else if (!dragging && pressedNode != null && pressedNode.equals(nodeAt(mouseX, mouseY))) {
            toggleTraining(data, pressedNode);
        } else if (!dragging && pressedSlot >= 0 && pressedSlot == slotAt(mouseX, mouseY)) {
            KeyActions.selectSpellSlot(pressedSlot);
            playClick();
        }

        pressedNode = null;
        pressedSlot = -1;
        dragging = false;
        panning = false;
        carried = null;
        carriedFromSlot = -1;

        return true;
    }

    private void drop(int target) {

        if (target >= 0 && target != carriedFromSlot) {
            send(new EquipSpellPacket(target, Optional.of(carried)));
            playClick();
        } else if (target < 0 && carriedFromSlot >= 0) {
            send(new EquipSpellPacket(carriedFromSlot, Optional.empty()));
            playClick();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {

        if (scrollY == 0.0D || !inGraph(mouseY)) {
            return false;
        }

        float newZoom = Mth.clamp(zoom * (float) Math.pow(ZOOM_STEP, scrollY), MIN_ZOOM, MAX_ZOOM);
        float graphX = (float) (mouseX - originX()) / zoom;
        float graphY = (float) (mouseY - originY()) / zoom;

        zoom = newZoom;
        panX = (float) mouseX - graphX * zoom - width / 2.0F;
        panY = (float) mouseY - graphY * zoom - height / 2.0F;

        return true;
    }

    private void toggleTraining(PlayerSpellData data, ResourceLocation node) {

        SpellNodeState state = data.getState(node);

        if (state == SpellNodeState.AVAILABLE) {
            send(new TrainSpellNodePacket(node, true));
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ENCHANTMENT_TABLE_USE, 1.2F));
        } else if (state == SpellNodeState.TRAINING) {
            send(new TrainSpellNodePacket(node, false));
            playClick();
        }
    }

    private void toggleEquip(PlayerSpellData data, ResourceLocation node) {

        int equipped = data.slotOf(node);

        if (equipped >= 0) {
            send(new EquipSpellPacket(equipped, Optional.empty()));
        } else {
            send(new EquipSpellPacket(firstFreeSlot(data), Optional.of(node)));
        }

        playClick();
    }

    private static int firstFreeSlot(PlayerSpellData data) {

        for (int slot = 0; slot < PlayerSpellData.SLOT_COUNT; slot++) {
            if (data.getSlot(slot) == null) {
                return slot;
            }
        }

        return data.getSelectedSlot();
    }

    private void send(CustomPacketPayload packet) {
        minecraft.getConnection().send(packet);
    }

    private void playClick() {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {

        super.render(gui, mouseX, mouseY, partialTick);

        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        PlayerSpellData data = PlayerSpellData.get(player);
        Map<ResourceLocation, SpellNode> nodes = data.visibleNodes();
        ResourceLocation hoveredNode = carried == null && !panning ? nodeAt(mouseX, mouseY) : null;
        int hoveredSlot = slotAt(mouseX, mouseY);

        gui.enableScissor(0, graphTop(), width, graphBottom());
        gui.pose().pushPose();
        gui.pose().translate(originX(), originY(), 0.0F);
        gui.pose().scale(zoom, zoom, 1.0F);

        renderLinks(gui, data, nodes);
        renderBranchLabels(gui, nodes);

        for (Map.Entry<ResourceLocation, SpellNode> entry : nodes.entrySet()) {
            renderNode(gui, data, entry.getKey(), entry.getValue(), entry.getKey().equals(hoveredNode));
        }

        gui.pose().popPose();
        gui.disableScissor();

        renderHeader(gui, player, nodes.isEmpty());
        renderSlots(gui, data, hoveredSlot);

        if (carried != null) {
            gui.pose().pushPose();
            gui.pose().translate(0.0F, 0.0F, Z_CARRIED);
            SpellSlots.icon(carried).render(gui, mouseX - 8, mouseY - 8);
            gui.pose().popPose();
            return;
        }

        int maxMana = ManaData.get(player).getMaxMana();

        if (hoveredNode != null) {
            gui.renderTooltip(font, tooltip(data, hoveredNode, nodes.get(hoveredNode), maxMana), mouseX, mouseY);
        } else if (hoveredSlot >= 0) {
            ResourceLocation id = data.getSlot(hoveredSlot);
            SpellNode node = id == null ? null : SpellNodeRegistry.get(id);
            List<FormattedCharSequence> lines = node == null
                    ? List.of(Component.literal("Empty slot").getVisualOrderText(),
                    Component.literal("Drag a spell here").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText())
                    : tooltip(data, id, node, maxMana);
            gui.renderTooltip(font, lines, mouseX, mouseY);
        }
    }

    private void renderHeader(GuiGraphics gui, Player player, boolean empty) {

        gui.drawCenteredString(font, title, width / 2, 6, COLOR_TITLE);
        gui.drawString(font, "Max mana: " + ManaData.get(player).getMaxMana(), MARGIN, 6, COLOR_MANA);
        gui.drawCenteredString(font, HINT, width / 2, height - FOOTER_HEIGHT + 3, COLOR_HINT);

        if (empty) {
            gui.drawCenteredString(font, "No spells exist yet.", width / 2, (graphTop() + graphBottom()) / 2, COLOR_HINT);
        }
    }

    private void renderLinks(GuiGraphics gui, PlayerSpellData data, Map<ResourceLocation, SpellNode> nodes) {

        VertexConsumer buffer = gui.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix = gui.pose().last().pose();

        for (Map.Entry<ResourceLocation, SpellNode> entry : nodes.entrySet()) {

            SpellNode node = entry.getValue();
            SpellNode parent = node.parent().map(nodes::get).orElse(null);

            if (parent == null) {
                continue;
            }

            int color = linkColor(data, entry.getKey());
            line(buffer, matrix, nodeX(parent), nodeY(parent), nodeX(node), nodeY(node), color);
        }

        gui.flush();
    }

    private static int linkColor(PlayerSpellData data, ResourceLocation id) {
        return switch (data.getState(id)) {
            case LEARNED -> data.isUsable(id) ? dim(COLOR_LEARNED) : dim(COLOR_UNUSABLE);
            case TRAINING -> dim(COLOR_TRAINING);
            case AVAILABLE -> dim(COLOR_AVAILABLE);
            case LOCKED, UNDISCOVERED, PLACEHOLDER -> COLOR_LINE_OFF;
        };
    }

    private static int dim(int color) {
        return color & 0x00FFFFFF | LINE_ALPHA;
    }

    private static void line(VertexConsumer buffer, Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {

        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = Mth.sqrt(dx * dx + dy * dy);

        if (length < 1.0E-3F) {
            return;
        }

        float normalX = -dy / length * LINE_WIDTH / 2.0F;
        float normalY = dx / length * LINE_WIDTH / 2.0F;

        buffer.addVertex(matrix, x1 - normalX, y1 - normalY, 0.0F).setColor(color);
        buffer.addVertex(matrix, x1 + normalX, y1 + normalY, 0.0F).setColor(color);
        buffer.addVertex(matrix, x2 + normalX, y2 + normalY, 0.0F).setColor(color);
        buffer.addVertex(matrix, x2 - normalX, y2 - normalY, 0.0F).setColor(color);
    }

    private void renderBranchLabels(GuiGraphics gui, Map<ResourceLocation, SpellNode> nodes) {

        for (Map.Entry<SpellBranch, Integer> branch : BRANCH_COLORS.entrySet()) {

            List<SpellNode> members = nodes.values().stream().filter(node -> node.branch() == branch.getKey()).toList();

            if (members.isEmpty()) {
                continue;
            }

            int minX = members.stream().mapToInt(SpellTreeScreen::nodeX).min().orElseThrow();
            int maxX = members.stream().mapToInt(SpellTreeScreen::nodeX).max().orElseThrow();
            int minY = members.stream().mapToInt(SpellTreeScreen::nodeY).min().orElseThrow();
            int maxY = members.stream().mapToInt(SpellTreeScreen::nodeY).max().orElseThrow();
            String label = StringUtils.capitalize(branch.getKey().getSerializedName());
            int color = branch.getValue();

            switch (branch.getKey()) {
                case DAMAGE -> gui.drawCenteredString(font, label, (minX + maxX) / 2,
                        minY - NODE_HALF - LABEL_GAP - font.lineHeight, color);
                case UTILITY -> gui.drawCenteredString(font, label, (minX + maxX) / 2,
                        maxY + NODE_HALF + LABEL_GAP, color);
                case DEFENSE -> gui.drawString(font, label, minX - NODE_HALF - LABEL_GAP - font.width(label),
                        (minY + maxY) / 2 - font.lineHeight / 2, color);
                case NONE -> {
                }
            }
        }
    }

    private void renderNode(GuiGraphics gui, PlayerSpellData data, ResourceLocation id, SpellNode node, boolean hovered) {

        int x = nodeX(node) - NODE_HALF;
        int y = nodeY(node) - NODE_HALF;
        SpellNodeState state = data.getState(id);

        if (highlighted.contains(id)) {
            renderHighlight(gui, x, y);
        }
        boolean faded = state == SpellNodeState.LOCKED || state == SpellNodeState.LEARNED && !data.isUsable(id);

        gui.fill(x, y, x + NODE_SIZE, y + NODE_SIZE, hovered ? COLOR_HOVER : stateColor(data, id, state));
        gui.fill(x + BORDER, y + BORDER, x + NODE_SIZE - BORDER, y + NODE_SIZE - BORDER, COLOR_NODE);

        if (state == SpellNodeState.PLACEHOLDER || state == SpellNodeState.UNDISCOVERED) {
            gui.drawCenteredString(font, state == SpellNodeState.PLACEHOLDER ? COMING_SOON_MARK : SpellNode.PLACEHOLDER_NAME,
                    x + NODE_HALF, y + NODE_HALF - font.lineHeight / 2 + 1, COLOR_PLACEHOLDER_TEXT);
            return;
        }

        SpellSlots.icon(id).render(gui, x + NODE_HALF - 8, y + NODE_HALF - 8);

        gui.pose().pushPose();
        gui.pose().translate(0.0F, 0.0F, Z_OVERLAY);

        if (faded) {
            gui.fill(x + BORDER, y + BORDER, x + NODE_SIZE - BORDER, y + NODE_SIZE - BORDER, COLOR_FADE);
        }

        if (state == SpellNodeState.LEARNED && data.isUsable(id)
                && data.getSpellLevel(id) >= SpellProficiency.MAX_LEVEL) {
            renderMasteredStar(gui, x - 2, y - 2);
        }

        gui.pose().popPose();

        float progress = data.getProgress(id);

        if (progress > 0.0F && progress < 1.0F) {
            int barY = y + NODE_SIZE + PROGRESS_GAP;
            int filled = Math.max(1, Math.round(NODE_SIZE * progress));
            gui.fill(x, barY, x + NODE_SIZE, barY + PROGRESS_HEIGHT, COLOR_PROGRESS_BACK);
            gui.fill(x, barY, x + filled, barY + PROGRESS_HEIGHT,
                    state == SpellNodeState.TRAINING ? COLOR_TRAINING : COLOR_PROGRESS_PAUSED);
        }
    }

    private void renderHighlight(GuiGraphics gui, int x, int y) {

        long elapsed = Util.getMillis() - highlightStart;

        if (elapsed >= HIGHLIGHT_MILLIS) {
            return;
        }

        float fade = 1.0F - (float) elapsed / HIGHLIGHT_MILLIS;
        float pulse = 0.5F + 0.5F * Mth.sin(elapsed / (float) HIGHLIGHT_PULSE_MILLIS * Mth.TWO_PI);
        int alpha = Math.round(255 * fade * (0.35F + 0.65F * pulse));

        for (int ring = HIGHLIGHT_SIZE; ring > 0; ring--) {
            int ringAlpha = alpha * (HIGHLIGHT_SIZE - ring + 1) / (HIGHLIGHT_SIZE + 1);
            gui.fill(x - ring, y - ring, x + NODE_SIZE + ring, y + NODE_SIZE + ring,
                    ringAlpha << 24 | COLOR_HIGHLIGHT);
        }
    }

    private static void renderMasteredStar(GuiGraphics gui, int x, int y) {

        for (int row = 0; row < MASTERED_STAR.length; row++) {
            for (int column = 0; column < MASTERED_STAR[row].length(); column++) {
                if (MASTERED_STAR[row].charAt(column) == '#') {
                    gui.fill(x + column - 1, y + row - 1, x + column + 2, y + row + 2, COLOR_STAR_OUTLINE);
                }
            }
        }

        for (int row = 0; row < MASTERED_STAR.length; row++) {
            for (int column = 0; column < MASTERED_STAR[row].length(); column++) {
                if (MASTERED_STAR[row].charAt(column) == '#') {
                    gui.fill(x + column, y + row, x + column + 1, y + row + 1, COLOR_MASTERED);
                }
            }
        }
    }

    private static int stateColor(PlayerSpellData data, ResourceLocation id, SpellNodeState state) {
        return switch (state) {
            case LEARNED -> data.isUsable(id) ? COLOR_LEARNED : COLOR_UNUSABLE;
            case TRAINING -> COLOR_TRAINING;
            case AVAILABLE -> COLOR_AVAILABLE;
            case LOCKED -> COLOR_LOCKED;
            case UNDISCOVERED, PLACEHOLDER -> COLOR_PLACEHOLDER;
        };
    }

    private void renderSlots(GuiGraphics gui, PlayerSpellData data, int hoveredSlot) {

        int y = slotBarY();

        for (int slot = 0; slot < PlayerSpellData.SLOT_COUNT; slot++) {

            int x = slotX(slot);
            ResourceLocation id = data.getSlot(slot);
            int border = carried != null && slot == hoveredSlot ? COLOR_SLOT_TARGET
                    : slot == data.getSelectedSlot() ? COLOR_SLOT_SELECTED
                    : slot == hoveredSlot ? COLOR_HOVER
                    : COLOR_SLOT_BORDER;

            gui.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, border);
            gui.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, COLOR_SLOT);

            if (id != null) {
                SpellSlots.icon(id).render(gui, x + 2, y + 2);
            }

            gui.pose().pushPose();
            gui.pose().translate(0.0F, 0.0F, Z_OVERLAY);

            if (id != null && !data.isUsable(id) && !data.isTraining(id)) {
                gui.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, COLOR_FADE);
            }

            String number = String.valueOf(slot + 1);
            gui.drawString(font, number, x + SLOT_SIZE - font.width(number), y + SLOT_SIZE - font.lineHeight + 1,
                    COLOR_SLOT_NUMBER);

            gui.pose().popPose();
        }
    }

    private List<FormattedCharSequence> tooltip(PlayerSpellData data, ResourceLocation id, SpellNode node, int maxMana) {

        List<Component> lines = new ArrayList<>();
        SpellNodeState state = data.getState(id);

        if (state == SpellNodeState.UNDISCOVERED) {
            lines.add(Component.literal("Unknown spell").withColor(COLOR_PLACEHOLDER_TEXT));
            lines.add(Component.literal(details(node)).withStyle(ChatFormatting.DARK_GRAY));
            lines.add(Component.literal("Find its Spell Tome to discover it.").withStyle(ChatFormatting.GRAY));
            return wrap(lines);
        }

        if (state == SpellNodeState.PLACEHOLDER) {
            lines.add(Component.literal("Coming soon").withColor(COLOR_PLACEHOLDER_TEXT));
            lines.add(Component.literal("This spell isn't in the game yet.").withStyle(ChatFormatting.DARK_GRAY));
            return wrap(lines);
        }

        lines.add(Component.literal(node.displayName()).withColor(stateColor(data, id, state)));

        if (node.isRoot()) {
            lines.add(Component.literal("The root of all magic. Every spell starts here.").withStyle(ChatFormatting.GRAY));
            return wrap(lines);
        }

        lines.add(stateLine(data, id, node, state));

        SpellDefinition spell = node.spellDefinition();

        if (spell == null) {
            return wrap(lines);
        }

        PlayerSpell playerSpell = spell.player().orElseThrow();

        lines.add(Component.literal(details(node)).withStyle(ChatFormatting.DARK_GRAY));

        if (!playerSpell.description().isEmpty()) {
            lines.add(Component.literal(playerSpell.description()).withStyle(ChatFormatting.GRAY));
        }

        String unit = playerSpell.castMode() == SpellCastMode.CONTINUOUS ? " mana/s" : " mana";
        int level = data.getSpellLevel(id);
        int cost = playerSpell.proficiency().cost(playerSpell.manaCost(), level);
        MutableComponent mana = Component.literal("Cost: " + cost + unit);

        if (state != SpellNodeState.LEARNED) {
            mana.append(" (training: " + SpellTraining.trainingCost(playerSpell.manaCost()) + ")");
        } else if (cost != playerSpell.manaCost()) {
            mana.append(" (base " + playerSpell.manaCost() + ")");
        }

        lines.add(mana.withStyle(ChatFormatting.AQUA));
        lines.add(Component.literal(playerSpell.cooldownTicks() > 0
                ? "Cooldown: " + seconds(playerSpell.cooldownTicks()) + "s"
                : "No cooldown").withStyle(ChatFormatting.AQUA));

        if (state == SpellNodeState.LEARNED) {
            lines.addAll(proficiencyLines(data, id, node, playerSpell));
        }

        if (state != SpellNodeState.LEARNED) {
            int required = SpellTraining.requiredCasts(playerSpell.manaCost(), node.tier(), maxMana);
            int left = Mth.ceil((1.0F - data.getProgress(id)) * required);
            lines.add(Component.literal("Progress: " + percent(data.getProgress(id)) + " · " + left + " of " + required
                    + " training casts left").withStyle(ChatFormatting.BLUE));
        }

        lines.addAll(actions(data, id, state));

        return wrap(lines);
    }

    private static List<Component> proficiencyLines(PlayerSpellData data, ResourceLocation id, SpellNode node,
                                                    PlayerSpell playerSpell) {

        List<Component> lines = new ArrayList<>();
        int level = data.getSpellLevel(id);
        int elementLevel = data.getElementLevel(node.element());
        SpellScale scale = SpellScale.of(playerSpell, level, elementLevel);

        lines.add(Component.literal(levelText("Level", level, data.getSpellLevelProgress(id)))
                .withStyle(ChatFormatting.GOLD));

        if (data.isSpellLevelCapped(id)) {
            lines.add(Component.literal("Can't level up past " + parentName(data, node) + " (level "
                    + data.getSpellLevelCap(id) + ")").withColor(COLOR_LOCKED));
        }

        if (node.element() != SpellElement.NONE) {
            lines.add(Component.literal(levelText(node.element().displayName() + " mastery", elementLevel,
                    data.getElementLevelProgress(node.element()))).withStyle(ChatFormatting.YELLOW));
        }

        List<String> bonuses = new ArrayList<>();
        addBonus(bonuses, scale.damage(), "damage");
        addBonus(bonuses, scale.size(), "size");

        int cost = playerSpell.proficiency().cost(playerSpell.manaCost(), level);
        if (cost < playerSpell.manaCost()) {
            bonuses.add("-" + Math.round(100.0F * (playerSpell.manaCost() - cost) / playerSpell.manaCost()) + "% cost");
        }

        if (!bonuses.isEmpty()) {
            lines.add(Component.literal(String.join(" · ", bonuses)).withStyle(ChatFormatting.GREEN));
        }

        return lines;
    }

    private static String levelText(String label, int level, float progress) {
        return level >= SpellProficiency.MAX_LEVEL
                ? label + " " + level + " · Max"
                : label + " " + level + " · " + percent(progress) + " to level " + (level + 1);
    }

    private static void addBonus(List<String> bonuses, float multiplier, String label) {

        int percent = Math.round((multiplier - 1.0F) * 100.0F);

        if (percent > 0) {
            bonuses.add("+" + percent + "% " + label);
        }
    }

    private static Component stateLine(PlayerSpellData data, ResourceLocation id, SpellNode node, SpellNodeState state) {

        String parent = parentName(data, node);

        return switch (state) {
            case LEARNED -> data.isUsable(id)
                    ? Component.literal("Learned").withColor(COLOR_LEARNED)
                    : Component.literal("Learned · needs " + parent + " to be usable").withColor(COLOR_UNUSABLE);
            case TRAINING -> Component.literal("Training").withColor(COLOR_TRAINING);
            case AVAILABLE -> Component.literal("Can be learned").withColor(COLOR_AVAILABLE);
            case LOCKED -> Component.literal("Locked · learn " + parent + " first").withColor(COLOR_LOCKED);
            case UNDISCOVERED, PLACEHOLDER -> Component.literal(SpellNode.PLACEHOLDER_NAME).withColor(COLOR_PLACEHOLDER);
        };
    }

    private static String parentName(PlayerSpellData data, SpellNode node) {
        return node.parent()
                .filter(data::isDiscovered)
                .map(SpellNodeRegistry::get)
                .map(SpellNode::displayName)
                .orElse("the spell before it");
    }

    private static String details(SpellNode node) {

        List<String> parts = new ArrayList<>();

        if (node.element() != SpellElement.NONE) {
            parts.add(StringUtils.capitalize(node.element().getSerializedName()));
        }

        parts.add("Tier " + node.tier());

        if (node.branch() != SpellBranch.NONE) {
            parts.add(StringUtils.capitalize(node.branch().getSerializedName()));
        }

        return String.join(" · ", parts);
    }

    private static List<Component> actions(PlayerSpellData data, ResourceLocation id, SpellNodeState state) {

        List<Component> actions = new ArrayList<>();

        if (state == SpellNodeState.AVAILABLE) {
            actions.add(Component.literal("Click to start training"));
        } else if (state == SpellNodeState.TRAINING) {
            actions.add(Component.literal("Click to pause training (progress is kept)"));
        }

        if (data.canEquip(id)) {
            int slot = data.slotOf(id);
            actions.add(Component.literal(slot >= 0
                    ? "In slot " + (slot + 1) + " · right-click to unequip"
                    : "Right-click or drag to a slot to equip"));
        }

        actions.replaceAll(line -> line.copy().withStyle(ChatFormatting.DARK_GRAY));

        return actions;
    }

    private List<FormattedCharSequence> wrap(List<Component> lines) {

        List<FormattedCharSequence> wrapped = new ArrayList<>();

        for (Component line : lines) {
            wrapped.addAll(font.split(line, TOOLTIP_WIDTH));
        }

        return wrapped;
    }

    private static String percent(float progress) {
        return (int) (progress * 100.0F) + "%";
    }

    private static int seconds(int ticks) {
        return (ticks + 19) / 20;
    }
}
