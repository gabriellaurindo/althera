package com.darksune.althera.common.academy.teacher;

import com.darksune.althera.Althera;
import com.darksune.althera.common.academy.Academy;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.TeacherEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Althera.MOD_ID)
public final class AcademyTeachers {

    private static final int CHECK_INTERVAL_TICKS = 40;

    private static final double MAX_DRIFT = 0.5D;

    private static final Map<ResourceLocation, TeacherEntity> TEACHERS = new HashMap<>();

    private AcademyTeachers() {
    }

    public static boolean isTracked(TeacherEntity entity) {
        ResourceLocation id = entity.getTeacherId();
        return id != null && TEACHERS.get(id) == entity;
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {

        if (event.getServer().getTickCount() % CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        ServerLevel level = Academy.level(event.getServer());

        if (level == null || level.players().isEmpty()) {
            return;
        }

        TEACHERS.entrySet().removeIf(entry -> {
            if (TeacherRegistry.get(entry.getKey()) == null) {
                entry.getValue().discard();
                return true;
            }
            return entry.getValue().isRemoved();
        });

        TeacherRegistry.getAll().forEach((id, definition) -> keep(level, id, definition));
    }

    private static void keep(ServerLevel level, ResourceLocation id, TeacherDefinition definition) {

        Vec3 home = definition.position();
        TeacherEntity teacher = TEACHERS.get(id);

        if (teacher == null) {

            if (!level.isLoaded(BlockPos.containing(home))) {
                return;
            }

            teacher = AltheraEntities.TEACHER.get().create(level);

            if (teacher == null) {
                return;
            }

            teacher.moveTo(home.x, home.y, home.z, definition.yaw(), 0.0F);
            teacher.setYHeadRot(definition.yaw());
            teacher.setYBodyRot(definition.yaw());
            teacher.setTeacher(id, definition);
            TEACHERS.put(id, teacher);
            level.addFreshEntity(teacher);
            return;
        }

        teacher.setTeacher(id, definition);

        if (teacher.position().distanceTo(home) > MAX_DRIFT) {
            teacher.teleportTo(home.x, home.y, home.z);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        TEACHERS.clear();
    }
}
