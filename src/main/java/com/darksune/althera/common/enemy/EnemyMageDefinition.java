package com.darksune.althera.common.enemy;

import com.darksune.althera.Althera;
import com.darksune.althera.common.hero.HeroRank;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record EnemyMageDefinition(
        String name,
        ResourceLocation faction,
        Stats stats,
        List<ResourceLocation> spells,
        List<ExtraCodecs.TagOrElementLocation> targets,
        Servant servant,
        List<Tier> tiers,
        CommandSeals commandSeals,
        Optional<UltimateTrigger> ultimate,
        Home home,
        Optional<ResourceLocation> lootTable
) {

    public static final List<ExtraCodecs.TagOrElementLocation> DEFAULT_TARGETS = List.of(
            element("minecraft", "player"),
            element(Althera.MOD_ID, "hero"),
            element("minecraft", "villager"),
            element("minecraft", "wandering_trader")
    );

    public static final List<Tier> DEFAULT_TIERS = List.of(
            new Tier(20, texture("noble_mage_green"), Optional.empty()),
            new Tier(30, texture("noble_mage_yellow"), Optional.empty()),
            new Tier(40, texture("noble_mage_red"), Optional.empty())
    );

    public static final Codec<EnemyMageDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name", "Enemy Mage").forGetter(EnemyMageDefinition::name),
            ResourceLocation.CODEC.fieldOf("faction").forGetter(EnemyMageDefinition::faction),
            Stats.CODEC.optionalFieldOf("stats", Stats.DEFAULT).forGetter(EnemyMageDefinition::stats),
            ExtraCodecs.nonEmptyList(ResourceLocation.CODEC.listOf()).fieldOf("spells").forGetter(EnemyMageDefinition::spells),
            ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("targets", DEFAULT_TARGETS).forGetter(EnemyMageDefinition::targets),
            Servant.CODEC.optionalFieldOf("servant", Servant.DEFAULT).forGetter(EnemyMageDefinition::servant),
            ExtraCodecs.nonEmptyList(Tier.CODEC.listOf()).optionalFieldOf("tiers", DEFAULT_TIERS).forGetter(EnemyMageDefinition::tiers),
            CommandSeals.CODEC.optionalFieldOf("command_seals", CommandSeals.NONE).forGetter(EnemyMageDefinition::commandSeals),
            UltimateTrigger.CODEC.optionalFieldOf("ultimate").forGetter(EnemyMageDefinition::ultimate),
            Home.CODEC.optionalFieldOf("home", Home.DEFAULT).forGetter(EnemyMageDefinition::home),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter(EnemyMageDefinition::lootTable)
    ).apply(instance, EnemyMageDefinition::new));

    public Tier tierFor(final int level) {
        Tier match = tiers.getFirst();
        for (final Tier tier : tiers) {
            if (tier.level() <= level && (match.level() > level || tier.level() > match.level())) {
                match = tier;
            }
        }
        return match;
    }

    public Optional<ResourceLocation> lootTableFor(final int level) {
        return tierFor(level).lootTable().or(this::lootTable);
    }

    private static ExtraCodecs.TagOrElementLocation element(final String namespace, final String path) {
        return new ExtraCodecs.TagOrElementLocation(ResourceLocation.fromNamespaceAndPath(namespace, path), false);
    }

    private static ResourceLocation texture(final String name) {
        return ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "textures/entity/" + name + ".png");
    }

    public record Stats(float health, float attack, float armor, float speed, float followRange,
                        float knockbackResistance, int xp) {

        public static final Stats DEFAULT = new Stats(20.0f, 1.0f, 0.0f, 0.3f, 24.0f, 0.0f, 60);

        public static final Codec<Stats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("health", DEFAULT.health).forGetter(Stats::health),
                Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("attack", DEFAULT.attack).forGetter(Stats::attack),
                Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("armor", DEFAULT.armor).forGetter(Stats::armor),
                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed", DEFAULT.speed).forGetter(Stats::speed),
                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("follow_range", DEFAULT.followRange).forGetter(Stats::followRange),
                Codec.floatRange(0.0f, 1.0f).optionalFieldOf("knockback_resistance", DEFAULT.knockbackResistance).forGetter(Stats::knockbackResistance),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("xp", DEFAULT.xp).forGetter(Stats::xp)
        ).apply(instance, Stats::new));
    }

    public record SpiritRegen(float amount, int intervalTicks) {

        public static final SpiritRegen DEFAULT = new SpiritRegen(1.0f, 40);

        public static final Codec<SpiritRegen> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("amount", DEFAULT.amount).forGetter(SpiritRegen::amount),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("interval_ticks", DEFAULT.intervalTicks).forGetter(SpiritRegen::intervalTicks)
        ).apply(instance, SpiritRegen::new));
    }

    public record Servant(List<HeroRank> ranks, List<ResourceLocation> heroes, float statShare, float damageShare,
                          int recallTicks, SpiritRegen spiritRegen) {

        public static final Servant DEFAULT = new Servant(List.of(HeroRank.EX), List.of(), 0.25f, 0.75f, 200, SpiritRegen.DEFAULT);

        public static final Codec<Servant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                HeroRank.CODEC.listOf().optionalFieldOf("ranks", DEFAULT.ranks).forGetter(Servant::ranks),
                ResourceLocation.CODEC.listOf().optionalFieldOf("heroes", DEFAULT.heroes).forGetter(Servant::heroes),
                Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("stat_share", DEFAULT.statShare).forGetter(Servant::statShare),
                Codec.floatRange(0.0f, 1.0f).optionalFieldOf("damage_share", DEFAULT.damageShare).forGetter(Servant::damageShare),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("recall_ticks", DEFAULT.recallTicks).forGetter(Servant::recallTicks),
                SpiritRegen.CODEC.optionalFieldOf("spirit_regen", DEFAULT.spiritRegen).forGetter(Servant::spiritRegen)
        ).apply(instance, Servant::new));
    }

    public record Tier(int level, ResourceLocation texture, Optional<ResourceLocation> lootTable) {

        public static final Codec<Tier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("level").forGetter(Tier::level),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Tier::texture),
                ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter(Tier::lootTable)
        ).apply(instance, Tier::new));
    }

    public record Heal(float healthRatio) {

        public static final Codec<Heal> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0.0f, 1.0f).optionalFieldOf("health_ratio", 0.2f).forGetter(Heal::healthRatio)
        ).apply(instance, Heal::new));
    }

    public record Revive(int delayTicks) {

        public static final Codec<Revive> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("delay_ticks", 20).forGetter(Revive::delayTicks)
        ).apply(instance, Revive::new));
    }

    public record Overdrive(float healthRatio, boolean afterRevive) {

        public static final Codec<Overdrive> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0.0f, 1.0f).optionalFieldOf("health_ratio", 0.3f).forGetter(Overdrive::healthRatio),
                Codec.BOOL.optionalFieldOf("after_revive", true).forGetter(Overdrive::afterRevive)
        ).apply(instance, Overdrive::new));
    }

    public record CommandSeals(int charges, Optional<Heal> heal, Optional<Revive> revive, Optional<Overdrive> overdrive) {

        public static final CommandSeals NONE = new CommandSeals(0, Optional.empty(), Optional.empty(), Optional.empty());

        public static final Codec<CommandSeals> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("charges", 3).forGetter(CommandSeals::charges),
                Heal.CODEC.optionalFieldOf("heal").forGetter(CommandSeals::heal),
                Revive.CODEC.optionalFieldOf("revive").forGetter(CommandSeals::revive),
                Overdrive.CODEC.optionalFieldOf("overdrive").forGetter(CommandSeals::overdrive)
        ).apply(instance, CommandSeals::new));
    }

    public record UltimateTrigger(float healthRatio) {

        public static final Codec<UltimateTrigger> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0.0f, 1.0f).optionalFieldOf("health_ratio", 0.5f).forGetter(UltimateTrigger::healthRatio)
        ).apply(instance, UltimateTrigger::new));
    }

    public record Home(int radius, int respawnTicks, int activationRange) {

        public static final Home DEFAULT = new Home(16, 24000, 32);

        public static final Codec<Home> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("radius", DEFAULT.radius).forGetter(Home::radius),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("respawn_ticks", DEFAULT.respawnTicks).forGetter(Home::respawnTicks),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("activation_range", DEFAULT.activationRange).forGetter(Home::activationRange)
        ).apply(instance, Home::new));
    }
}
