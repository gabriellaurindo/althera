package com.darksune.althera.common.command;

import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
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

import java.util.Comparator;
import java.util.Map;

public class SpellCommand {

    private static final SuggestionProvider<CommandSourceStack> NODES =
            (ctx, builder) -> SharedSuggestionProvider.suggestResource(SpellNodeRegistry.getAll().keySet(), builder);

    @FunctionalInterface
    private interface PlayerAction {
        int run(CommandContext<CommandSourceStack> ctx, ServerPlayer player) throws CommandSyntaxException;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(AltheraCommands.root("spell"))
                        .requires(AltheraCommands::hasCheats)

                        .then(withTarget(Commands.literal("info"), SpellCommand::info))

                        .then(withTarget(Commands.literal("reset"), SpellCommand::reset))

                        .then(Commands.literal("progress")
                                .then(Commands.argument("node", ResourceLocationArgument.id())
                                        .suggests(NODES)
                                        .then(withTarget(
                                                Commands.argument("value", FloatArgumentType.floatArg(0.0F, 1.0F)),
                                                SpellCommand::setProgress
                                        ))
                                )
                        )

                        .then(Commands.literal("train")
                                .then(withTarget(
                                        Commands.argument("node", ResourceLocationArgument.id()).suggests(NODES),
                                        SpellCommand::startTraining
                                ))
                        )

                        .then(Commands.literal("untrain")
                                .then(withTarget(
                                        Commands.argument("node", ResourceLocationArgument.id()).suggests(NODES),
                                        SpellCommand::stopTraining
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

    private static ResourceLocation node(CommandContext<CommandSourceStack> ctx) {

        ResourceLocation id = ResourceLocationArgument.getId(ctx, "node");

        if (SpellNodeRegistry.get(id) == null) {
            ctx.getSource().sendFailure(Component.literal("§cUnknown spell node: " + id));
            return null;
        }

        return id;
    }

    private static int info(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        PlayerSpellData data = PlayerSpellData.get(player);

        ctx.getSource().sendSuccess(() -> Component.literal("§bSpells of " + player.getGameProfile().getName() + ":"), false);

        SpellNodeRegistry.getAll().entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .forEach(entry -> ctx.getSource().sendSuccess(() -> Component.literal(describe(data, entry)), false));

        return 1;
    }

    private static String describe(PlayerSpellData data, Map.Entry<ResourceLocation, SpellNode> entry) {

        ResourceLocation id = entry.getKey();

        return String.format(
                "§7%s §f%s §e%s §7%.0f%%%s",
                id,
                entry.getValue().displayName(),
                data.getState(id),
                data.getProgress(id) * 100.0F,
                data.isLearned(id) && !data.isUsable(id) ? " §c(unusable)" : ""
        );
    }

    private static int reset(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        PlayerSpellData.get(player).reset();

        ctx.getSource().sendSuccess(() -> Component.literal("§bReset the spells of " + player.getGameProfile().getName() + "."), true);

        return 1;
    }

    private static int setProgress(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        ResourceLocation id = node(ctx);

        if (id == null) {
            return 0;
        }

        float value = FloatArgumentType.getFloat(ctx, "value");

        PlayerSpellData data = PlayerSpellData.get(player);

        data.setProgress(id, value);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "§bSet " + id + " to " + Math.round(data.getProgress(id) * 100.0F) + "% (" + data.getState(id) + ")."
        ), true);

        return 1;
    }

    private static int startTraining(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        ResourceLocation id = node(ctx);

        if (id == null) {
            return 0;
        }

        PlayerSpellData data = PlayerSpellData.get(player);

        if (!data.startTraining(id)) {
            ctx.getSource().sendFailure(Component.literal("§c" + id + " cannot be trained (" + data.getState(id) + ")."));
            return 0;
        }

        ctx.getSource().sendSuccess(() -> Component.literal("§bNow training " + id + "."), true);

        return 1;
    }

    private static int stopTraining(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        ResourceLocation id = node(ctx);

        if (id == null) {
            return 0;
        }

        PlayerSpellData.get(player).stopTraining(id);

        ctx.getSource().sendSuccess(() -> Component.literal("§bStopped training " + id + "."), true);

        return 1;
    }
}
