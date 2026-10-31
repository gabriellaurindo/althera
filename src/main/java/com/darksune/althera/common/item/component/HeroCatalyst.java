package com.darksune.althera.common.item.component;

import com.darksune.althera.common.hero.HeroClass;
import com.darksune.althera.common.hero.HeroClassRegistry;
import com.darksune.althera.common.hero.HeroRank;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;

import static com.darksune.althera.Althera.MOD_ID;

public record HeroCatalyst(ResourceLocation classId, HeroRank rank) {

    public static final List<ResourceLocation> BUILT_IN_CLASSES = List.of(
            builtIn("saber"),
            builtIn("caster"),
            builtIn("lancer"),
            builtIn("shielder"),
            builtIn("archer"),
            builtIn("assassin")
    );

    public static final Codec<HeroCatalyst> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("class").forGetter(HeroCatalyst::classId),
            HeroRank.CODEC.fieldOf("rank").forGetter(HeroCatalyst::rank)
    ).apply(instance, HeroCatalyst::new));

    public static final StreamCodec<FriendlyByteBuf, HeroCatalyst> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, HeroCatalyst::classId,
            NeoForgeStreamCodecs.enumCodec(HeroRank.class), HeroCatalyst::rank,
            HeroCatalyst::new
    );

    public HeroClass heroClass() {
        return HeroClassRegistry.get(classId);
    }

    public boolean isKnownClass() {
        return heroClass() != HeroClass.NONE;
    }

    private static ResourceLocation builtIn(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
