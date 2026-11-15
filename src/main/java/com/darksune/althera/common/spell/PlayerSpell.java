package com.darksune.althera.common.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record PlayerSpell(int manaCost, String description, SpellCastMode castMode, int cooldownTicks) {

    public static final Codec<PlayerSpell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("mana_cost").forGetter(PlayerSpell::manaCost),
            Codec.STRING.optionalFieldOf("description", "").forGetter(PlayerSpell::description),
            SpellCastMode.CODEC.optionalFieldOf("cast_mode", SpellCastMode.INSTANT).forGetter(PlayerSpell::castMode),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("cooldown_ticks", 0).forGetter(PlayerSpell::cooldownTicks)
    ).apply(instance, PlayerSpell::new));
}
