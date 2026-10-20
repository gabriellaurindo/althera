package com.darksune.althera.common.ultimate.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

import java.util.List;
import java.util.Optional;

public record EntityFilter(Optional<TagKey<EntityType<?>>> entityTag, boolean hostile, boolean excludeTarget) {

    public static final EntityFilter ANY = new EntityFilter(Optional.empty(), false, false);

    public static final Codec<EntityFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("entity_tag").forGetter(EntityFilter::entityTag),
            Codec.BOOL.optionalFieldOf("hostile", false).forGetter(EntityFilter::hostile),
            Codec.BOOL.optionalFieldOf("exclude_target", false).forGetter(EntityFilter::excludeTarget)
    ).apply(instance, EntityFilter::new));

    public List<LivingEntity> resolve(UltimateTarget target, UltimateContext context) {
        List<LivingEntity> entities = target.resolve(context);
        if (this.equals(ANY)) {
            return entities;
        }
        return entities.stream().filter(entity -> test(entity, context)).toList();
    }

    private boolean test(LivingEntity entity, UltimateContext context) {
        return entityTag.map(tag -> entity.getType().is(tag)).orElse(true)
                && (!hostile || entity instanceof Enemy)
                && (!excludeTarget || entity != context.target());
    }
}
