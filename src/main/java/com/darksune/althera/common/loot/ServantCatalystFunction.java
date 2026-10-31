package com.darksune.althera.common.loot;

import com.darksune.althera.common.entity.EnemyMageEntity;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.item.component.AltheraDataComponents;
import com.darksune.althera.common.item.component.HeroCatalyst;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Set;

public class ServantCatalystFunction extends LootItemConditionalFunction {

    public static final MapCodec<ServantCatalystFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).apply(instance, ServantCatalystFunction::new));

    protected ServantCatalystFunction(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    public LootItemFunctionType<ServantCatalystFunction> getType() {
        return AltheraLootFunctions.SERVANT_CATALYST.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (!(context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof EnemyMageEntity mage)) {
            return ItemStack.EMPTY;
        }
        HeroDefinition servant = mage.getServantDefinition();
        if (servant == null) {
            return ItemStack.EMPTY;
        }
        stack.set(AltheraDataComponents.HERO_CATALYST.get(), new HeroCatalyst(servant.getHeroClassId(), servant.getRank()));
        return stack;
    }
}
