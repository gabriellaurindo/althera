package com.darksune.althera.common.command;

import com.darksune.althera.common.academy.Academy;
import com.darksune.althera.common.academy.AcademyBuilder;
import com.darksune.althera.common.academy.AcademyData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class AcademyCommand {

    @FunctionalInterface
    private interface PlayerAction {
        int run(CommandContext<CommandSourceStack> ctx, ServerPlayer player) throws CommandSyntaxException;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(AltheraCommands.root("academy"))
                        .requires(AltheraCommands::hasCheats)
                        .then(withTarget(Commands.literal("tp"), AcademyCommand::teleport))
                        .then(withTarget(Commands.literal("return"), AcademyCommand::sendHome))
                        .then(withTarget(Commands.literal("invite"), (ctx, player) -> setInvited(ctx, player, true)))
                        .then(withTarget(Commands.literal("uninvite"), (ctx, player) -> setInvited(ctx, player, false)))
                        .then(Commands.literal("rebuild").executes(AcademyCommand::rebuild))
        );
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T withTarget(T builder, PlayerAction action) {
        return builder
                .executes(ctx -> action.run(ctx, ctx.getSource().getPlayerOrException()))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> action.run(ctx, EntityArgument.getPlayer(ctx, "target"))));
    }

    private static int teleport(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        if (!Academy.sendToAcademy(player)) {
            ctx.getSource().sendFailure(Component.literal("§cThe academy dimension is not loaded."));
            return 0;
        }

        ctx.getSource().sendSuccess(() -> Component.literal("§dSent " + player.getGameProfile().getName() + " to the "
                + Academy.NAME + "."), true);

        return 1;
    }

    private static int sendHome(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        Academy.sendHome(player);

        ctx.getSource().sendSuccess(() -> Component.literal("§dSent " + player.getGameProfile().getName()
                + " back to the world spawn."), true);

        return 1;
    }

    private static int setInvited(CommandContext<CommandSourceStack> ctx, ServerPlayer player, boolean invited) {

        AcademyData.get(player).setInvited(invited);

        ctx.getSource().sendSuccess(() -> Component.literal("§d" + player.getGameProfile().getName()
                + (invited ? " is now invited to the " : " is no longer invited to the ") + Academy.NAME + "."), true);

        return 1;
    }

    private static int rebuild(CommandContext<CommandSourceStack> ctx) {

        ServerLevel academy = Academy.level(ctx.getSource().getServer());

        if (academy == null) {
            ctx.getSource().sendFailure(Component.literal("§cThe academy dimension is not loaded."));
            return 0;
        }

        AcademyBuilder.build(academy);

        ctx.getSource().sendSuccess(() -> Component.literal("§dRebuilt the " + Academy.NAME + "."), true);

        return 1;
    }
}
