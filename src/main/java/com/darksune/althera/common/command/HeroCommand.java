package com.darksune.althera.common.command;

import com.darksune.althera.Althera;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.hero.HeroClassRegistry;
import com.darksune.althera.common.hero.HeroDefinition;
import com.darksune.althera.common.hero.HeroRank;
import com.darksune.althera.common.hero.HeroRegistry;
import com.darksune.althera.common.item.HeroCatalystItem;
import com.darksune.althera.common.system.HeroProgressionSystem;
import com.darksune.althera.common.system.HeroRollSystem;
import com.darksune.althera.common.system.HeroStatsSystem;
import com.darksune.althera.common.system.HeroSummonSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class HeroCommand {

    private static final int BENCH_MAX = 200;
    private static final double BENCH_DISTANCE = 4.0;
    private static final double BENCH_SPACING = 1.5;
    private static final String BENCH_TAG = "althera_bench";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(AltheraCommands.root("hero"))

                        // RANDOM
                        .then(Commands.literal("summon")
                                .requires(AltheraCommands::hasCheats)
                                .executes(ctx -> summonRandom(ctx.getSource()))
                        )

                        // DIVINE
                        .then(Commands.literal("divine")
                                .requires(AltheraCommands::hasCheats)
                                .executes(ctx -> summonDivine(ctx.getSource()))
                        )

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

                        .then(Commands.literal("level")
                                .requires(AltheraCommands::hasCheats)
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, HeroProgressionSystem.MAX_LEVEL))
                                        .executes(ctx -> setLevel(
                                                ctx.getSource(),
                                                ctx.getSource().getPlayerOrException(),
                                                IntegerArgumentType.getInteger(ctx, "level")
                                        ))
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .executes(ctx -> setLevel(
                                                        ctx.getSource(),
                                                        EntityArgument.getPlayer(ctx, "target"),
                                                        IntegerArgumentType.getInteger(ctx, "level")
                                                ))
                                        )
                                )
                        )

                        .then(Commands.literal("catalyst")
                                .requires(AltheraCommands::hasCheats)
                                .then(Commands.argument("class", ResourceLocationArgument.id())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(
                                                HeroClassRegistry.getAll().keySet(),
                                                builder
                                        ))
                                        .then(Commands.argument("rank", StringArgumentType.word())
                                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                        Arrays.stream(HeroRank.values()).map(HeroRank::name),
                                                        builder
                                                ))
                                                .executes(ctx -> giveCatalyst(
                                                        ctx.getSource(),
                                                        ctx.getSource().getPlayerOrException(),
                                                        ResourceLocationArgument.getId(ctx, "class"),
                                                        StringArgumentType.getString(ctx, "rank")
                                                ))
                                                .then(Commands.argument("target", EntityArgument.player())
                                                        .executes(ctx -> giveCatalyst(
                                                                ctx.getSource(),
                                                                EntityArgument.getPlayer(ctx, "target"),
                                                                ResourceLocationArgument.getId(ctx, "class"),
                                                                StringArgumentType.getString(ctx, "rank")
                                                        ))
                                                )
                                        )
                                )
                        )

                        // BENCH (render benchmark: idle copies of a hero around the player)
                        .then(Commands.literal("bench")
                                .requires(AltheraCommands::isDevWithCheats)
                                .then(Commands.literal("clear")
                                        .executes(ctx -> clearBench(ctx.getSource()))
                                )
                                .then(Commands.literal("mob")
                                        .then(Commands.argument("entity", ResourceLocationArgument.id())
                                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                                .then(Commands.argument("count", IntegerArgumentType.integer(1, BENCH_MAX))
                                                        .executes(ctx -> spawnMobBench(
                                                                ctx.getSource(),
                                                                ResourceLocationArgument.getId(ctx, "entity"),
                                                                IntegerArgumentType.getInteger(ctx, "count")
                                                        ))
                                                )
                                        )
                                )
                                .then(Commands.argument("hero", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                HeroRegistry.getRollable().stream().map(h -> h.getId().getPath()),
                                                builder
                                        ))
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1, BENCH_MAX))
                                                .executes(ctx -> spawnBench(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "hero"),
                                                        IntegerArgumentType.getInteger(ctx, "count")
                                                ))
                                        )
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
    // BENCH
    // =========================

    private static int spawnBench(CommandSourceStack source, String heroPath, int count) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ResourceLocation heroId = ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, heroPath);

        if (HeroRegistry.get(heroId) == null) {
            source.sendFailure(Component.literal("§cUnknown hero: " + heroId));
            return 0;
        }

        int spawned = spawnInGrid(player, count, level -> {
            HeroEntity hero = AltheraEntities.HERO.get().create(level);
            if (hero != null) {
                hero.setHeroId(heroId);
                hero.markAsBenchmark();
            }
            return hero;
        });

        source.sendSuccess(() -> Component.literal("§eSpawned " + spawned + " benchmark " + heroPath + "."), false);
        return spawned;
    }

    private static int spawnMobBench(CommandSourceStack source, ResourceLocation entityId, int count) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Optional<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId);

        if (type.isEmpty() || !type.get().canSummon()) {
            source.sendFailure(Component.literal("§cUnknown entity: " + entityId));
            return 0;
        }

        int spawned = spawnInGrid(player, count, level -> {
            Entity entity = type.get().create(level);
            if (entity != null) {
                entity.addTag(BENCH_TAG);
                entity.setInvulnerable(true);
                entity.setSilent(true);
                if (entity instanceof Mob mob) {
                    mob.setNoAi(true);
                    mob.setPersistenceRequired();
                }
            }
            return entity;
        });

        source.sendSuccess(() -> Component.literal("§eSpawned " + spawned + " benchmark " + entityId + "."), false);
        return spawned;
    }

    private static int spawnInGrid(ServerPlayer player, int count, Function<ServerLevel, Entity> factory) {
        ServerLevel level = player.serverLevel();
        int columns = (int) Math.ceil(Math.sqrt(count));
        Vec3 forward = Vec3.directionFromRotation(0, player.getYRot());
        Vec3 right = new Vec3(-forward.z, 0, forward.x);
        Vec3 origin = player.position().add(forward.scale(BENCH_DISTANCE));
        int spawned = 0;

        for (int i = 0; i < count; i++) {
            Entity entity = factory.apply(level);
            if (entity == null) continue;

            double column = i % columns - (columns - 1) / 2.0;
            double row = i / columns;
            Vec3 pos = origin.add(right.scale(column * BENCH_SPACING)).add(forward.scale(row * BENCH_SPACING));

            entity.moveTo(pos.x, player.getY(), pos.z, player.getYRot() + 180, 0);
            entity.setYHeadRot(player.getYRot() + 180);
            level.addFreshEntity(entity);
            spawned++;
        }

        return spawned;
    }

    private static int clearBench(CommandSourceStack source) throws CommandSyntaxException {
        ServerLevel level = source.getPlayerOrException().serverLevel();
        List<Entity> benched = new ArrayList<>();
        level.getAllEntities().forEach(entity -> {
            if (entity.getTags().contains(BENCH_TAG) || (entity instanceof HeroEntity hero && hero.isBenchmark())) {
                benched.add(entity);
            }
        });
        benched.forEach(Entity::discard);

        source.sendSuccess(() -> Component.literal("§eRemoved " + benched.size() + " benchmark entities."), false);
        return benched.size();
    }

    // =========================
    // UNSTUCK
    // =========================

    private static int setLevel(CommandSourceStack source, ServerPlayer player, int level) {
        HeroData data = HeroData.get(player);
        if (data.getHeroDefinition() == null) {
            source.sendFailure(Component.literal("§c" + player.getName().getString() + " has no hero."));
            return 0;
        }

        data.setLevel(level);
        data.setXp(0);
        if (!data.isDefeated()) {
            data.setHealth(HeroStatsSystem.getMaxHealth(data));
        }
        data.sync(player);
        HeroSummonSystem.refreshSummon(player);

        HeroEntity summon = HeroSummonSystem.getSummon(player);
        if (summon != null) {
            summon.setHealth(summon.getMaxHealth());
        }

        source.sendSuccess(() -> Component.literal("§aHero of " + player.getName().getString() + " set to level " + level + "."), true);
        return 1;
    }

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

        Optional<HeroDefinition> rolled = HeroRollSystem.rollHero();

        if (rolled.isEmpty()) {
            source.sendFailure(Component.literal("§cNo rollable heroes registered."));
            return 0;
        }

        HeroDefinition def = rolled.get();

        HeroData data = HeroData.get(player);
        data.setHero(def.getId());
        data.setHealth(HeroStatsSystem.getMaxHealth(data));
        data.sync(player);
        HeroSummonSystem.refreshSummon(player);

        source.sendSuccess(() -> Component.literal("§aRandom hero assigned."), false);
        return 1;
    }

    // =========================
    // DIVINE
    // =========================

    private static int summonDivine(CommandSourceStack source) throws CommandSyntaxException {
        Player player = source.getPlayerOrException();

        List<HeroDefinition> pool = HeroRegistry.getRollable().stream()
                .filter(h -> h.getNature().isDivine())
                .toList();

        if (pool.isEmpty()) {
            source.sendFailure(Component.literal("§cNo rollable divine heroes registered."));
            return 0;
        }

        HeroDefinition def = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));

        HeroData data = HeroData.get(player);
        data.setHero(def.getId());
        data.setHealth(HeroStatsSystem.getMaxHealth(data));
        data.sync(player);
        HeroSummonSystem.refreshSummon(player);

        source.sendSuccess(() -> Component.literal("§dDivine hero granted."), false);
        return 1;
    }

    // =========================
    // BY RANK
    // =========================

    private static int giveCatalyst(CommandSourceStack source, ServerPlayer target, ResourceLocation classId, String rankStr) {
        if (!HeroClassRegistry.getAll().containsKey(classId)) {
            source.sendFailure(Component.literal("§cUnknown hero class: " + classId));
            return 0;
        }

        HeroRank rank;
        try {
            rank = HeroRank.valueOf(rankStr.toUpperCase());
        } catch (Exception e) {
            source.sendFailure(Component.literal("§cInvalid rank."));
            return 0;
        }

        ItemStack catalyst = HeroCatalystItem.of(classId, rank);
        if (!target.addItem(catalyst)) {
            target.drop(catalyst, false);
        }

        source.sendSuccess(() -> Component.literal("§aGave a " + rank.getDisplayName() + " "
                + HeroClassRegistry.get(classId).getDisplayName() + " Catalyst to " + target.getName().getString()), false);
        return 1;
    }

    private static int summonByRank(CommandSourceStack source, String rankStr) throws CommandSyntaxException {
        Player player = source.getPlayerOrException();

        HeroRank rank;
        try {
            rank = HeroRank.valueOf(rankStr.toUpperCase());
        } catch (Exception e) {
            source.sendFailure(Component.literal("§cInvalid rank."));
            return 0;
        }

        List<HeroDefinition> pool = HeroRegistry.getRollable().stream()
                .filter(h -> h.getRank() == rank)
                .toList();

        if (pool.isEmpty()) {
            source.sendFailure(Component.literal("§cNo rollable heroes with that rank."));
            return 0;
        }

        HeroDefinition def = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));

        HeroData data = HeroData.get(player);
        data.setHero(def.getId());
        data.setHealth(HeroStatsSystem.getMaxHealth(data));
        data.sync(player);
        HeroSummonSystem.refreshSummon(player);

        source.sendSuccess(() -> Component.literal("§aHero granted (" + rank + ")"), false);
        return 1;
    }
}