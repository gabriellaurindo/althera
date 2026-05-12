package com.darksune.althera.common.commandseal;

import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.SyncableAttachment;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandSealData extends SyncableAttachment {

    // =========================
    // DATA
    // =========================

    private int color = 0xFFFFFF;
    private int commandSealCharges = 3;
    private long lastChargeResetDay;
    private Map<CommandSealSkillType, Integer> activeSkills = new HashMap<>();
    private Map<CommandSealSkillType, Integer> cooldownSkills = new HashMap<>();

    // =========================
    // CODEC (save/load)
    // =========================

    public static final Codec<Map<CommandSealSkillType, Integer>> SKILL_MAP_CODEC = Codec.unboundedMap(
            CommandSealSkillType.CODEC,
            Codec.INT
    );

    public static final Codec<CommandSealData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("color", 0xFFFFFF)
                            .forGetter(data -> data.color),

                    Codec.INT.optionalFieldOf("commandSealCharges", 3)
                            .forGetter(CommandSealData::getCommandSealCharges),

                    Codec.LONG.optionalFieldOf("lastChargeResetDay", 0L)
                            .forGetter(CommandSealData::getLastChargeResetDay),

                    SKILL_MAP_CODEC.optionalFieldOf("activeSkills", new HashMap<>())
                            .forGetter(CommandSealData::getActiveSkills),

                    SKILL_MAP_CODEC.optionalFieldOf("cooldownSkills", new HashMap<>())
                            .forGetter(CommandSealData::getCooldownSkills)

            ).apply(instance, (color, commandSealCharges, lastChargeResetDay, activeSkills, cooldownSkills) -> {

                CommandSealData data = new CommandSealData();

                data.color = color;
                data.commandSealCharges = commandSealCharges;
                data.lastChargeResetDay = lastChargeResetDay;
                data.activeSkills.putAll(activeSkills);
                data.cooldownSkills.putAll(cooldownSkills);

                return data;
            })
    );

    // =========================
    // STREAM (network sync)
    // =========================

    public static final StreamCodec<FriendlyByteBuf, CommandSealData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {

                buf.writeInt(data.color);

                buf.writeInt(data.commandSealCharges);

                buf.writeLong(data.lastChargeResetDay);

                buf.writeInt(data.activeSkills.size());

                for (Map.Entry<CommandSealSkillType, Integer> entry : data.activeSkills.entrySet()) {

                    buf.writeEnum(entry.getKey());
                    buf.writeInt(entry.getValue());
                }

                buf.writeInt(data.cooldownSkills.size());

                for (Map.Entry<CommandSealSkillType, Integer> entry : data.cooldownSkills.entrySet()) {

                    buf.writeEnum(entry.getKey());
                    buf.writeInt(entry.getValue());
                }
            },

            buf -> {

                CommandSealData data = new CommandSealData();

                data.color = buf.readInt();

                data.commandSealCharges = buf.readInt();

                data.lastChargeResetDay = buf.readLong();

                int activeSkillSize = buf.readInt();

                for (int i = 0; i < activeSkillSize; i++) {

                    CommandSealSkillType skillType = buf.readEnum(CommandSealSkillType.class);

                    int timer = buf.readInt();

                    data.activeSkills.put(skillType, timer);
                }

                int cooldownSkillSize = buf.readInt();

                for (int i = 0; i < cooldownSkillSize; i++) {

                    CommandSealSkillType skillType = buf.readEnum(CommandSealSkillType.class);

                    int timer = buf.readInt();

                    data.cooldownSkills.put(skillType, timer);
                }

                return data;
            }
    );

    // =========================
    // GET / SET
    // =========================

    public static CommandSealData get(Player player) {
        return player.getData(AltheraAttachments.COMMAND_SEAL.get());
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        markDirty();
    }

    public int getCommandSealCharges() {
        return commandSealCharges;
    }

    public void setCommandSealCharges(int commandSealCharges) {
        this.commandSealCharges = commandSealCharges;
        markDirty();
    }

    public long getLastChargeResetDay() {
        return lastChargeResetDay;
    }

    public void setLastChargeResetDay(long lastChargeResetDay) {
        this.lastChargeResetDay = lastChargeResetDay;
//        markDirty();
    }

    public Map<CommandSealSkillType, Integer> getActiveSkills() {
        return activeSkills;
    }

    public Map<CommandSealSkillType, Integer> getCooldownSkills() {
        return cooldownSkills;
    }

    // =========================
    // STATE
    // =========================

    public boolean hasCharges() {
        return commandSealCharges > 0;
    }

    public boolean consumeCharge() {
        if (!hasCharges()) {
            return false;
        }
        commandSealCharges--;
        markDirty();
        return true;
    }

    public void resetCharges() {
        commandSealCharges = 3;
        markDirty();
    }

    public boolean isSkillOnCooldown(CommandSealSkillType skillType) {
        return cooldownSkills.containsKey(skillType);
    }

    public void activateSkill(CommandSealSkillType skillType, int duration) {
        activeSkills.put(skillType, duration);
    }

    public void startSkillCooldown(CommandSealSkillType skillType, int cooldown) {
        cooldownSkills.put(skillType, cooldown);
    }

    // =========================
    // LOGIC
    // =========================

    @Override
    public void sync(ServerPlayer player) {
        player.setData(AltheraAttachments.COMMAND_SEAL.get(), this);
        clearDirty();
    }
}
