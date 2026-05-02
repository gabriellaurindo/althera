package com.darksune.althera.common.system;

import java.util.concurrent.ThreadLocalRandom;

public class HeroClassSystem {

    public static HeroClass getRandomClass() {
        final HeroClass[] values = HeroClass.values();
        final int index = ThreadLocalRandom.current().nextInt(values.length);
        return values[index];
    }
}
