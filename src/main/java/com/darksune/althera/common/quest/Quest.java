package com.darksune.althera.common.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record Quest(String title, Optional<String> giver, String description, int order, List<ResourceLocation> requires,
                    List<QuestTask> tasks, List<QuestReward> rewards) {

    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(Quest::title),
            Codec.STRING.optionalFieldOf("giver").forGetter(Quest::giver),
            Codec.STRING.optionalFieldOf("description", "").forGetter(Quest::description),
            Codec.INT.optionalFieldOf("order", 0).forGetter(Quest::order),
            ResourceLocation.CODEC.listOf().optionalFieldOf("requires", List.of()).forGetter(Quest::requires),
            QuestTask.CODEC.listOf().optionalFieldOf("tasks", List.of()).forGetter(Quest::tasks),
            QuestReward.CODEC.listOf().optionalFieldOf("rewards", List.of()).forGetter(Quest::rewards)
    ).apply(instance, Quest::new));
}
