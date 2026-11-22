package com.darksune.althera.common.spell;

import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.SyncableAttachment;
import com.darksune.althera.common.skill.SkillTimers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerSpellData extends SyncableAttachment {

    public static final int SLOT_COUNT = 9;

    private static final float LEARNED_EPSILON = 1.0E-4F;

    private final Map<ResourceLocation, Float> progress = new HashMap<>();

    private final Set<ResourceLocation> training = new HashSet<>();

    private final Set<ResourceLocation> discovered = new HashSet<>();

    private final Set<ResourceLocation> fresh = new HashSet<>();

    private final ResourceLocation[] slots = new ResourceLocation[SLOT_COUNT];

    private int selectedSlot;

    private final Map<ResourceLocation, Integer> cooldowns = new HashMap<>();

    private final Map<ResourceLocation, Integer> spellXp = new HashMap<>();

    private final Map<SpellElement, Integer> elementXp = new EnumMap<>(SpellElement.class);

    public record LevelUps(int spellLevel, int elementLevel) {

        public static final LevelUps NONE = new LevelUps(0, 0);

        public boolean any() {
            return spellLevel > 0 || elementLevel > 0;
        }
    }

    private record Slot(int index, ResourceLocation node) {

        private static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, SLOT_COUNT - 1).fieldOf("index").forGetter(Slot::index),
                ResourceLocation.CODEC.fieldOf("node").forGetter(Slot::node)
        ).apply(instance, Slot::new));
    }

    public static final Codec<PlayerSpellData> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT).optionalFieldOf("progress", Map.of()).forGetter(data -> data.progress),

            ResourceLocation.CODEC.listOf().optionalFieldOf("training", List.of()).forGetter(data -> List.copyOf(data.training)),

            Slot.CODEC.listOf().optionalFieldOf("slots", List.of()).forGetter(PlayerSpellData::slotList),

            Codec.intRange(0, SLOT_COUNT - 1).optionalFieldOf("selectedSlot", 0).forGetter(PlayerSpellData::getSelectedSlot),

            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).optionalFieldOf("spellXp", Map.of()).forGetter(data -> data.spellXp),

            Codec.unboundedMap(SpellElement.CODEC, Codec.INT).optionalFieldOf("elementXp", Map.of()).forGetter(data -> data.elementXp),

            ResourceLocation.CODEC.listOf().optionalFieldOf("discovered", List.of()).forGetter(data -> List.copyOf(data.discovered)),

            ResourceLocation.CODEC.listOf().optionalFieldOf("fresh", List.of()).forGetter(data -> List.copyOf(data.fresh))

    ).apply(instance, (progress, training, slots, selectedSlot, spellXp, elementXp, discovered, fresh) -> {

        PlayerSpellData data = new PlayerSpellData();

        data.progress.putAll(progress);

        data.training.addAll(training);

        slots.forEach(slot -> data.slots[slot.index()] = slot.node());

        data.selectedSlot = selectedSlot;

        data.spellXp.putAll(spellXp);

        data.elementXp.putAll(elementXp);

        data.discovered.addAll(discovered);

        data.fresh.addAll(fresh);

        return data;
    }));

    public static final StreamCodec<FriendlyByteBuf, PlayerSpellData> STREAM_CODEC = StreamCodec.of(

            (buf, data) -> {

                buf.writeMap(data.progress, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeFloat);

                buf.writeCollection(data.training, FriendlyByteBuf::writeResourceLocation);

                for (ResourceLocation slot : data.slots) {
                    buf.writeNullable(slot, FriendlyByteBuf::writeResourceLocation);
                }

                buf.writeVarInt(data.selectedSlot);

                buf.writeMap(data.cooldowns, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeVarInt);

                buf.writeMap(data.spellXp, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeVarInt);

                buf.writeMap(data.elementXp, FriendlyByteBuf::writeEnum, FriendlyByteBuf::writeVarInt);

                buf.writeCollection(data.discovered, FriendlyByteBuf::writeResourceLocation);

                buf.writeCollection(data.fresh, FriendlyByteBuf::writeResourceLocation);
            },

            buf -> {

                PlayerSpellData data = new PlayerSpellData();

                data.progress.putAll(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readFloat));

                data.training.addAll(buf.readList(FriendlyByteBuf::readResourceLocation));

                for (int i = 0; i < SLOT_COUNT; i++) {
                    data.slots[i] = buf.readNullable(FriendlyByteBuf::readResourceLocation);
                }

                data.selectedSlot = buf.readVarInt();

                data.cooldowns.putAll(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readVarInt));

                data.spellXp.putAll(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readVarInt));

                data.elementXp.putAll(buf.readMap(b -> b.readEnum(SpellElement.class), FriendlyByteBuf::readVarInt));

                data.discovered.addAll(buf.readList(FriendlyByteBuf::readResourceLocation));

                data.fresh.addAll(buf.readList(FriendlyByteBuf::readResourceLocation));

                return data;
            });

    public static PlayerSpellData get(Player player) {
        return player.getData(AltheraAttachments.SPELLS.get());
    }

    public float getProgress(ResourceLocation node) {
        return progress.getOrDefault(node, 0.0F);
    }

    public boolean addProgress(ResourceLocation node, float amount) {
        return setProgress(node, getProgress(node) + amount);
    }

    public boolean setProgress(ResourceLocation node, float value) {

        boolean wasLearned = isLearned(node);

        float clamped = Mth.clamp(value, 0.0F, 1.0F);

        if (clamped >= 1.0F - LEARNED_EPSILON) {
            clamped = 1.0F;
        }

        if (clamped <= 0.0F) {
            progress.remove(node);
        } else {
            progress.put(node, clamped);
        }

        if (clamped >= 1.0F) {
            training.remove(node);
        }

        markDirty();

        boolean learnedNow = !wasLearned && isLearned(node);

        if (learnedNow) {
            equip(node);
        }

        return learnedNow;
    }

    public boolean isTraining(ResourceLocation node) {
        return training.contains(node);
    }

    public boolean startTraining(ResourceLocation node) {

        if (getState(node) != SpellNodeState.AVAILABLE) {
            return false;
        }

        training.add(node);
        equip(node);
        markDirty();

        return true;
    }

    public void stopTraining(ResourceLocation node) {

        if (training.remove(node)) {
            unequip(node);
            markDirty();
        }
    }

    public Set<ResourceLocation> getTraining() {
        return Set.copyOf(training);
    }

    public boolean isDiscovered(ResourceLocation id) {

        SpellNode node = SpellNodeRegistry.get(id);

        if (node != null && node.isRoot()) {
            return true;
        }

        return discovered.contains(id) || getProgress(id) > 0.0F || training.contains(id);
    }

    public Map<ResourceLocation, SpellNode> visibleNodes() {

        Map<ResourceLocation, SpellNode> all = SpellNodeRegistry.getAll();
        Set<ResourceLocation> visible = new HashSet<>();

        all.forEach((id, node) -> {

            if (node.isRoot() || node.parent().map(all::get).filter(parent -> parent.isRoot()).isPresent()
                    || node.parent().filter(this::isDiscovered).isPresent()) {
                visible.add(id);
            }

            if (isDiscovered(id)) {
                ResourceLocation current = id;
                int steps = all.size();

                while (current != null && steps-- >= 0 && all.containsKey(current)) {
                    visible.add(current);
                    current = all.get(current).parent().orElse(null);
                }
            }
        });

        Map<ResourceLocation, SpellNode> result = new LinkedHashMap<>();
        all.forEach((id, node) -> {
            if (visible.contains(id)) {
                result.put(id, node);
            }
        });

        return result;
    }

    public boolean discover(ResourceLocation id) {

        if (isDiscovered(id)) {
            return false;
        }

        discovered.add(id);
        fresh.add(id);

        SpellNodeRegistry.getAll().forEach((child, node) -> {
            if (node.parent().filter(id::equals).isPresent()) {
                fresh.add(child);
            }
        });

        markDirty();

        return true;
    }

    public boolean hasDiscoveredSpell() {
        return SpellNodeRegistry.getAll().entrySet().stream()
                .anyMatch(entry -> entry.getValue().isTrainable() && isDiscovered(entry.getKey()));
    }

    public Set<ResourceLocation> getFresh() {
        return Set.copyOf(fresh);
    }

    public void clearFresh() {
        if (!fresh.isEmpty()) {
            fresh.clear();
            markDirty();
        }
    }

    public void forget(ResourceLocation id) {

        stopTraining(id);
        unequip(id);
        discovered.remove(id);
        fresh.remove(id);
        setProgress(id, 0.0F);
    }

    public boolean isLearned(ResourceLocation id) {

        SpellNode node = SpellNodeRegistry.get(id);

        if (node == null) {
            return false;
        }

        if (node.isRoot()) {
            return true;
        }

        return node.isTrainable() && getProgress(id) >= 1.0F;
    }

    public boolean isUsable(ResourceLocation id) {

        ResourceLocation current = id;

        int steps = SpellNodeRegistry.getAll().size();

        while (current != null && steps-- >= 0) {

            if (!isLearned(current)) {
                return false;
            }

            current = SpellNodeRegistry.get(current).parent().orElse(null);
        }

        return current == null;
    }

    public SpellNodeState getState(ResourceLocation id) {

        SpellNode node = SpellNodeRegistry.get(id);

        if (node == null) {
            return SpellNodeState.LOCKED;
        }

        if (!node.isRoot() && !node.isTrainable()) {
            return SpellNodeState.PLACEHOLDER;
        }

        if (isLearned(id)) {
            return SpellNodeState.LEARNED;
        }

        if (!isDiscovered(id)) {
            return SpellNodeState.UNDISCOVERED;
        }

        if (isTraining(id)) {
            return SpellNodeState.TRAINING;
        }

        return node.parent().map(this::isLearned).orElse(false)
                ? SpellNodeState.AVAILABLE
                : SpellNodeState.LOCKED;
    }

    public int getSpellXp(ResourceLocation node) {
        return spellXp.getOrDefault(node, 0);
    }

    public int getSpellLevel(ResourceLocation node) {
        return SpellProficiency.levelFor(cappedXp(node), SpellProficiency.SPELL_BASE_XP);
    }

    public float getSpellLevelProgress(ResourceLocation node) {
        return SpellProficiency.progressInLevel(cappedXp(node), SpellProficiency.SPELL_BASE_XP);
    }

    public int getSpellLevelCap(ResourceLocation node) {
        return levelCap(node, SpellNodeRegistry.getAll().size());
    }

    public boolean isSpellLevelCapped(ResourceLocation node) {
        int cap = getSpellLevelCap(node);
        return cap < SpellProficiency.MAX_LEVEL && getSpellLevel(node) >= cap;
    }

    private int levelCap(ResourceLocation id, int depth) {

        SpellNode node = SpellNodeRegistry.get(id);

        if (node == null || depth <= 0) {
            return SpellProficiency.MAX_LEVEL;
        }

        return node.parent()
                .filter(parent -> {
                    SpellNode parentNode = SpellNodeRegistry.get(parent);
                    return parentNode != null && parentNode.isTrainable();
                })
                .map(parent -> Math.min(levelCap(parent, depth - 1),
                        SpellProficiency.levelFor(getSpellXp(parent), SpellProficiency.SPELL_BASE_XP)))
                .orElse(SpellProficiency.MAX_LEVEL);
    }

    private int xpCap(ResourceLocation node) {

        int cap = getSpellLevelCap(node);

        return cap >= SpellProficiency.MAX_LEVEL
                ? SpellProficiency.maxXp(SpellProficiency.SPELL_BASE_XP)
                : SpellProficiency.totalXpFor(cap, SpellProficiency.SPELL_BASE_XP);
    }

    private int cappedXp(ResourceLocation node) {
        return Math.min(getSpellXp(node), xpCap(node));
    }

    public int getElementXp(SpellElement element) {
        return elementXp.getOrDefault(element, 0);
    }

    public int getElementLevel(SpellElement element) {
        return SpellProficiency.levelFor(getElementXp(element), SpellProficiency.ELEMENT_BASE_XP);
    }

    public float getElementLevelProgress(SpellElement element) {
        return SpellProficiency.progressInLevel(getElementXp(element), SpellProficiency.ELEMENT_BASE_XP);
    }

    public LevelUps addXp(ResourceLocation node, SpellElement element, int amount) {

        if (amount <= 0) {
            return LevelUps.NONE;
        }

        int spellBefore = getSpellLevel(node);
        int xp = getSpellXp(node);
        int cap = xpCap(node);

        if (xp < cap) {
            setSpellXp(node, Math.min(xp + amount, cap));
        }

        int spellAfter = getSpellLevel(node);

        int elementAfter = 0;

        if (element != SpellElement.NONE) {
            int elementBefore = getElementLevel(element);
            setElementXp(element, getElementXp(element) + amount);
            elementAfter = getElementLevel(element) > elementBefore ? getElementLevel(element) : 0;
        }

        return new LevelUps(spellAfter > spellBefore ? spellAfter : 0, elementAfter);
    }

    public void setSpellXp(ResourceLocation node, int xp) {

        int clamped = Mth.clamp(xp, 0, SpellProficiency.maxXp(SpellProficiency.SPELL_BASE_XP));

        if (clamped == 0) {
            spellXp.remove(node);
        } else {
            spellXp.put(node, clamped);
        }

        markDirty();
    }

    public void setElementXp(SpellElement element, int xp) {

        if (element == SpellElement.NONE) {
            return;
        }

        int clamped = Mth.clamp(xp, 0, SpellProficiency.maxXp(SpellProficiency.ELEMENT_BASE_XP));

        if (clamped == 0) {
            elementXp.remove(element);
        } else {
            elementXp.put(element, clamped);
        }

        markDirty();
    }

    public boolean isOnCooldown(ResourceLocation node) {
        return cooldowns.containsKey(node);
    }

    public int getCooldown(ResourceLocation node) {
        return cooldowns.getOrDefault(node, 0);
    }

    public void startCooldown(ResourceLocation node, int ticks) {
        cooldowns.put(node, ticks);
        markDirty();
    }

    public void tickCooldowns() {
        SkillTimers.tick(
                cooldowns,
                (node, remaining) -> SkillTimers.markDirtyOnSyncTick(this, remaining),
                node -> markDirty()
        );
    }

    @Nullable
    public ResourceLocation getSlot(int index) {
        return slots[index];
    }

    public void setSlot(int index, @Nullable ResourceLocation node) {

        if (node != null) {
            unequip(node);
        }

        slots[index] = node;
        markDirty();
    }

    public boolean canEquip(ResourceLocation id) {

        SpellNode node = SpellNodeRegistry.get(id);

        return node != null && node.isTrainable() && (isLearned(id) || isTraining(id));
    }

    public int slotOf(ResourceLocation node) {

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (node.equals(slots[i])) {
                return i;
            }
        }

        return -1;
    }

    public boolean equip(ResourceLocation node) {

        if (slotOf(node) >= 0) {
            return true;
        }

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (slots[i] == null) {
                slots[i] = node;
                markDirty();
                return true;
            }
        }

        return false;
    }

    public void unequip(ResourceLocation node) {

        int index = slotOf(node);

        if (index >= 0) {
            slots[index] = null;
            markDirty();
        }
    }

    @Nullable
    public ResourceLocation getSelectedNode() {
        return slots[selectedSlot];
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int index) {
        selectedSlot = Math.floorMod(index, SLOT_COUNT);
        markDirty();
    }

    public int nextFilledSlot(int direction) {

        for (int step = 1; step <= SLOT_COUNT; step++) {

            int index = Math.floorMod(selectedSlot + step * direction, SLOT_COUNT);

            if (slots[index] != null) {
                return index;
            }
        }

        return selectedSlot;
    }

    private List<Slot> slotList() {

        List<Slot> list = new ArrayList<>();

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (slots[i] != null) {
                list.add(new Slot(i, slots[i]));
            }
        }

        return list;
    }

    public void reset() {
        progress.clear();
        training.clear();
        discovered.clear();
        fresh.clear();
        Arrays.fill(slots, null);
        selectedSlot = 0;
        cooldowns.clear();
        spellXp.clear();
        elementXp.clear();
        markDirty();
    }

    @Override
    public void sync(ServerPlayer player) {
        player.setData(AltheraAttachments.SPELLS.get(), this);
        clearDirty();
    }
}
