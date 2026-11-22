package com.darksune.althera.common.quest;

import com.darksune.althera.common.academy.Academy;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.Optional;

public sealed interface QuestTask {

    Codec<QuestTask> CODEC = Type.CODEC.dispatch(QuestTask::type, Type::codec);

    Type type();

    int required();

    String describe();

    default boolean usesCounter() {
        return false;
    }

    int progress(Player player, int counter);

    enum Type implements StringRepresentable {
        DISCOVER_SPELL("discover_spell", DiscoverSpell.CODEC),
        LEARN_SPELL("learn_spell", LearnSpell.CODEC),
        SPELL_LEVEL("spell_level", SpellLevel.CODEC),
        KILL("kill", Kill.CODEC),
        ITEM("item", ItemTask.CODEC),
        VISIT_ACADEMY("visit_academy", VisitAcademy.CODEC);

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;

        private final MapCodec<? extends QuestTask> codec;

        Type(String name, MapCodec<? extends QuestTask> codec) {
            this.name = name;
            this.codec = codec;
        }

        public MapCodec<? extends QuestTask> codec() {
            return codec;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    private static String spellName(Optional<ResourceLocation> node) {
        return node.map(SpellNodeRegistry::get).map(SpellNode::displayName).orElse("any spell");
    }

    private static boolean anySpell(Player player, Optional<ResourceLocation> node,
                                    java.util.function.BiPredicate<PlayerSpellData, ResourceLocation> test) {

        PlayerSpellData data = PlayerSpellData.get(player);

        return node.map(id -> test.test(data, id))
                .orElseGet(() -> SpellNodeRegistry.getTrainableIds().stream().anyMatch(id -> test.test(data, id)));
    }

    record DiscoverSpell(Optional<ResourceLocation> node) implements QuestTask {

        static final MapCodec<DiscoverSpell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("node").forGetter(DiscoverSpell::node)
        ).apply(instance, DiscoverSpell::new));

        @Override
        public Type type() {
            return Type.DISCOVER_SPELL;
        }

        @Override
        public int required() {
            return 1;
        }

        @Override
        public String describe() {
            return "Discover " + spellName(node);
        }

        @Override
        public int progress(Player player, int counter) {
            return anySpell(player, node, (data, id) -> !SpellNodeRegistry.get(id).isRoot() && data.isDiscovered(id)) ? 1 : 0;
        }
    }

    record LearnSpell(Optional<ResourceLocation> node) implements QuestTask {

        static final MapCodec<LearnSpell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("node").forGetter(LearnSpell::node)
        ).apply(instance, LearnSpell::new));

        @Override
        public Type type() {
            return Type.LEARN_SPELL;
        }

        @Override
        public int required() {
            return 1;
        }

        @Override
        public String describe() {
            return "Learn " + spellName(node);
        }

        @Override
        public int progress(Player player, int counter) {
            return anySpell(player, node, (data, id) -> !SpellNodeRegistry.get(id).isRoot() && data.isLearned(id)) ? 1 : 0;
        }
    }

    record SpellLevel(Optional<ResourceLocation> node, int level) implements QuestTask {

        static final MapCodec<SpellLevel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("node").forGetter(SpellLevel::node),
                Codec.intRange(2, 10).fieldOf("level").forGetter(SpellLevel::level)
        ).apply(instance, SpellLevel::new));

        @Override
        public Type type() {
            return Type.SPELL_LEVEL;
        }

        @Override
        public int required() {
            return 1;
        }

        @Override
        public String describe() {
            return "Raise " + spellName(node) + " to level " + level;
        }

        @Override
        public int progress(Player player, int counter) {
            return anySpell(player, node, (data, id) -> data.isLearned(id) && data.getSpellLevel(id) >= level) ? 1 : 0;
        }
    }

    record Kill(String entity, int count, Optional<String> name) implements QuestTask {

        static final MapCodec<Kill> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("entity").forGetter(Kill::entity),
                Codec.intRange(1, 10000).optionalFieldOf("count", 1).forGetter(Kill::count),
                Codec.STRING.optionalFieldOf("name").forGetter(Kill::name)
        ).apply(instance, Kill::new));

        @Override
        public Type type() {
            return Type.KILL;
        }

        @Override
        public int required() {
            return count;
        }

        @Override
        public String describe() {
            String target = name.orElseGet(() -> entity.startsWith("#") ? entity.substring(1)
                    : Optional.ofNullable(ResourceLocation.tryParse(entity)).flatMap(BuiltInRegistries.ENTITY_TYPE::getOptional)
                    .map(type -> type.getDescription().getString()).orElse(entity));
            return "Defeat " + count + " " + target;
        }

        @Override
        public boolean usesCounter() {
            return true;
        }

        @Override
        public int progress(Player player, int counter) {
            return Math.min(counter, count);
        }

        public boolean matches(Entity victim) {

            ResourceLocation id = ResourceLocation.tryParse(entity.startsWith("#") ? entity.substring(1) : entity);

            if (id == null) {
                return false;
            }

            if (entity.startsWith("#")) {
                return victim.getType().is(TagKey.create(Registries.ENTITY_TYPE, id));
            }

            return EntityType.getKey(victim.getType()).equals(id);
        }
    }

    record ItemTask(Item item, int count, boolean consume) implements QuestTask {

        static final MapCodec<ItemTask> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemTask::item),
                Codec.intRange(1, 10000).optionalFieldOf("count", 1).forGetter(ItemTask::count),
                Codec.BOOL.optionalFieldOf("consume", true).forGetter(ItemTask::consume)
        ).apply(instance, ItemTask::new));

        @Override
        public Type type() {
            return Type.ITEM;
        }

        @Override
        public int required() {
            return count;
        }

        @Override
        public String describe() {
            return (consume ? "Bring " : "Have ") + count + " " + item.getDescription().getString();
        }

        @Override
        public int progress(Player player, int counter) {
            return Math.min(player.getInventory().countItem(item), count);
        }
    }

    record VisitAcademy() implements QuestTask {

        static final MapCodec<VisitAcademy> CODEC = MapCodec.unit(VisitAcademy::new);

        @Override
        public Type type() {
            return Type.VISIT_ACADEMY;
        }

        @Override
        public int required() {
            return 1;
        }

        @Override
        public String describe() {
            return "Go to the " + Academy.NAME;
        }

        @Override
        public boolean usesCounter() {
            return true;
        }

        @Override
        public int progress(Player player, int counter) {
            return Math.min(counter, 1);
        }
    }
}
