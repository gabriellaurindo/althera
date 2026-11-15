package com.darksune.althera.common.item.component;

import com.darksune.althera.common.spell.SpellNode;
import com.darksune.althera.common.spell.SpellNodeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record SpellTome(ResourceLocation nodeId) {

    public static final float BASIC_PROGRESS = 0.5F;

    public static final Codec<SpellTome> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("node").forGetter(SpellTome::nodeId)
    ).apply(instance, SpellTome::new));

    public static final StreamCodec<FriendlyByteBuf, SpellTome> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, SpellTome::nodeId,
            SpellTome::new
    );

    @Nullable
    public SpellNode node() {
        SpellNode node = SpellNodeRegistry.get(nodeId);
        return node != null && node.isTrainable() ? node : null;
    }

    public static float progressFor(SpellNode node) {
        return BASIC_PROGRESS / node.tier();
    }
}
