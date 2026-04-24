package com.darksune.althera.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ManaData {

    public int mana = 200;
    public int maxMana = 200;
    public int manaRegenerated = 0;

    public static final int MANA_REGEN = 10;

    // =========================
    // GET / SET (Attachment)
    // =========================

    public static ManaData get(final Player player) {
        return player.getData(AltheraAttachments.MANA.get());
    }

    public int getMana() {
        return this.mana;
    }

    public void setMana(int value) {
        this.mana = value; // 🔥 sync
    }

    public int getMaxMana() {
        return this.maxMana;
    }

    public void setMaxMana( int value) {
        this.maxMana = value;
    }

    public int getManaRegenerated() {
        return this.manaRegenerated;
    }

    public void setManaRegenerated(final int value) {
        this.manaRegenerated = value;
    }

    // =========================
    // CODEC (save/load)
    // =========================

    public static final Codec<ManaData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("mana").forGetter(data -> data.mana),
                    Codec.INT.fieldOf("maxMana").forGetter(data -> data.maxMana),
                    Codec.INT.fieldOf("manaRegenerated").forGetter(data -> data.manaRegenerated)
            ).apply(instance, (mana, max, regen) -> {
                ManaData data = new ManaData();
                data.mana = mana;
                data.maxMana = max;
                data.manaRegenerated = regen;
                return data;
            })
    );

    // =========================
    // STREAM (network sync)
    // =========================

    public static final StreamCodec<FriendlyByteBuf, ManaData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                buf.writeInt(data.mana);
                buf.writeInt(data.maxMana);
                buf.writeInt(data.manaRegenerated);
            },
            buf -> {
                ManaData data = new ManaData();
                data.mana = buf.readInt();
                data.maxMana = buf.readInt();
                data.manaRegenerated = buf.readInt();
                return data;
            }
    );

    // =========================
    // LOGIC
    // =========================

    public void sync(final Player player) {
        player.setData(AltheraAttachments.MANA.get(), this);
    }

    public boolean consumeMana(final Player player, int amount) {
        if (!hasEnoughMana(amount)) {
            return false;
        }

        setMana(getMana() - amount);
        sync(player);
        return true;
    }

    public void regenMana(final Player player, final Level level) {
        if (HeroData.get(player).getSummonUUID() != null) {
            return;
        }

        int mana = getMana();
        int max = getMaxMana();

        int newMana = Math.min(mana + MANA_REGEN, max);
        setMana(newMana);

        if (newMana < max) {
            incrementMaxMana(MANA_REGEN);
        }
        sync(player);
    }

    public void incrementMaxMana(final int amount) {
        int total = getManaRegenerated() + amount;
        setManaRegenerated(total);

        int levelsGained = total / 25 - (total - amount) / 25;

        if (levelsGained < 0) {
           return;
        }
        setMaxMana(getMaxMana() + levelsGained);
    }

    public boolean hasEnoughMana(int amount) {
        return this.mana >= amount;
    }
}
