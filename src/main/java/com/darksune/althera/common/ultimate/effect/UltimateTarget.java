package com.darksune.althera.common.ultimate.effect;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public sealed interface UltimateTarget {

    Codec<UltimateTarget> CODEC = Codec.either(Codec.STRING, Area.CODEC)
            .comapFlatMap(UltimateTarget::decode, UltimateTarget::encode);

    List<LivingEntity> resolve(UltimateContext context);

    record Self() implements UltimateTarget {

        @Override
        public List<LivingEntity> resolve(UltimateContext context) {
            return List.of(context.hero());
        }
    }

    record Owner() implements UltimateTarget {

        @Override
        public List<LivingEntity> resolve(UltimateContext context) {
            return List.of(context.owner());
        }
    }

    record Area(double radius) implements UltimateTarget {

        static final Codec<Area> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("area").forGetter(Area::radius)
        ).apply(instance, Area::new));

        @Override
        public List<LivingEntity> resolve(UltimateContext context) {
            return context.level().getEntitiesOfClass(
                    LivingEntity.class,
                    context.hero().getBoundingBox().inflate(radius),
                    entity -> entity != context.hero()
                            && entity != context.owner()
                            && entity.distanceToSqr(context.hero()) <= radius * radius
            );
        }
    }

    private static DataResult<UltimateTarget> decode(Either<String, Area> either) {
        return either.map(
                name -> switch (name) {
                    case "self" -> DataResult.success(new Self());
                    case "owner" -> DataResult.success(new Owner());
                    default -> DataResult.error(() -> "Unknown ultimate target: " + name);
                },
                DataResult::success
        );
    }

    private static Either<String, Area> encode(UltimateTarget target) {
        return switch (target) {
            case Self self -> Either.left("self");
            case Owner owner -> Either.left("owner");
            case Area area -> Either.right(area);
        };
    }
}
