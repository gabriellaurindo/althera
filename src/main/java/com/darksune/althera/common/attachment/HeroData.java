package com.darksune.althera.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public class HeroData {

    public int health = 20;
    public int maxHealth = 20;

    // =========================
    // GET / SET (Attachment)
    // =========================

    public static HeroData get(final Player player) {
        return player.getData(AltheraAttachments.HERO.get());
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    // =========================
    // CODEC (save/load)
    // =========================

    public static final Codec<HeroData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("health").forGetter(data -> data.health),
                    Codec.INT.fieldOf("maxHealth").forGetter(data -> data.maxHealth)
            ).apply(instance, (health, maxHealth) -> {
                HeroData data = new HeroData();
                data.health = health;
                data.maxHealth = health;
                return data;
            })
    );

    // =========================
    // STREAM (network sync)
    // =========================

    public static final StreamCodec<FriendlyByteBuf, HeroData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                buf.writeInt(data.health);
                buf.writeInt(data.maxHealth);
            },
            buf -> {
                HeroData data = new HeroData();
                data.health = buf.readInt();
                data.maxHealth = buf.readInt();
                return data;
            }
    );

    // =========================
    // LOGIC
    // =========================

    public void sync(final Player player) {
        player.setData(AltheraAttachments.HERO.get(), this);
    }

    public void incrementMaxHealth(final int amount) {
        int total = getMaxHealth() + amount;
        setMaxHealth(total);
    }
}
