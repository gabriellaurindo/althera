# Creating Custom Spells

Spells are data-driven. The same spell file is used by enemy mages ([Nobles](Nobles)) and, when it has a `player`
block, by players. The [spell tree](Spells#-spell-tree) is data-driven too: each node is its own file.

A datapack can change the built-in spells, add new ones to the tree, or fill the `?` placeholders.

---

# 📁 File Location

```text
data/<your_namespace>/spell/        spells
data/<your_namespace>/spell_node/   spell tree nodes
```

The file name is the id. Example: `data/althera/spell/fire_bolt.json` is the spell `althera:fire_bolt`, and
`data/althera/spell_node/fire_bolt.json` is the node `althera:fire_bolt`.

Both reload with `/reload`. Training progress is stored per **node** id: renaming a node file loses the progress
players made on it.

---

# ✨ Spells

```json
{
  "name": "Fire Bolt",
  "player": { "mana_cost": 20, "description": "Hurls a bolt of fire that sets the target ablaze." },
  "range": 16,
  "cooldown_ticks": 40,
  "cast_ticks": 10,
  "on_cast": [
    { "type": "sound", "sound": "minecraft:entity.blaze.shoot", "volume": 1.0, "pitch": 0.9 },
    {
      "type": "projectile",
      "speed": 1.3,
      "attack_multiplier": 1.8,
      "trail": { "type": "minecraft:flame" },
      "on_hit": [
        { "type": "ignite", "target": "target", "ticks": 80 },
        { "type": "particles", "particle": { "type": "minecraft:lava" }, "count": 4, "spread": [0.2, 0.2, 0.2] },
        { "type": "sound", "sound": "minecraft:entity.generic.burn", "volume": 0.6, "pitch": 1.2 }
      ]
    }
  ]
}
```

| Field | Default | Description |
|---|---|---|
| `name` | `"Spell"` | Display name |
| `range` | `16` | Mages: distance (blocks) they cast from. Not used by players |
| `cooldown_ticks` | `40` | Mages: ticks between casts. Players use the `player` block's own `cooldown_ticks` |
| `cast_ticks` | `0` | Mages: ticks spent casting before the spell goes off. Players: charge time of `charge` spells, time between pulses of `continuous` spells |
| `on_cast` | `[]` | Effects applied when the spell goes off |
| `player` | none | Makes the spell usable by players (see below). Without it, the spell is for mages only |

`on_cast` uses the same effects and targets as Ultimates (see
[Creating Custom Ultimates](Creating-Custom-Ultimates)).

- **Mages** aim at their current target, which is the `"target"` of `on_cast` effects.
- **Players** aim where they look and have no target: `"target"` effects in `on_cast` do nothing. Use a
  `projectile`: inside its `on_hit`, `"target"` is what the projectile hit.
- `attack_multiplier` multiplies the caster's spell power: a mage's attack damage, or **4** for players. Fire Bolt's
  `1.8` deals 7.2 damage when a player casts it.

## `player`

| Field | Default | Description |
|---|---|---|
| `mana_cost` | required | Mana per cast (doubled while training). For `continuous` spells, mana per second |
| `description` | `""` | Shown in the spell tree tooltip and on Spell Tomes |
| `cast_mode` | `"instant"` | `instant`, `charge` or `continuous` (see below) |
| `cooldown_ticks` | `0` | Ticks before the player can cast it again. `0` = no cooldown |
| `proficiency` | see below | How much each [spell level](Spells#-proficiency) improves the spell |

### `proficiency`

Each level above 1 adds these fractions (level 10 = 9 times the value):

| Field | Default | Description |
|---|---|---|
| `damage_per_level` | `0.10` | Damage of every `damage` and `projectile` effect (+10% per level) |
| `size_per_level` | `0.05` | `area` radius, `line` length and width, particle `spread` and `line` length |
| `visual_per_level` | `0.10` | Particle `count` and projectile trail density |
| `cost_per_level` | `0.03` | Mana cost reduction (at most `0.1`). The cost never drops below 1 |

```json
"player": {
  "mana_cost": 40,
  "proficiency": { "damage_per_level": 0.15, "cost_per_level": 0.05 }
}
```

The element mastery bonus (+5% damage per level) applies on top and is not configurable. Effects that are not listed
(ignite time, mob effect duration, knockback, explosions, heal...) don't change with the level.

### Cast modes

| Mode | Behaviour |
|---|---|
| `instant` | Right-click casts at once |
| `charge` | Hold right-click to charge for `cast_ticks`; mana is paid while charging. Releasing once fully charged casts the spell, releasing early makes it fizzle (the mana spent is lost). Running out of mana stops the charge |
| `continuous` | Hold right-click: `mana_cost` per second while held, `on_cast` fires every `cast_ticks`. Stops on release or when mana runs out. The cooldown starts when it stops |

While training, a `charge` spell gains one training cast per full charge and a `continuous` spell one per second held,
both at double mana and without real effect.

---

# 🌳 Spell Tree Nodes

```json
{
  "spell": "althera:fire_bolt",
  "parent": "althera:knowledge",
  "x": -1,
  "y": 2,
  "branch": "damage",
  "element": "fire",
  "tier": 1
}
```

| Field | Default | Description |
|---|---|---|
| `x`, `y` | required | Position in the tree, in node units. The centre is `0, 0`; `y` grows **upwards** |
| `parent` | none | The node that must be learned before this one can be trained. A node without a parent is a **root**: always learned, never trained (the built-in root is `althera:knowledge`) |
| `spell` | none | The spell this node teaches. A node with a parent but no spell is a `?` **placeholder** |
| `name` | spell name | Overrides the name shown in the tree |
| `branch` | `"none"` | `damage`, `defense`, `utility` or `none`. Shown in the tooltip |
| `element` | `"none"` | `fire`, `lightning`, `ice` or `none`. Colours the spell's tome |
| `tier` | `1` | Difficulty: a tier 2 spell needs twice as many training casts as a tier 1 spell of the same cost, and its tome gives half as much |

A node can only be trained when its spell has a `player` block. A node whose spell is missing, or has no `player`
block, is shown as a placeholder and can't be trained.

The built-in layout: Damage nodes go up (`y` 2 for tier 1, 3 and 4 for the placeholders above), Defense nodes go left
(`x` -2 and -3), Utility nodes go down (`y` -2). The right side is free.

### Training formula

```text
casts = max(3, ceil(mana_cost × 100 × tier / max mana))
```

Every training cast adds `1 / casts` of the training, computed with the player's max mana at the time of the cast.
A Spell Tome adds `50% / tier`.

---

# 🧪 Example: a tier 2 fire spell

`data/mypack/spell/fireball.json`:

```json
{
  "name": "Fireball",
  "player": { "mana_cost": 40, "description": "A heavy ball of fire that explodes on impact.", "cooldown_ticks": 40 },
  "cast_ticks": 20,
  "on_cast": [
    { "type": "sound", "sound": "minecraft:entity.blaze.shoot", "volume": 1.0, "pitch": 0.7 },
    {
      "type": "projectile",
      "speed": 1.0,
      "attack_multiplier": 2.5,
      "trail": { "type": "minecraft:flame" },
      "on_hit": [
        { "type": "damage", "target": { "area": 2.5 }, "filter": { "exclude_target": true }, "attack_multiplier": 1.0 },
        { "type": "ignite", "target": { "area": 2.5 }, "ticks": 60 },
        { "type": "particles", "particle": { "type": "minecraft:explosion" }, "count": 1 }
      ]
    }
  ]
}
```

`data/althera/spell_node/damage_fire_2.json` in the same datapack (overrides the built-in `?` above Fire Bolt):

```json
{
  "spell": "mypack:fireball",
  "parent": "althera:fire_bolt",
  "x": -1,
  "y": 3,
  "branch": "damage",
  "element": "fire",
  "tier": 2
}
```

Add `"cast_mode": "charge"` to the `player` block to make it a charged spell that needs `cast_ticks` (1 second) of
charging.

---

# ✅ Validation

Problems are written to the game log on load (and on `/reload`):

- A file that can't be read (missing `x` / `y`, a `player` block without `mana_cost`, an unknown `branch`, `element`
  or `cast_mode`...) is skipped: `Failed to load spell node ...` / `Failed to load spell ...`
- A node whose `parent` doesn't exist logs `Spell node ... has an unknown parent ...` and can never be trained
- Don't make a node its own ancestor: a parent chain that loops back is never learned

---

## 🔗 Related Pages

- [Spells](Spells)
- [Creating Custom Ultimates](Creating-Custom-Ultimates) (effects and targets)
- [Creating Custom Enemy Mages](Creating-Custom-Enemy-Mages) (giving spells to mages)
- [Nobles](Nobles)
