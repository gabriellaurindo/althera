package com.darksune.althera.common.academy.teacher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record TeacherDefinition(String name, Optional<String> title, ResourceLocation texture, Vec3 position, float yaw,
                                String greeting) {

    public static final Codec<TeacherDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(TeacherDefinition::name),
            Codec.STRING.optionalFieldOf("title").forGetter(TeacherDefinition::title),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(TeacherDefinition::texture),
            Vec3.CODEC.fieldOf("position").forGetter(TeacherDefinition::position),
            Codec.FLOAT.optionalFieldOf("yaw", 0.0F).forGetter(TeacherDefinition::yaw),
            Codec.STRING.optionalFieldOf("greeting", "").forGetter(TeacherDefinition::greeting)
    ).apply(instance, TeacherDefinition::new));

    public String displayName() {
        return title.map(value -> name + ", " + value).orElse(name);
    }
}
