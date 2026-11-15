package com.darksune.althera.common.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record PlayerSpell(int manaCost, String description) {

    public static final Codec<PlayerSpell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("mana_cost").forGetter(PlayerSpell::manaCost),
            Codec.STRING.optionalFieldOf("description", "").forGetter(PlayerSpell::description)
    ).apply(instance, PlayerSpell::new));
}
