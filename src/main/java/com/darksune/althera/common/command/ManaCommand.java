package com.darksune.althera.common.command;

import com.darksune.althera.common.attachment.ManaData;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

public class ManaCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(AltheraCommands.root("mana"))
                        .requires(AltheraCommands::hasCheats)

                        // REFILL
                        .then(Commands.literal("refill")
                                .executes(ctx -> refillMana(
                                        ctx.getSource(),
                                        List.of(ctx.getSource().getPlayerOrException())
                                ))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> refillMana(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets")
                                        ))
                                )
                        )
        );
    }

    // =========================
    // REFILL
    // =========================

    private static int refillMana(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            ManaData mana = ManaData.get(player);
            mana.setMana(mana.getMaxMana());
            mana.sync(player);
        }

        source.sendSuccess(() -> Component.literal("§bMana restored for " + targets.size() + " player(s)."), true);
        return targets.size();
    }
}
