package com.darksune.althera.common.attachment;

import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class HeroData {

    // =========================
    // DATA
    // =========================

    private int level = 1;
    private long xp = 0;
    private double health = 20;
    private UUID summonUUID = null;
    private int interventions = 0;
    private boolean defeated;
    private boolean isHiddenHud = false;
    private boolean isSaveDisabled = false;
    private boolean canResurrect = true;
    private ResourceLocation heroId = null;

    // =========================
    // CODEC (save/load)
    // =========================

    public static final Codec<HeroData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("level")
                            .forGetter(data -> data.level),

                    Codec.LONG.fieldOf("xp")
                            .forGetter(data -> data.xp),

                    Codec.DOUBLE.fieldOf("health")
                            .forGetter(data -> data.health),

                    Codec.STRING.optionalFieldOf("summonUUID", "")
                            .forGetter(data -> data.summonUUID != null
                                    ? data.summonUUID.toString()
                                    : ""),

                    Codec.INT.optionalFieldOf("interventions", 0)
                            .forGetter(data -> data.interventions),

                    Codec.BOOL.optionalFieldOf("defeated", false)
                            .forGetter(data -> data.defeated),

                    Codec.BOOL.optionalFieldOf("isHiddenHud", false)
                            .forGetter(data -> data.isHiddenHud),

                    Codec.BOOL.optionalFieldOf("isSaveDisabled", false)
                            .forGetter(data -> data.isSaveDisabled),

                    Codec.BOOL.optionalFieldOf("canResurrect", true)
                            .forGetter(data -> data.canResurrect),

                    Codec.STRING.optionalFieldOf("heroId", "")
                            .forGetter(data -> data.heroId != null
                                    ? data.heroId.toString()
                                    : "")

            ).apply(instance, (level, xp, health, uuidStr,
                               interventions, defeated,
                               isHiddenHud, isSaveDisabled, canResurrect,
                               heroIdStr) -> {
                HeroData data = new HeroData();

                data.level = level;
                data.xp = xp;
                data.health = health;
                data.interventions = interventions;
                data.defeated = defeated;
                data.isHiddenHud = isHiddenHud;
                data.isSaveDisabled = isSaveDisabled;
                data.canResurrect = canResurrect;

                if (!uuidStr.isEmpty()) {
                    data.summonUUID = UUID.fromString(uuidStr);
                }

                if (!heroIdStr.isEmpty()) {
                    data.heroId = ResourceLocation.parse(heroIdStr);
                }

                return data;
            })
    );

    // =========================
    // STREAM (network sync)
    // =========================

    public static final StreamCodec<FriendlyByteBuf, HeroData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {

                buf.writeInt(data.level);
                buf.writeLong(data.xp);
                buf.writeDouble(data.health);

                buf.writeBoolean(data.summonUUID != null);
                if (data.summonUUID != null) {
                    buf.writeUUID(data.summonUUID);
                }

                buf.writeInt(data.interventions);
                buf.writeBoolean(data.defeated);
                buf.writeBoolean(data.isHiddenHud);
                buf.writeBoolean(data.isSaveDisabled);
                buf.writeBoolean(data.canResurrect);

                buf.writeBoolean(data.heroId != null);
                if (data.heroId != null) {
                    buf.writeResourceLocation(data.heroId);
                }
            },
            buf -> {
                HeroData data = new HeroData();

                data.level = buf.readInt();
                data.xp = buf.readLong();
                data.health = buf.readDouble();

                if (buf.readBoolean()) {
                    data.summonUUID = buf.readUUID();
                }

                data.interventions = buf.readInt();
                data.defeated = buf.readBoolean();
                data.isHiddenHud = buf.readBoolean();
                data.isSaveDisabled = buf.readBoolean();
                data.canResurrect = buf.readBoolean();

                if (buf.readBoolean()) {
                    data.heroId = buf.readResourceLocation();
                }

                return data;
            }
    );

    // =========================
    // GET / SET
    // =========================

    public static HeroData get(Player player) {
        return player.getData(AltheraAttachments.HERO.get());
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public UUID getSummonUUID() {
        return summonUUID;
    }

    public void setSummonUUID(UUID summonUUID) {
        this.summonUUID = summonUUID;
    }

    public int getInterventions() {
        return interventions;
    }

    public void setInterventions(int interventions) {
        this.interventions = interventions;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public void setDefeated(boolean defeated) {
        this.defeated = defeated;
    }

    public boolean isHiddenHud() {
        return isHiddenHud;
    }

    public void setHiddenHud(boolean hiddenHud) {
        isHiddenHud = hiddenHud;
    }

    public boolean isSaveDisabled() {
        return isSaveDisabled;
    }

    public void setSaveDisabled(boolean saveDisabled) {
        isSaveDisabled = saveDisabled;
    }

    public boolean canResurrect() {
        return canResurrect;
    }

    public void setCanResurrect(boolean canResurrect) {
        this.canResurrect = canResurrect;
    }

    public HeroDefinition getHeroDefinition() {
        return heroId != null ? HeroRegistry.get(heroId) : null;
    }

    public void setHero(ResourceLocation heroId) {

        if (HeroRegistry.get(heroId) == null) {
            throw new IllegalArgumentException("Hero not found: " + heroId);
        }

        this.heroId = heroId;
    }

    // =========================
    // STATE
    // =========================

    public void clearSummon() {
        this.summonUUID = null;
    }

    public boolean isSummoned() {
        return summonUUID != null;
    }

    public void incrementInterventions() {
        this.interventions++;
    }

    // =========================
    // LOGIC
    // =========================

    //todo usar um sistema de dirt check no tick pra sync automatico
    public void sync(Player player) {
        player.setData(AltheraAttachments.HERO.get(), this);
    }
}
