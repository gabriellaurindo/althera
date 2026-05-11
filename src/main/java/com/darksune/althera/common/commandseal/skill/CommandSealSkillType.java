package com.darksune.althera.common.commandseal.skill;

import com.mojang.serialization.Codec;

public enum CommandSealSkillType {

    OVERDRIVE("overdrive", true, new OverdriveCommandSealSkill()),
    REVIVE("revive", false, new ReviveCommandSealSkill());

    public static final Codec<CommandSealSkillType> CODEC =
            Codec.STRING.xmap(
                    CommandSealSkillType::valueOf,
                    CommandSealSkillType::name
            );

    private final String id;

    private final boolean requiresHeroEntity;

    private final ICommandSealSkill skill;

    CommandSealSkillType(String id, boolean requiresHeroEntity, ICommandSealSkill skill) {
        this.id = id;
        this.requiresHeroEntity = requiresHeroEntity;
        this.skill = skill;
    }

    public String getId() {
        return id;
    }

    public boolean requiresHeroEntity() {
        return requiresHeroEntity;
    }

    public ICommandSealSkill getSkill() {
        return skill;
    }
}