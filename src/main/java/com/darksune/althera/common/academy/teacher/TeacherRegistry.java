package com.darksune.althera.common.academy.teacher;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class TeacherRegistry {

    private static final Map<ResourceLocation, TeacherDefinition> TEACHERS = new HashMap<>();

    private TeacherRegistry() {
    }

    public static void register(ResourceLocation id, TeacherDefinition teacher) {
        TEACHERS.put(id, teacher);
    }

    @Nullable
    public static TeacherDefinition get(@Nullable ResourceLocation id) {
        return id == null ? null : TEACHERS.get(id);
    }

    @Nullable
    public static TeacherDefinition byGiver(String giver) {
        return get(ResourceLocation.tryParse(giver));
    }

    public static String giverName(String giver) {
        TeacherDefinition teacher = byGiver(giver);
        return teacher == null ? giver : teacher.displayName();
    }

    public static Map<ResourceLocation, TeacherDefinition> getAll() {
        return Map.copyOf(TEACHERS);
    }

    public static void clear() {
        TEACHERS.clear();
    }
}
