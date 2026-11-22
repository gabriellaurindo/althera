package com.darksune.althera.common.command;

import com.darksune.althera.common.quest.Quest;
import com.darksune.althera.common.quest.QuestData;
import com.darksune.althera.common.quest.QuestRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class QuestCommand {

    private static final SuggestionProvider<CommandSourceStack> QUESTS =
            (ctx, builder) -> SharedSuggestionProvider.suggestResource(QuestRegistry.getAll().keySet(), builder);

    @FunctionalInterface
    private interface PlayerAction {
        int run(CommandContext<CommandSourceStack> ctx, ServerPlayer player) throws CommandSyntaxException;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(AltheraCommands.root("quest"))
                        .requires(AltheraCommands::hasCheats)
                        .then(withTarget(Commands.literal("reset"), QuestCommand::reset))
                        .then(Commands.literal("complete")
                                .then(withTarget(
                                        Commands.argument("quest", ResourceLocationArgument.id()).suggests(QUESTS),
                                        QuestCommand::complete
                                ))
                        )
        );
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T withTarget(T builder, PlayerAction action) {
        return builder
                .executes(ctx -> action.run(ctx, ctx.getSource().getPlayerOrException()))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> action.run(ctx, EntityArgument.getPlayer(ctx, "target"))));
    }

    private static int reset(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        QuestData.get(player).resetAll();

        ctx.getSource().sendSuccess(() -> Component.literal("§dReset the quests of " + player.getGameProfile().getName() + "."), true);

        return 1;
    }

    private static int complete(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        ResourceLocation id = ResourceLocationArgument.getId(ctx, "quest");
        Quest quest = QuestRegistry.get(id);

        if (quest == null) {
            ctx.getSource().sendFailure(Component.literal("§cUnknown quest: " + id));
            return 0;
        }

        QuestData data = QuestData.get(player);

        if (data.isClaimed(id)) {
            ctx.getSource().sendFailure(Component.literal("§c" + id + " is already completed."));
            return 0;
        }

        data.claim(id);
        quest.rewards().forEach(reward -> reward.give(player));

        ctx.getSource().sendSuccess(() -> Component.literal("§dCompleted " + id + " for " + player.getGameProfile().getName() + "."), true);

        return 1;
    }
}
