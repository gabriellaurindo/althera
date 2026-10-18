package com.darksune.althera.common.ultimate;

import com.darksune.althera.common.ultimate.effect.UltimateContext;
import com.darksune.althera.common.ultimate.effect.UltimateEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record UltimateDefinition(
        String name,
        int manaCost,
        int channelTicks,
        int cooldownTicks,
        List<UltimateEffect> onStart,
        List<UltimateEffect> onTick,
        List<UltimateEffect> onEnd
) {

    public static final Codec<UltimateDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name", "Ultimate").forGetter(UltimateDefinition::name),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("mana_cost").forGetter(UltimateDefinition::manaCost),
            ExtraCodecs.POSITIVE_INT.fieldOf("channel_ticks").forGetter(UltimateDefinition::channelTicks),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("cooldown_ticks", 0).forGetter(UltimateDefinition::cooldownTicks),
            UltimateEffect.CODEC.listOf().optionalFieldOf("on_start", List.of()).forGetter(UltimateDefinition::onStart),
            UltimateEffect.CODEC.listOf().optionalFieldOf("on_tick", List.of()).forGetter(UltimateDefinition::onTick),
            UltimateEffect.CODEC.listOf().optionalFieldOf("on_end", List.of()).forGetter(UltimateDefinition::onEnd)
    ).apply(instance, UltimateDefinition::new));

    public void start(UltimateContext context) {
        apply(onStart, context);
    }

    public void tick(UltimateContext context) {
        apply(onTick, context);
    }

    public void end(UltimateContext context) {
        apply(onEnd, context);
    }

    private static void apply(List<UltimateEffect> effects, UltimateContext context) {
        effects.forEach(effect -> effect.apply(context));
    }
}
