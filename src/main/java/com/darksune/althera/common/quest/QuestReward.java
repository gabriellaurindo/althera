package com.darksune.althera.common.quest;

import com.darksune.althera.common.academy.Academy;
import com.darksune.althera.common.academy.AcademyData;
import com.darksune.althera.common.item.SpellTomeItem;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.spell.SpellAdvancements;
import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public sealed interface QuestReward {

    Codec<QuestReward> CODEC = Type.CODEC.dispatch(QuestReward::type, Type::codec);

    Type type();

    String describe();

    void give(ServerPlayer player);

    enum Type implements StringRepresentable {
        INVITATION("invitation", Invitation.CODEC),
        DISCOVER_SPELL("discover_spell", DiscoverSpell.CODEC),
        ITEM("item", ItemReward.CODEC);

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;

        private final MapCodec<? extends QuestReward> codec;

        Type(String name, MapCodec<? extends QuestReward> codec) {
            this.name = name;
            this.codec = codec;
        }

        public MapCodec<? extends QuestReward> codec() {
            return codec;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    record Invitation() implements QuestReward {

        static final MapCodec<Invitation> CODEC = MapCodec.unit(Invitation::new);

        @Override
        public Type type() {
            return Type.INVITATION;
        }

        @Override
        public String describe() {
            return "Invitation to the " + Academy.NAME;
        }

        @Override
        public void give(ServerPlayer player) {
            AcademyData.get(player).setInvited(true);
        }
    }

    record DiscoverSpell(Optional<ResourceLocation> node, int tier) implements QuestReward {

        static final MapCodec<DiscoverSpell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("node").forGetter(DiscoverSpell::node),
                Codec.intRange(1, 10).optionalFieldOf("tier", 1).forGetter(DiscoverSpell::tier)
        ).apply(instance, DiscoverSpell::new));

        @Override
        public Type type() {
            return Type.DISCOVER_SPELL;
        }

        @Override
        public String describe() {
            return node.map(SpellNodeRegistry::get).map(spell -> "Discover " + spell.displayName())
                    .orElse("Discover a tier " + tier + " spell");
        }

        @Override
        public void give(ServerPlayer player) {

            PlayerSpellData data = PlayerSpellData.get(player);
            List<ResourceLocation> candidates = node.map(List::of).orElseGet(() -> SpellNodeRegistry.getTrainableIds().stream()
                    .filter(id -> SpellNodeRegistry.get(id).tier() == tier)
                    .toList());
            List<ResourceLocation> unknown = candidates.stream().filter(id -> !data.isDiscovered(id)).toList();

            if (unknown.isEmpty()) {
                if (!candidates.isEmpty()) {
                    ItemStack tome = SpellTomeItem.of(candidates.get(player.getRandom().nextInt(candidates.size())));
                    if (!player.getInventory().add(tome)) {
                        player.drop(tome, false);
                    }
                }
                return;
            }

            ResourceLocation id = unknown.get(player.getRandom().nextInt(unknown.size()));
            SpellNode spell = SpellNodeRegistry.get(id);
            data.discover(id);
            SpellAdvancements.check(player);
            player.sendSystemMessage(Component.literal("§bDiscovered " + spell.displayName() + "! §7Train it in the spell tree."));
        }
    }

    record ItemReward(Item item, int count) implements QuestReward {

        static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemReward::item),
                Codec.intRange(1, 6400).optionalFieldOf("count", 1).forGetter(ItemReward::count)
        ).apply(instance, ItemReward::new));

        @Override
        public Type type() {
            return Type.ITEM;
        }

        @Override
        public String describe() {
            return count + " " + item.getDescription().getString();
        }

        @Override
        public void give(ServerPlayer player) {

            int left = count;

            while (left > 0) {
                ItemStack stack = new ItemStack(item, Math.min(left, item.getDefaultMaxStackSize()));
                left -= stack.getCount();

                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
        }
    }
}
