package com.darksune.althera.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ManaData {
    private int mana = 100;
    private int maxMana = 100;

    public int getMana() { return mana; }
    public int getMaxMana() { return maxMana; }

    public void setMana(int value) {
        this.mana = Math.max(0, Math.min(value, maxMana));
    }

    public void addMana(int amount) {
        setMana(this.mana + amount);
    }

    // CODEC (save/load)
    public static final Codec<ManaData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("mana").forGetter(ManaData::getMana),
                    Codec.INT.fieldOf("maxMana").forGetter(ManaData::getMaxMana)
            ).apply(instance, (mana, max) -> {
                ManaData data = new ManaData();
                data.mana = mana;
                data.maxMana = max;
                return data;
            })
    );

    // STREAM (network sync)
    public static final StreamCodec<FriendlyByteBuf, ManaData> STREAM_CODEC =
            StreamCodec.of(
                    (buf, data) -> {
                        buf.writeInt(data.mana);
                        buf.writeInt(data.maxMana);
                    },
                    buf -> {
                        ManaData data = new ManaData();
                        data.mana = buf.readInt();
                        data.maxMana = buf.readInt();
                        return data;
                    }
            );

    // resto igual antes...
}