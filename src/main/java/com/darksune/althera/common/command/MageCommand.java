package com.darksune.althera.common.command;

import com.darksune.althera.common.enemy.EnemyMageRegistry;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.EnemyMageEntity;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class MageCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(AltheraCommands.root("mage"))
                        .requires(AltheraCommands::hasCheats)

                        // SPAWN
                        .then(Commands.literal("spawn")
                                .then(Commands.argument("definition", ResourceLocationArgument.id())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(
                                                EnemyMageRegistry.getAll().keySet(),
                                                builder
                                        ))
                                        .executes(ctx -> spawnMage(
                                                ctx.getSource(),
                                                ResourceLocationArgument.getId(ctx, "definition")
                                        ))
                                )
                        )
        );
    }

    // =========================
    // SPAWN
    // =========================

    private static int spawnMage(CommandSourceStack source, ResourceLocation id) {
        if (EnemyMageRegistry.get(id) == null) {
            source.sendFailure(Component.literal("§cUnknown enemy mage: " + id));
            return 0;
        }

        ServerLevel level = source.getLevel();
        EnemyMageEntity mage = AltheraEntities.ENEMY_MAGE.get().create(level);
        if (mage == null) return 0;

        Vec3 pos = source.getPosition();
        mage.setDefinition(id);
        mage.moveTo(pos.x, pos.y, pos.z, source.getRotation().y, 0.0F);
        EventHooks.finalizeMobSpawn(mage, level, level.getCurrentDifficultyAt(BlockPos.containing(pos)), MobSpawnType.COMMAND, null);
        level.addFreshEntity(mage);

        source.sendSuccess(() -> Component.literal("§bSpawned " + mage.getName().getString() + " (" + id + ")."), true);
        return 1;
    }
}
