package com.darksune.althera.common.attachment;

import com.darksune.althera.common.entity.HeroEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class HeroData {

    public double health = 20;
    public double maxHealth = 20;

    // =========================
    // GET / SET (Attachment)
    // =========================

    public static HeroData get(final Player player) {
        return player.getData(AltheraAttachments.HERO.get());
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    // =========================
    // CODEC (save/load)
    // =========================

    public static final Codec<HeroData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("health").forGetter(data -> data.health),
                    Codec.DOUBLE.fieldOf("maxHealth").forGetter(data -> data.maxHealth)
            ).apply(instance, (health, maxHealth) -> {
                HeroData data = new HeroData();
                data.health = health;
                data.maxHealth = maxHealth;
                return data;
            })
    );

    // =========================
    // STREAM (network sync)
    // =========================

    public static final StreamCodec<FriendlyByteBuf, HeroData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                buf.writeDouble(data.health);
                buf.writeDouble(data.maxHealth);
            },
            buf -> {
                HeroData data = new HeroData();
                data.health = buf.readDouble();
                data.maxHealth = buf.readDouble();
                return data;
            }
    );

    // =========================
    // LOGIC
    // =========================

    public void sync(final Player player) {
        player.setData(AltheraAttachments.HERO.get(), this);
    }

    public void incrementMaxHealth(final double amount, final Player player) {
        double total = getMaxHealth() + amount;
        setMaxHealth(total);
        //TODO: Criar um esquema de dirty aqui + async direto no tick do player, colocar esse metodo em uma interface e implementar em todos os attachments
        sync(player);
    }

    public void setAttributes(final HeroEntity hero, final Player player) {
        hero.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        hero.setHealth((float) health);
        hero.setOwner(player.getUUID());
    }
}
