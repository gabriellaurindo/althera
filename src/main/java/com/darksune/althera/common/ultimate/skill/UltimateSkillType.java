package com.darksune.althera.common.ultimate.skill;

import com.mojang.serialization.Codec;

public enum UltimateSkillType {

    EXPLOSION("explosion", true, new ExplosionUltimateSkill());

    public static final Codec<UltimateSkillType> CODEC =
            Codec.STRING.xmap(
                    UltimateSkillType::valueOf,
                    UltimateSkillType::name
            );

    private final String id;

    private final boolean requiresHeroEntity;

    private final IUltimateSkill skill;

    UltimateSkillType(String id, boolean requiresHeroEntity, IUltimateSkill skill) {
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

    public IUltimateSkill getSkill() {
        return skill;
    }
}
