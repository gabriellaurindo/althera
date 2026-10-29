# Creating Custom Enemy Mages

Enemy mages are data-driven. The [Noble](Nobles) is just one JSON file: a datapack can tweak him, or add a new mage
of another faction with its own stats, spells, Hero, robes, Command Seals and shrine.

---

# 📁 File Location

```text
data/<your_namespace>/enemy_mage/
```

The file name is the mage id. Example: `data/althera/enemy_mage/noble.json` is `althera:noble`.

Enemy mages reload with `/reload`: mages already in the world pick up the new values for their behaviour (damage
split, seals, targets...), while their stats (health, attack, armor, speed) only change for mages spawned afterwards.

Spawn one with `/althera_mage spawn <mage>` (cheats), for example `/althera_mage spawn althera:noble`.

---

# 🧩 Structure

Only `faction` and `spells` are required. Every other field falls back to the Noble's value.

```json
{
  "name": "Noble",
  "faction": "althera:nobles",
  "stats": { "health": 20, "attack": 1, "armor": 0, "speed": 0.3, "follow_range": 24, "knockback_resistance": 0, "xp": 60 },
  "spells": ["althera:fire_bolt", "althera:frost_shard", "althera:lightning_strike"],
  "targets": ["minecraft:player", "althera:hero", "minecraft:villager", "minecraft:wandering_trader"],
  "servant": {
    "ranks": ["EX"],
    "heroes": [],
    "stat_share": 0.25,
    "damage_share": 0.75,
    "recall_ticks": 200,
    "spirit_regen": { "amount": 1, "interval_ticks": 40 }
  },
  "tiers": [
    { "level": 20, "texture": "althera:textures/entity/noble_mage_green.png", "loot_table": "althera:entities/noble_green" },
    { "level": 30, "texture": "althera:textures/entity/noble_mage_yellow.png", "loot_table": "althera:entities/noble_yellow" },
    { "level": 40, "texture": "althera:textures/entity/noble_mage_red.png", "loot_table": "althera:entities/noble_red" }
  ],
  "command_seals": {
    "charges": 3,
    "heal": { "health_ratio": 0.2 },
    "revive": { "delay_ticks": 20 },
    "overdrive": { "health_ratio": 0.3, "after_revive": true }
  },
  "ultimate": { "health_ratio": 0.5 },
  "home": { "radius": 16, "respawn_ticks": 24000, "activation_range": 32 }
}
```

---

# 📋 Fields

## General

