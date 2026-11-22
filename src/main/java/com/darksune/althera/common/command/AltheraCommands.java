package com.darksune.althera.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Central registry for chat commands. Every root command follows the {@code /althera_<feature>} pattern.
 */
public final class AltheraCommands {

    public static final String PREFIX = "althera_";

    private AltheraCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        AcademyCommand.register(dispatcher);
        HeroCommand.register(dispatcher);
        ManaCommand.register(dispatcher);
        MageCommand.register(dispatcher);
        QuestCommand.register(dispatcher);
        SealCommand.register(dispatcher);
        SpellCommand.register(dispatcher);
    }

    static String root(String feature) {
        return PREFIX + feature;
    }

    static boolean hasCheats(CommandSourceStack source) {
        return source.hasPermission(2);
    }

    static boolean isDevWithCheats(CommandSourceStack source) {
        return !FMLEnvironment.production && hasCheats(source);
    }
}
