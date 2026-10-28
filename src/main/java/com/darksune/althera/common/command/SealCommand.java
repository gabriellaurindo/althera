package com.darksune.althera.common.command;

import com.darksune.althera.common.system.SealTattooSystem;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class SealCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(AltheraCommands.root("seal"))

                        .then(Commands.literal("color")
                                .executes(ctx -> {
                                    SealTattooSystem.rerollColor(ctx.getSource().getPlayerOrException());
                                    ctx.getSource().sendSuccess(() -> Component.literal("§dYour Command Seal changed colour."), false);
                                    return 1;
                                })
                        )

//                        .then(Commands.literal("design")
//                                .executes(ctx -> {
//                                    SealTattooSystem.rerollDesign(ctx.getSource().getPlayerOrException());
//                                    ctx.getSource().sendSuccess(() -> Component.literal("§dYour Command Seal changed shape."), false);
//                                    return 1;
//                                })
//                        )
        );
    }
}
