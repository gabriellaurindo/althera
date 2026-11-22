package com.darksune.althera.common.academy;

import com.darksune.althera.Althera;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.TrainingDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Althera.MOD_ID)
public final class AcademyDummies {

    private static final int CHECK_INTERVAL_TICKS = 40;

    private static final double MAX_DRIFT = 0.5D;

    private static final float YAW = 180.0F;

    private static final int[] OFFSETS = {-3, 0, 3};

    private static final List<TrainingDummyEntity> DUMMIES = new ArrayList<>();

    private AcademyDummies() {
    }

    private static List<Vec3> positions() {

        int cx = AcademyLayout.CENTER_X + AcademyLayout.YARD_GRID_X * AcademyLayout.ROOM_SPACING;
        int cz = AcademyLayout.CENTER_Z + AcademyLayout.YARD_GRID_Z * AcademyLayout.ROOM_SPACING;
        List<Vec3> positions = new ArrayList<>();

        for (int offset : OFFSETS) {
            positions.add(new Vec3(cx + offset + 0.5D, AcademyLayout.SURFACE_Y + 1, cz + 0.5D));
        }

        return positions;
    }

    public static boolean isTracked(TrainingDummyEntity dummy) {
        return DUMMIES.contains(dummy);
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

        List<Vec3> positions = positions();

        while (DUMMIES.size() < positions.size()) {
            DUMMIES.add(null);
        }

        for (int i = 0; i < positions.size(); i++) {

            Vec3 home = positions.get(i);
            TrainingDummyEntity dummy = DUMMIES.get(i);

            if (dummy != null && !dummy.isRemoved()) {
                if (dummy.position().distanceTo(home) > MAX_DRIFT) {
                    dummy.teleportTo(home.x, home.y, home.z);
                }
                continue;
            }

            if (!level.isLoaded(BlockPos.containing(home))) {
                continue;
            }

            dummy = AltheraEntities.TRAINING_DUMMY.get().create(level);

            if (dummy == null) {
                continue;
            }

            dummy.setManaged(true);
            dummy.moveTo(home.x, home.y, home.z, YAW, 0.0F);
            dummy.setYHeadRot(YAW);
            dummy.setYBodyRot(YAW);
            DUMMIES.set(i, dummy);
            level.addFreshEntity(dummy);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        DUMMIES.clear();
    }
}