| Field | Default | Description |
|---|---|---|
| `name` | `"Enemy Mage"` | Name shown above the mage |
| `faction` | required | Faction id. Mages of the same faction (and their Heroes) never fight each other |
| `spells` | required | Spell ids. Each mage picks **one** of them at random when it appears (see [Nobles](Nobles#-custom-spells-datapacks)) |
| `targets` | Noble's list | Entity type ids or `#tags` the mage attacks. The nearest one is chosen. Heroes of the same faction are never targeted |
| `loot_table` | none | Loot table dropped when the mage is slain, used by tiers without their own `loot_table` (a regular `minecraft:entity` loot table in `data/<namespace>/loot_table/`). Leave both out and the mage drops nothing |

## `stats`

The mage's own base stats. A share of its Hero's stats is added on top (`servant.stat_share`).

| Field | Default | Description |
|---|---|---|
| `health` | `20` | Base health |
| `attack` | `1` | Base attack damage |
| `armor` | `0` | Base armor |
| `speed` | `0.3` | Movement speed |
| `follow_range` | `24` | How far (blocks) it notices targets |
| `knockback_resistance` | `0` | 0 to 1 |
| `xp` | `60` | Experience dropped when slain |

## `servant`

The Hero bound to the mage.

| Field | Default | Description |
|---|---|---|
| `ranks` | `["EX"]` | A random Hero of one of these ranks is chosen |
| `heroes` | `[]` | Hero ids to choose from. When not empty it replaces `ranks`. Only Heroes with a model can be chosen |
| `stat_share` | `0.25` | Share of the Hero's health, attack and armor added to the mage |
| `damage_share` | `0.75` | Share of the damage the mage takes that hits its Hero instead (0 to 1) |
| `recall_ticks` | `200` | Ticks without a target before the Hero returns to spirit form |
| `spirit_regen.amount` | `1` | Health the Hero recovers in spirit form... |
| `spirit_regen.interval_ticks` | `40` | ...every this many ticks |

If no Hero matches, the mage fights alone.

## `tiers`

One tier is picked at random when the mage appears. `level` is its Hero's level and `texture` is the mage's skin: a
64 x 64 player skin (same layout as a Minecraft skin). The optional `loot_table` is what that tier drops, so tougher
tiers can give better rewards; without it the mage's own `loot_table` is used.

A kill by a player's Hero counts as a kill by that player, so loot tables can use the vanilla
`minecraft:killed_by_player` condition (the Noble's do) to stop iron golem or other mob farms.

## `command_seals`

Leave a seal out and the mage never uses it. Leave the whole block out and the mage has no seals. Each seal is used
at most once per day and costs one charge.

| Field | Default | Description |
|---|---|---|
| `charges` | `3` | Charges per day |
| `heal.health_ratio` | `0.2` | Fully heals its Hero when its health drops to this ratio |
| `revive.delay_ticks` | `20` | Revives its fallen Hero after this many ticks |
| `overdrive.health_ratio` | `0.3` | Overdrives its Hero (stronger, tougher and faster for 30 seconds, then it falls) at this ratio |
| `overdrive.after_revive` | `true` | Only overdrive after Revive was used |

## `ultimate`

Leave it out and the mage never casts its Hero's Ultimate.

| Field | Default | Description |
|---|---|---|
| `health_ratio` | `0.5` | Casts its Hero's Ultimate once per day when the Hero's health drops to this ratio |

## `home`

Used when the mage guards a shrine.

| Field | Default | Description |
|---|---|---|
| `radius` | `16` | How far (blocks) the mage wanders from its sigil |
| `respawn_ticks` | `24000` | Ticks before a new mage appears after the last one was slain (24000 = one in-game day) |
| `activation_range` | `32` | A new mage only appears when a player is this close to the sigil |

---

# 🧪 Example: a new faction

`data/mypack/enemy_mage/zealot.json`: a faster, frailer mage of another faction who only ever summons Aria, has two
seals and no Ultimate. Zealots also hunt other enemy mages (Nobles included), who strike back; they never attack
each other.

```json
{
  "name": "Zealot",
  "faction": "mypack:zealots",
  "stats": { "health": 16, "speed": 0.35, "xp": 40 },
  "spells": ["althera:lightning_strike"],
  "targets": ["minecraft:player", "althera:hero", "althera:enemy_mage"],
  "servant": { "heroes": ["althera:aria"], "stat_share": 0.2 },
  "tiers": [
    { "level": 25, "texture": "mypack:textures/entity/zealot.png" }
  ],
  "command_seals": {
    "charges": 2,
    "heal": { "health_ratio": 0.25 },
    "revive": {}
  }
}
```

---

# 🏛 Giving it a shrine

Shrines are worldgen features. The built-in `althera:noble_shrine` feature takes the mage it spawns as `mage`
(default `althera:noble`). Add three files to your datapack:

`data/mypack/worldgen/configured_feature/zealot_shrine.json`

```json
{
  "type": "althera:noble_shrine",
  "config": {
    "mage": "mypack:zealot"
  }
}
```

`data/mypack/worldgen/placed_feature/zealot_shrine.json`

```json
{
  "feature": "mypack:zealot_shrine",
  "placement": [
    { "type": "minecraft:rarity_filter", "chance": 300 },
    { "type": "minecraft:in_square" },
    { "type": "minecraft:heightmap", "heightmap": "WORLD_SURFACE_WG" },
    { "type": "minecraft:biome" }
  ]
}
```

`data/mypack/neoforge/biome_modifier/zealot_shrine.json`

```json
{
  "type": "neoforge:add_features",
  "biomes": "#minecraft:is_overworld",
  "features": "mypack:zealot_shrine",
  "step": "surface_structures"
}
```

The shrine keeps the Noble look (quartz, gold and a Noble Sigil). Worldgen changes only apply to newly generated
chunks.

---

## 🔗 Related Pages

- [Nobles](Nobles)
- [Creating Custom Heroes](Creating-Custom-Heroes)
- [Creating Custom Ultimates](Creating-Custom-Ultimates)
- [Commands](Commands)
