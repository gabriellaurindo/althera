package com.darksune.althera.common.spell;

import com.darksune.althera.common.ultimate.effect.UltimateContext;
import com.darksune.althera.common.ultimate.effect.UltimateEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record SpellDefinition(
        String name,
        double range,
        int cooldownTicks,
        int castTicks,
        List<UltimateEffect> onCast
) {

    public static final Codec<SpellDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name", "Spell").forGetter(SpellDefinition::name),
            Codec.DOUBLE.optionalFieldOf("range", 16.0D).forGetter(SpellDefinition::range),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("cooldown_ticks", 40).forGetter(SpellDefinition::cooldownTicks),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("cast_ticks", 0).forGetter(SpellDefinition::castTicks),
            UltimateEffect.CODEC.listOf().optionalFieldOf("on_cast", List.of()).forGetter(SpellDefinition::onCast)
    ).apply(instance, SpellDefinition::new));

    public void cast(UltimateContext context) {
        onCast.forEach(effect -> effect.apply(context));
    }
}
