package com.darksune.althera.common.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SpellNode(
        Optional<String> name,
        Optional<ResourceLocation> spell,
        Optional<ResourceLocation> parent,
        float x,
        float y,
        SpellBranch branch,
        SpellElement element,
        int tier
) {

    public static final String PLACEHOLDER_NAME = "?";

    public static final Codec<SpellNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name").forGetter(SpellNode::name),
            ResourceLocation.CODEC.optionalFieldOf("spell").forGetter(SpellNode::spell),
            ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(SpellNode::parent),
            Codec.FLOAT.fieldOf("x").forGetter(SpellNode::x),
            Codec.FLOAT.fieldOf("y").forGetter(SpellNode::y),
            SpellBranch.CODEC.optionalFieldOf("branch", SpellBranch.NONE).forGetter(SpellNode::branch),
            SpellElement.CODEC.optionalFieldOf("element", SpellElement.NONE).forGetter(SpellNode::element),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("tier", 1).forGetter(SpellNode::tier)
    ).apply(instance, SpellNode::new));

    public boolean isRoot() {
        return parent.isEmpty();
    }

    public boolean isPlaceholder() {
        return !isRoot() && spell.isEmpty();
    }

    public boolean isTrainable() {
        return !isRoot() && spellDefinition() != null;
    }

    @Nullable
    public SpellDefinition spellDefinition() {
        return spell.map(SpellRegistry::get).filter(SpellDefinition::isPlayerSpell).orElse(null);
    }

    public String displayName() {

        if (isPlaceholder()) {
            return PLACEHOLDER_NAME;
        }

        return name.orElseGet(() -> spell.map(SpellRegistry::get).map(SpellDefinition::name).orElse(PLACEHOLDER_NAME));
    }
}
