package com.darksune.althera.common.commandseal.skill;


import com.mojang.serialization.Codec;

public enum CommandSealSkillType {

    OVERDRIVE("overdrive", new OverdriveCommandSealSkill());

    public static final Codec<CommandSealSkillType> CODEC =
            Codec.STRING.xmap(
                    CommandSealSkillType::valueOf,
                    CommandSealSkillType::name
            );

    private final String id;
    private final ICommandSealSkill skill;

    CommandSealSkillType(String id, ICommandSealSkill skill) {
        this.id = id;
        this.skill = skill;
    }

    public String getId() {
        return id;
    }

    public ICommandSealSkill getSkill() {
        return skill;
    }
}
