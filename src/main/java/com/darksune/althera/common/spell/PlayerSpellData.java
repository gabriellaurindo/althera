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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerSpellData extends SyncableAttachment {

    public static final int SLOT_COUNT = 9;

    private static final float LEARNED_EPSILON = 1.0E-4F;

    private final Map<ResourceLocation, Float> progress = new HashMap<>();

    private final Set<ResourceLocation> training = new HashSet<>();

    private final ResourceLocation[] slots = new ResourceLocation[SLOT_COUNT];

    private int selectedSlot;

    private final Map<ResourceLocation, Integer> cooldowns = new HashMap<>();

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

            Codec.intRange(0, SLOT_COUNT - 1).optionalFieldOf("selectedSlot", 0).forGetter(PlayerSpellData::getSelectedSlot)

    ).apply(instance, (progress, training, slots, selectedSlot) -> {

        PlayerSpellData data = new PlayerSpellData();

        data.progress.putAll(progress);

        data.training.addAll(training);

        slots.forEach(slot -> data.slots[slot.index()] = slot.node());

        data.selectedSlot = selectedSlot;

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

        return !wasLearned && isLearned(node);
    }

    public boolean isTraining(ResourceLocation node) {
        return training.contains(node);
    }

    public boolean startTraining(ResourceLocation node) {

        if (getState(node) != SpellNodeState.AVAILABLE) {
            return false;
        }

        training.add(node);
        markDirty();

        return true;
    }

    public void stopTraining(ResourceLocation node) {

        if (training.remove(node)) {
            markDirty();
        }
    }

    public Set<ResourceLocation> getTraining() {
        return Set.copyOf(training);
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

        if (isTraining(id)) {
            return SpellNodeState.TRAINING;
        }

        return node.parent().map(this::isLearned).orElse(false)
                ? SpellNodeState.AVAILABLE
                : SpellNodeState.LOCKED;
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
        slots[index] = node;
        markDirty();
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int index) {
        selectedSlot = Math.floorMod(index, SLOT_COUNT);
        markDirty();
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
        Arrays.fill(slots, null);
        selectedSlot = 0;
        cooldowns.clear();
        markDirty();
    }

    @Override
    public void sync(ServerPlayer player) {
        player.setData(AltheraAttachments.SPELLS.get(), this);
        clearDirty();
    }
}
