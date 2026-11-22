package com.darksune.althera.common.spell;

import com.darksune.althera.common.ultimate.effect.UltimateContext;
import com.darksune.althera.common.ultimate.effect.UltimateEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record SpellDefinition(
        String name,
        double range,
        int cooldownTicks,
        int castTicks,
        List<UltimateEffect> onCast,
        Optional<PlayerSpell> player,
        List<UltimateEffect> onCharge
) {

    public static final int CHARGE_INTERVAL_TICKS = 4;

    public static final Codec<SpellDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name", "Spell").forGetter(SpellDefinition::name),
            Codec.DOUBLE.optionalFieldOf("range", 16.0D).forGetter(SpellDefinition::range),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("cooldown_ticks", 40).forGetter(SpellDefinition::cooldownTicks),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("cast_ticks", 0).forGetter(SpellDefinition::castTicks),
            UltimateEffect.CODEC.listOf().optionalFieldOf("on_cast", List.of()).forGetter(SpellDefinition::onCast),
            PlayerSpell.CODEC.optionalFieldOf("player").forGetter(SpellDefinition::player),
            UltimateEffect.CODEC.listOf().optionalFieldOf("on_charge", List.of()).forGetter(SpellDefinition::onCharge)
    ).apply(instance, SpellDefinition::new));

    public void cast(UltimateContext context) {
        onCast.forEach(effect -> effect.apply(context));
    }

    public void charge(UltimateContext context, int chargedTicks) {
        if (chargedTicks % CHARGE_INTERVAL_TICKS == 0) {
            onCharge.forEach(effect -> effect.apply(context));
        }
    }

    public boolean isPlayerSpell() {
        return player.isPresent();
    }
}
