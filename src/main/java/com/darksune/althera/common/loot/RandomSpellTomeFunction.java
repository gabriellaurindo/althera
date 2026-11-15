package com.darksune.althera.common.loot;

import com.darksune.althera.common.item.SpellTomeItem;
import com.darksune.althera.common.item.component.AltheraDataComponents;
import com.darksune.althera.common.item.component.SpellTome;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class RandomSpellTomeFunction extends LootItemConditionalFunction {

    public static final MapCodec<RandomSpellTomeFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance)
                    .and(ExtraCodecs.POSITIVE_INT.optionalFieldOf("tier", 1).forGetter(function -> function.tier))
                    .apply(instance, RandomSpellTomeFunction::new));

    private final int tier;

    protected RandomSpellTomeFunction(List<LootItemCondition> conditions, int tier) {
        super(conditions);
        this.tier = tier;
    }

    @Override
    public LootItemFunctionType<RandomSpellTomeFunction> getType() {
        return AltheraLootFunctions.RANDOM_SPELL_TOME.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {

        List<ResourceLocation> candidates = SpellTomeItem.trainableNodes().stream()
                .filter(id -> SpellNodeRegistry.get(id).tier() == tier)
                .toList();

        if (candidates.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ResourceLocation nodeId = candidates.get(context.getRandom().nextInt(candidates.size()));
        stack.set(AltheraDataComponents.SPELL_TOME.get(), new SpellTome(nodeId));

        return stack;
    }
}
