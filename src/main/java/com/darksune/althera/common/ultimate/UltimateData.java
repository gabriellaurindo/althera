package com.darksune.althera.common.ultimate;

import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.SyncableAttachment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class UltimateData extends SyncableAttachment {

    // =========================
    // DATA
    // =========================

    private long lastUltimateResetDay;

    private Map<ResourceLocation, Integer> activeSkills = new HashMap<>();

    private Map<ResourceLocation, Integer> cooldownSkills = new HashMap<>();

    // ultimate used since the last daily reset (day change or sleeping)
    private boolean usedToday;

    // =========================
    // CODEC (save/load)
    // =========================

    public static final Codec<Map<ResourceLocation, Integer>> SKILL_MAP_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT);

    public static final Codec<UltimateData> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            Codec.LONG.optionalFieldOf("lastUltimateResetDay", 0L).forGetter(UltimateData::getLastUltimateResetDay),

            SKILL_MAP_CODEC.optionalFieldOf("activeUltimates", new HashMap<>()).forGetter(UltimateData::getActiveSkills),

            SKILL_MAP_CODEC.optionalFieldOf("ultimateCooldowns", new HashMap<>()).forGetter(UltimateData::getCooldownSkills),

            Codec.BOOL.optionalFieldOf("usedUltimateToday", false).forGetter(UltimateData::wasUsedToday)

    ).apply(instance, (lastUltimateResetDay, activeSkills, cooldownSkills, usedToday) -> {

        UltimateData data = new UltimateData();

        data.lastUltimateResetDay = lastUltimateResetDay;

        data.activeSkills.putAll(activeSkills);

        data.cooldownSkills.putAll(cooldownSkills);

        data.usedToday = usedToday;

        return data;
    }));

    // =========================
    // STREAM (network sync)
    // =========================

    public static final StreamCodec<FriendlyByteBuf, UltimateData> STREAM_CODEC = StreamCodec.of(

            (buf, data) -> {

                buf.writeLong(data.lastUltimateResetDay);

                buf.writeMap(data.activeSkills, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeInt);

                buf.writeMap(data.cooldownSkills, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeInt);

                buf.writeBoolean(data.usedToday);
            },

            buf -> {

                UltimateData data = new UltimateData();

                data.lastUltimateResetDay = buf.readLong();

                data.activeSkills.putAll(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readInt));

                data.cooldownSkills.putAll(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readInt));

                data.usedToday = buf.readBoolean();

                return data;
            });

    // =========================
    // GET / SET
    // =========================

    public static UltimateData get(Player player) {
        return player.getData(AltheraAttachments.ULTIMATE.get());
    }

    public long getLastUltimateResetDay() {
        return lastUltimateResetDay;
    }

    public void setLastUltimateResetDay(long lastUltimateResetDay) {
        this.lastUltimateResetDay = lastUltimateResetDay;
    }

    public Map<ResourceLocation, Integer> getActiveSkills() {
        return activeSkills;
    }

    public Map<ResourceLocation, Integer> getCooldownSkills() {
        return cooldownSkills;
    }

    // =========================
    // STATE
    // =========================

    public boolean isSkillOnCooldown(ResourceLocation ultimateId) {
        return cooldownSkills.containsKey(ultimateId);
    }

    public void activateSkill(ResourceLocation ultimateId, int duration) {
        activeSkills.put(ultimateId, duration);
    }

    public void startSkillCooldown(ResourceLocation ultimateId, int cooldown) {
        cooldownSkills.put(ultimateId, cooldown);
    }

    public boolean wasUsedToday() {
        return usedToday;
    }

    public void markUsedToday() {
        usedToday = true;
        markDirty();
    }

    public void resetDaily() {

        cooldownSkills.clear();

        usedToday = false;

        markDirty();
    }

    // =========================
    // LOGIC
    // =========================

    @Override
    public void sync(ServerPlayer player) {
        player.setData(AltheraAttachments.ULTIMATE.get(), this);
        clearDirty();
    }
}
