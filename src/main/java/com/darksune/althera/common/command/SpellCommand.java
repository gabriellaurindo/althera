package com.darksune.althera.common.command;

import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellAdvancements;
import com.darksune.althera.common.spell.SpellCastSystem;
import com.darksune.althera.common.spell.SpellElement;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.darksune.althera.common.spell.SpellProficiency;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

public class SpellCommand {

    private static final SuggestionProvider<CommandSourceStack> NODES =
            (ctx, builder) -> SharedSuggestionProvider.suggestResource(SpellNodeRegistry.getAll().keySet(), builder);

    private static final SuggestionProvider<CommandSourceStack> ELEMENTS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(elements().map(SpellElement::getSerializedName), builder);

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

                        .then(Commands.literal("cast")
                                .then(withTarget(
                                        Commands.argument("node", ResourceLocationArgument.id()).suggests(NODES),
                                        SpellCommand::cast
                                ))
                        )

                        .then(Commands.literal("equip")
                                .then(Commands.argument("slot", IntegerArgumentType.integer(1, PlayerSpellData.SLOT_COUNT))
                                        .then(withTarget(
                                                Commands.argument("node", ResourceLocationArgument.id()).suggests(NODES),
                                                SpellCommand::equip
                                        ))
                                )
                        )

                        .then(Commands.literal("unequip")
                                .then(withTarget(
                                        Commands.argument("slot", IntegerArgumentType.integer(1, PlayerSpellData.SLOT_COUNT)),
                                        SpellCommand::unequip
                                ))
                        )

                        .then(Commands.literal("untrain")
                                .then(withTarget(
                                        Commands.argument("node", ResourceLocationArgument.id()).suggests(NODES),
                                        SpellCommand::stopTraining
                                ))
                        )

                        .then(Commands.literal("level")
                                .then(Commands.argument("node", ResourceLocationArgument.id())
                                        .suggests(NODES)
                                        .then(withTarget(
                                                Commands.argument("level", IntegerArgumentType.integer(SpellProficiency.MIN_LEVEL, SpellProficiency.MAX_LEVEL)),
                                                SpellCommand::setSpellLevel
                                        ))
                                )
                        )

                        .then(Commands.literal("element")
                                .then(Commands.argument("element", StringArgumentType.word())
                                        .suggests(ELEMENTS)
                                        .then(withTarget(
                                                Commands.argument("level", IntegerArgumentType.integer(SpellProficiency.MIN_LEVEL, SpellProficiency.MAX_LEVEL)),
                                                SpellCommand::setElementLevel
                                        ))
                                )
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

        elements().forEach(element -> ctx.getSource().sendSuccess(() -> Component.literal(String.format(
                "§7%s mastery §6lv %d §7(%d xp, %.0f%%)",
                element.displayName(),
                data.getElementLevel(element),
                data.getElementXp(element),
                data.getElementLevelProgress(element) * 100.0F
        )), false));

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
                data.isLearned(id) && !data.isUsable(id) ? " §c(unusable)" : "",
                entry.getValue().isTrainable() ? " §6lv " + data.getSpellLevel(id) + " §7(" + data.getSpellXp(id) + " xp)" : ""
        );
    }

    private static Stream<SpellElement> elements() {
        return Arrays.stream(SpellElement.values()).filter(element -> element != SpellElement.NONE);
    }

    private static int setSpellLevel(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        ResourceLocation id = node(ctx);

        if (id == null) {
            return 0;
        }

        if (!SpellNodeRegistry.get(id).isTrainable()) {
            ctx.getSource().sendFailure(Component.literal("§c" + id + " is not a spell."));
            return 0;
        }

        int level = IntegerArgumentType.getInteger(ctx, "level");

        PlayerSpellData.get(player).setSpellXp(id, SpellProficiency.totalXpFor(level, SpellProficiency.SPELL_BASE_XP));
        SpellAdvancements.check(player);

        ctx.getSource().sendSuccess(() -> Component.literal("§bSet " + id + " to level " + level + "."), true);

        return 1;
    }

    private static int setElementLevel(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        String name = StringArgumentType.getString(ctx, "element");

        SpellElement element = elements()
                .filter(candidate -> candidate.getSerializedName().equals(name))
                .findFirst()
                .orElse(null);

        if (element == null) {
            ctx.getSource().sendFailure(Component.literal("§cUnknown element: " + name));
            return 0;
        }

        int level = IntegerArgumentType.getInteger(ctx, "level");

        PlayerSpellData.get(player).setElementXp(element, SpellProficiency.totalXpFor(level, SpellProficiency.ELEMENT_BASE_XP));
        SpellAdvancements.check(player);

        ctx.getSource().sendSuccess(() -> Component.literal("§bSet " + element.displayName() + " mastery to level " + level + "."), true);

        return 1;
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
        SpellAdvancements.check(player);

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

    private static int cast(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        ResourceLocation id = node(ctx);

        return id != null && SpellCastSystem.cast(player, id) ? 1 : 0;
    }

    private static int equip(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        ResourceLocation id = node(ctx);

        if (id == null) {
            return 0;
        }

        int slot = IntegerArgumentType.getInteger(ctx, "slot");

        PlayerSpellData.get(player).setSlot(slot - 1, id);

        ctx.getSource().sendSuccess(() -> Component.literal("§bEquipped " + id + " in slot " + slot + "."), true);

        return 1;
    }

    private static int unequip(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {

        int slot = IntegerArgumentType.getInteger(ctx, "slot");

        PlayerSpellData.get(player).setSlot(slot - 1, null);

        ctx.getSource().sendSuccess(() -> Component.literal("§bCleared slot " + slot + "."), true);

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
