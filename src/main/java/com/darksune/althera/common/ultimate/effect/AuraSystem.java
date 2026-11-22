package com.darksune.althera.common.ultimate.effect;

import com.darksune.althera.Althera;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = Althera.MOD_ID)
public final class AuraSystem {

    private static final int MAX_AURAS = 512;

    private static final List<Aura> AURAS = new ArrayList<>();

    private AuraSystem() {
    }

    private static final class Aura {

        private final LivingEntity entity;

        private final AuraEffect effect;

        private final double radius;

        private final int count;

        private int age;

        private Aura(LivingEntity entity, AuraEffect effect, double radius, int count) {
            this.entity = entity;
            this.effect = effect;
            this.radius = radius;
            this.count = count;
        }

        private boolean expired() {
            return age >= effect.duration() || entity.isRemoved() || !entity.isAlive()
                    || effect.whileEffect().filter(mobEffect -> !entity.hasEffect(mobEffect)).isPresent();
        }
    }

    public static void start(LivingEntity entity, AuraEffect effect, double radius, int count) {
        if (entity.level() instanceof ServerLevel && AURAS.size() < MAX_AURAS) {
            AURAS.add(new Aura(entity, effect, radius, count));
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {

        Iterator<Aura> iterator = AURAS.iterator();

        while (iterator.hasNext()) {

            Aura aura = iterator.next();

            if (aura.expired()) {
                iterator.remove();
                continue;
            }

            if (aura.age % aura.effect.interval() == 0 && aura.entity.level() instanceof ServerLevel level) {
                spawn(level, aura);
            }

            aura.age++;
        }
    }

    private static void spawn(ServerLevel level, Aura aura) {

        AuraEffect effect = aura.effect;
        double base = aura.age * effect.turn();

        for (int i = 0; i < aura.count; i++) {

            double angle = base + Mth.TWO_PI * i / aura.count;
            double x = aura.entity.getX() + Math.cos(angle) * aura.radius;
            double y = aura.entity.getY() + effect.height() + Math.sin(angle * 2.0D + aura.age * 0.1D) * effect.bob();
            double z = aura.entity.getZ() + Math.sin(angle) * aura.radius;

            level.sendParticles(effect.particle(), x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        AURAS.clear();
    }
}
