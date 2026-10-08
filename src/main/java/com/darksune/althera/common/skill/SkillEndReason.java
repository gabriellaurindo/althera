package com.darksune.althera.common.skill;

/**
 * Why an active skill was ended. Each skill decides the consequence
 * of each reason, so the systems never need to know about specific skills.
 */
public enum SkillEndReason {

    /** The skill's duration ran out. */
    EXPIRED,

    /** The hero died or was defeated. */
    HERO_DEFEATED,

    /** The hero was dismissed (by the player, lack of mana...). */
    HERO_DISMISSED
}
