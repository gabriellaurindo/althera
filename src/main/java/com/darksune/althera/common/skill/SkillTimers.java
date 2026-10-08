package com.darksune.althera.common.skill;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Generic handling of skill timers (active skills and cooldowns).
 * <p>
 * A skill's callbacks can end other skills mid-iteration
 * (e.g. a skill kills the hero -> die() -> endAll), so the live map is never iterated:
 * we walk a copy of the keys and remove each skill from the map before notifying it.
 */
public final class SkillTimers {

    private SkillTimers() {
    }

    @FunctionalInterface
    public interface TickAction<K> {
        void tick(K skill, int remainingTicks);
    }

    public static <K> void tick(final Map<K, Integer> timers, final TickAction<K> onTick, final Consumer<K> onExpire) {
        for (K skill : List.copyOf(timers.keySet())) {
            final Integer current = timers.get(skill);

            // already ended by another callback during this tick
            if (current == null) {
                continue;
            }

            final int remainingTicks = current - 1;

            if (remainingTicks <= 0) {
                timers.remove(skill);
                onExpire.accept(skill);
                continue;
            }

            timers.put(skill, remainingTicks);
            onTick.tick(skill, remainingTicks);
        }
    }

    public static <K> void endAll(final Map<K, Integer> timers, final Consumer<K> onEnd) {
        if (timers.isEmpty()) {
            return;
        }

        final List<K> ended = List.copyOf(timers.keySet());
        timers.clear();
        ended.forEach(onEnd);
    }
}
