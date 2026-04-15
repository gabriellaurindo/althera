package com.darksune.althera.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public final class AltheraEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "althera");

    public static final DeferredHolder<EntityType<?>, EntityType<LightOrbEntity>> LIGHT_ORB =
            ENTITIES.register("light_orb",
                    () -> EntityType.Builder
                            .of(LightOrbEntity::new, MobCategory.MISC)
                            .sized(0.2f, 0.2f)
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .build("light_orb"));

    public static final DeferredHolder<EntityType<?>, EntityType<SummonedZombieEntity>> SUMMONED_ZOMBIE =
            ENTITIES.register("summoned_zombie",
                    () -> EntityType.Builder
                            .of(SummonedZombieEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(8)
                            .build("summoned_zombie"));

    public static final DeferredHolder<EntityType<?>, EntityType<SummonedEntity>> SUMMONED =
            ENTITIES.register("summoned",
                    () -> EntityType.Builder
                            .of(SummonedEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.8f)
                            .clientTrackingRange(8)
                            .build("summoned"));

    public static void register(final IEventBus bus) {
        ENTITIES.register(bus);
    }
}
