package com.darksune.althera.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public class HeroData {

    // =========================
    // DATA
    // =========================

    private int level = 1;
    private long xp = 0;

    private double health = 10;

    // =========================
    // GET / SET
    // =========================

    public static HeroData get(final Player player) {
        return player.getData(AltheraAttachments.HERO.get());
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(final long xp) {
        this.xp = xp;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    // =========================
    // CODEC (save/load)
    // =========================

    public static final Codec<HeroData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("level").forGetter(data -> data.level),
                    Codec.LONG.fieldOf("xp").forGetter(data -> data.xp),
                    Codec.DOUBLE.fieldOf("health").forGetter(data -> data.health)
            ).apply(instance, (level, xp, health) -> {
                HeroData data = new HeroData();
                data.level = level;
                data.xp = xp;
                data.health = health;
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
            },
            buf -> {
                HeroData data = new HeroData();
                data.level = buf.readInt();
                data.xp = buf.readLong();
                data.health = buf.readDouble();
                return data;
            }
    );

    // =========================
    // LOGIC
    // =========================

    public void sync(final Player player) {
        player.setData(AltheraAttachments.HERO.get(), this);
    }
}