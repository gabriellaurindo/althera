package com.darksune.althera.common.ultimate.effect;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public sealed interface UltimateTarget {

    Codec<UltimateTarget> CODEC = Codec.either(Codec.STRING, Codec.either(Area.CODEC, Line.CODEC))
            .comapFlatMap(UltimateTarget::decode, UltimateTarget::encode);

    List<LivingEntity> resolve(UltimateContext context);

    record Self() implements UltimateTarget {

        @Override
        public List<LivingEntity> resolve(UltimateContext context) {
            return List.of(context.caster());
        }
    }

    record Owner() implements UltimateTarget {

        @Override
        public List<LivingEntity> resolve(UltimateContext context) {
            return List.of(context.owner());
        }
    }

    record Target() implements UltimateTarget {

        @Override
        public List<LivingEntity> resolve(UltimateContext context) {
            LivingEntity target = context.target();
            return target != null && target.isAlive() ? List.of(target) : List.of();
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
                    AABB.ofSize(context.position(), 0, 0, 0).inflate(radius + 1),
                    entity -> context.canAffect(entity)
                            && entity.distanceToSqr(context.position()) <= radius * radius
            );
        }
    }

    record Line(double length, double width) implements UltimateTarget {

        static final Codec<Line> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("line").forGetter(Line::length),
                Codec.DOUBLE.optionalFieldOf("width", 1.0).forGetter(Line::width)
        ).apply(instance, Line::new));

        @Override
        public List<LivingEntity> resolve(UltimateContext context) {

            Vec3 lift = new Vec3(0, context.caster().getBbHeight() / 2, 0);
            Vec3 start = context.origin().add(lift);
            Vec3 end = context.reach(length).add(lift);

            return context.level().getEntitiesOfClass(
                    LivingEntity.class,
                    new AABB(start, end).inflate(width + 1),
                    entity -> context.canAffect(entity)
                            && distanceToSegment(entity.getBoundingBox().getCenter(), start, end)
                            <= width + entity.getBbWidth() / 2
            );
        }

        private static double distanceToSegment(Vec3 point, Vec3 start, Vec3 end) {

            Vec3 segment = end.subtract(start);
            double lengthSqr = segment.lengthSqr();

            if (lengthSqr < 1.0E-6) {
                return point.distanceTo(start);
            }

            double progress = Mth.clamp(point.subtract(start).dot(segment) / lengthSqr, 0, 1);

            return point.distanceTo(start.add(segment.scale(progress)));
        }
    }

    private static DataResult<UltimateTarget> decode(Either<String, Either<Area, Line>> either) {
        return either.map(
                name -> switch (name) {
                    case "self" -> DataResult.success(new Self());
                    case "owner" -> DataResult.success(new Owner());
                    case "target" -> DataResult.success(new Target());
                    default -> DataResult.error(() -> "Unknown ultimate target: " + name);
                },
                shape -> DataResult.success(shape.<UltimateTarget>map(area -> area, line -> line))
        );
    }

    private static Either<String, Either<Area, Line>> encode(UltimateTarget target) {
        return switch (target) {
            case Self self -> Either.left("self");
            case Owner owner -> Either.left("owner");
            case Target hit -> Either.left("target");
            case Area area -> Either.right(Either.left(area));
            case Line line -> Either.right(Either.right(line));
        };
    }
}
