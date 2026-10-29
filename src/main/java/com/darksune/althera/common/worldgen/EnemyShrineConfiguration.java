package com.darksune.althera.common.worldgen;

import com.darksune.althera.common.enemy.EnemyMageRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record EnemyShrineConfiguration(ResourceLocation mage) implements FeatureConfiguration {

    public static final Codec<EnemyShrineConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("mage", EnemyMageRegistry.DEFAULT_ID).forGetter(EnemyShrineConfiguration::mage)
    ).apply(instance, EnemyShrineConfiguration::new));
}
