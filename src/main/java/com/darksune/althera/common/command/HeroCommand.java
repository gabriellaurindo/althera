package com.darksune.althera.common.command;

import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRank;
import com.darksune.althera.common.hero.HeroRegistry;
import com.darksune.althera.common.system.HeroRollSystem;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.system.HeroSummonSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HeroCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(AltheraCommands.root("hero"))

                        // RANDOM
                        .then(Commands.literal("summon")
                                .requires(AltheraCommands::hasCheats)
                                .executes(ctx -> summonRandom(ctx.getSource()))
                        )

                        // DIVINE
                        // todo disabled: with HeroRank limited to EX no divine hero can load (see KNOWN_ISSUES.md)
//                        .then(Commands.literal("divine")
//                                .requires(AltheraCommands::hasCheats)
//                                .executes(ctx -> summonDivine(ctx.getSource()))
//                        )

                        // BY RANK
                        .then(Commands.literal("rank")
                                .requires(AltheraCommands::hasCheats)
                                .then(Commands.argument("rank", StringArgumentType.word())
                                        .executes(ctx -> summonByRank(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "rank")
                                        ))
                                )
                        )

                        // UNSTUCK (players can use it on themselves to unstick their summon)
                        .then(Commands.literal("unstuck")
                                .executes(ctx -> unstuckHero(
                                        ctx.getSource(),
                                        ctx.getSource().getPlayerOrException()
                                ))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .requires(AltheraCommands::hasCheats)
                                        .executes(ctx -> unstuckHero(
                                                ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "target")
                                        ))
                                )
                        )
        );
    }

    // =========================
    // UNSTUCK
    // =========================

    private static int unstuckHero(CommandSourceStack source, ServerPlayer player) {
        if (HeroData.get(player).getHeroDefinition() == null) {
            source.sendFailure(Component.literal("§c" + player.getName().getString() + " has no hero."));
            return 0;
        }

        HeroSummonSystem.unstuckSummon(player);

        source.sendSuccess(() -> Component.literal("§eHero unstuck for " + player.getName().getString() + "."), true);
        return 1;
    }

    // =========================
    // RANDOM
    // =========================

    private static int summonRandom(CommandSourceStack source) throws CommandSyntaxException {
        Player player = source.getPlayerOrException();

        HeroDefinition def = HeroRollSystem.rollHero();

        HeroData data = HeroData.get(player);
        data.setHero(def.getId());
        data.setHealth(HeroStatsSystem.getMaxHealth(data));
        data.sync(player);

        source.sendSuccess(() -> Component.literal("§aRandom hero assigned."), false);
        return 1;
    }

//    // =========================
//    // DIVINE
//    // =========================

//    private static int summonDivine(CommandSourceStack source) throws CommandSyntaxException {
//        Player player = source.getPlayerOrException();

//        List<HeroDefinition> pool = HeroRegistry.getAll().stream()
//                .filter(h -> h.getNature().isDivine())
//                .toList();

//        if (pool.isEmpty()) {
//            source.sendFailure(Component.literal("§cNo divine heroes registered."));
//            return 0;
//        }

//        HeroDefinition def = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));

//        HeroData data = HeroData.get(player);
//        data.setHero(def.getId());
//        data.setHealth(HeroStatsSystem.getMaxHealth(data));
//        data.sync(player);

//        source.sendSuccess(() -> Component.literal("§dDivine hero granted."), false);
//        return 1;
//    }

    // =========================
    // BY RANK
    // =========================

    private static int summonByRank(CommandSourceStack source, String rankStr) throws CommandSyntaxException {
        Player player = source.getPlayerOrException();

        HeroRank rank;
        try {
            rank = HeroRank.valueOf(rankStr.toUpperCase());
        } catch (Exception e) {
            source.sendFailure(Component.literal("§cInvalid rank."));
            return 0;
        }

        List<HeroDefinition> pool = HeroRegistry.getAll().stream()
                .filter(h -> h.getRank() == rank)
                .toList();

        if (pool.isEmpty()) {
            source.sendFailure(Component.literal("§cNo heroes with that rank."));
            return 0;
        }

        HeroDefinition def = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));

        HeroData data = HeroData.get(player);
        data.setHero(def.getId());
        data.setHealth(HeroStatsSystem.getMaxHealth(data));
        data.sync(player);

        source.sendSuccess(() -> Component.literal("§aHero granted (" + rank + ")"), false);
        return 1;
    }
}