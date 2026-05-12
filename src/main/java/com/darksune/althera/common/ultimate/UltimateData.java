package com.darksune.althera.common.ultimate;

import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.SyncableAttachment;
import com.darksune.althera.common.ultimate.skill.UltimateSkillType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class UltimateData extends SyncableAttachment {

    // =========================
    // DATA
    // =========================

    private long lastUltimateResetDay;

    private Map<UltimateSkillType, Integer> activeSkills = new HashMap<>();

    private Map<UltimateSkillType, Integer> cooldownSkills = new HashMap<>();

    // =========================
    // CODEC (save/load)
    // =========================

    public static final Codec<Map<UltimateSkillType, Integer>> SKILL_MAP_CODEC = Codec.unboundedMap(UltimateSkillType.CODEC, Codec.INT);

    public static final Codec<UltimateData> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            Codec.LONG.optionalFieldOf("lastUltimateResetDay", 0L).forGetter(UltimateData::getLastUltimateResetDay),

            SKILL_MAP_CODEC.optionalFieldOf("activeSkills", new HashMap<>()).forGetter(UltimateData::getActiveSkills),

            SKILL_MAP_CODEC.optionalFieldOf("cooldownSkills", new HashMap<>()).forGetter(UltimateData::getCooldownSkills)

    ).apply(instance, (lastUltimateResetDay, activeSkills, cooldownSkills) -> {

        UltimateData data = new UltimateData();

        data.lastUltimateResetDay = lastUltimateResetDay;

        data.activeSkills.putAll(activeSkills);

        data.cooldownSkills.putAll(cooldownSkills);

        return data;
    }));

    // =========================
    // STREAM (network sync)
    // =========================

    public static final StreamCodec<FriendlyByteBuf, UltimateData> STREAM_CODEC = StreamCodec.of(

            (buf, data) -> {

                buf.writeLong(data.lastUltimateResetDay);

                // =========================
                // ACTIVE SKILLS
                // =========================

                buf.writeInt(data.activeSkills.size());

                for (Map.Entry<UltimateSkillType, Integer> entry : data.activeSkills.entrySet()) {

                    buf.writeEnum(entry.getKey());

                    buf.writeInt(entry.getValue());
                }

                // =========================
                // COOLDOWNS
                // =========================

                buf.writeInt(data.cooldownSkills.size());

                for (Map.Entry<UltimateSkillType, Integer> entry : data.cooldownSkills.entrySet()) {

                    buf.writeEnum(entry.getKey());

                    buf.writeInt(entry.getValue());
                }
            },

            buf -> {

                UltimateData data = new UltimateData();

                data.lastUltimateResetDay = buf.readLong();

                // =========================
                // ACTIVE SKILLS
                // =========================

                int activeSkillSize = buf.readInt();

                for (int i = 0; i < activeSkillSize; i++) {

                    UltimateSkillType skillType = buf.readEnum(UltimateSkillType.class);

                    int timer = buf.readInt();

                    data.activeSkills.put(skillType, timer);
                }

                // =========================
                // COOLDOWNS
                // =========================

                int cooldownSkillSize = buf.readInt();

                for (int i = 0; i < cooldownSkillSize; i++) {

                    UltimateSkillType skillType = buf.readEnum(UltimateSkillType.class);

                    int timer = buf.readInt();

                    data.cooldownSkills.put(skillType, timer);
                }

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

    public Map<UltimateSkillType, Integer> getActiveSkills() {
        return activeSkills;
    }

    public Map<UltimateSkillType, Integer> getCooldownSkills() {
        return cooldownSkills;
    }

    // =========================
    // STATE
    // =========================

    public boolean isSkillOnCooldown(UltimateSkillType skillType) {
        return cooldownSkills.containsKey(skillType);
    }

    public void activateSkill(UltimateSkillType skillType, int duration) {
        activeSkills.put(skillType, duration);
    }

    public void startSkillCooldown(UltimateSkillType skillType, int cooldown) {
        cooldownSkills.put(skillType, cooldown);
    }

    public void resetCooldowns() {

        cooldownSkills.clear();

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
