# Creating Custom Heroes

Althera features a fully data-driven Hero system.

This means new Heroes can be added through JSON files without requiring direct code modifications.

Custom Heroes may include:

- Names
- Descriptions
- Classes
- Ranks
- Models
- Textures
- Animations
- Personality data
- Ultimates

---

# 📁 Hero File Location

Hero definition files must be placed inside:

```text
data/<your_namespace>/hero/
```

Example:

```text
data/althera/hero/aria.json
```

---

# 🧩 Basic Hero Structure

Example Hero JSON:

```json
{
  "name": "Aria",
  "description": "A disciplined swordswoman from the old order.",
  "class": "SABER",
  "rank": "EX",
  "nature": "COMMON",

  "model": "althera:geo/entity/hero/aria.geo.json",
  "texture": "althera:textures/entity/hero/aria.png",
  "animations": "althera:animations/entity/hero/aria.animation.json",

  "personality": "Honorable and calm",
  "scale": 0.72,
  "ultimate": "althera:explosion"
}
```

---

# 📝 JSON Properties

## `name`

The Hero's display name.

Example:

```json
"name": "Aria"
```

---

## `description`

A short description used by Hero-related systems.

Example:

```json
"description": "A disciplined swordswoman from the old order."
```

---

## `class`

Defines the Hero archetype and internal combat scaling.

Current supported values:

- `SABER`
- `LANCER`
- `ARCHER`
- `CASTER`
- `SHIELDER`
- `ASSASSIN`

Example:

```json
"class": "SABER"
```

### Current Class Characteristics

| Class | Specialty |
|---|---|
| Saber | Balanced offense and defense |
| Lancer | High speed and attack |
| Archer | High attack, lower durability |
| Caster | Extremely high attack scaling |
| Shielder | High defense and survivability |
| Assassin | Highest speed and high attack, but fragile |

Class advantages apply when Heroes fight each other: a Hero deals **+25%** damage to the class it is strong against
(with a burst of crit particles) and **−25%** to the class it is weak against. Every hit counts: attacks, projectiles
and Ultimate effects.

- Saber > Lancer
- Lancer > Archer
- Archer > Saber
- Assassin > Caster
- Shielder > Assassin

Casters have no advantage over any class.

---

## `rank`

Defines Hero rarity and overall power scaling. **Required**: a Hero without `rank` fails to load.

| Rank | Chance | Health multiplier |
|---|---|---|
| F | 40% | ×0.7 |
| E | 25% | ×0.8 |
| D | 15% | ×0.9 |
| C | 10% | ×1.0 |
| B | 5% | ×1.15 |
| A | 3% | ×1.3 |
| S | 1.5% | ×1.5 |
| EX | 0.5% | ×1.75 |

The roll first picks a rank (weighted by chance, among ranks that have at least one rollable Hero), then a random
Hero of that rank.

Example:

```json
"rank": "EX"
```

---

## `nature`

Internal Hero alignment/category system.

Current supported values:

- `COMMON` (default)
- `DIVINE` — ×10 global multiplier, granted with `/althera_hero divine`

Example:

```json
"nature": "COMMON"
```

---

# 🎨 Visual Assets

A Hero only enters the roll (first summon, ritual and the `summon` / `rank` / `divine` commands) when it defines
**all three**: `model`, `texture` and `animations`.

Heroes without them still load (and keep working for players who already have them, using the default assets), but
are never rolled. This lets you add a Hero's data first and its visuals later.

---

## `model`

Path to the GeckoLib `.geo.json` model.

Example:

```json
"model": "althera:geo/entity/hero/aria.geo.json"
```

Expected asset location:

```text
assets/althera/geo/entity/hero/aria.geo.json
```

---

## `texture`

Path to the Hero texture.

Example:

```json
"texture": "althera:textures/entity/hero/aria.png"
```

Expected asset location:

```text
assets/althera/textures/entity/hero/aria.png
```

---

## `animations`

Path to the GeckoLib animation file.

Example:

```json
"animations": "althera:animations/entity/hero/aria.animation.json"
```

Expected asset location:

```text
assets/althera/animations/entity/hero/aria.animation.json
```

### Currently Supported Animations

At the current stage of development, the following animation names are expected:

- `idle`
- `walk`
- `attack`
- `ultimate` (played while channeling an ultimate)
- `death` (played on defeat; keep it at most 2 seconds long and hold the last frame)

These animation names should exist inside the animation JSON file. A missing animation simply doesn't play.

---

# 📏 Scale

Optional. Multiplies the size of the hero model in the world (default `1.0`). The player is 1.8 blocks tall: lower the
value if the hero looks taller than the player, raise it if shorter. The hero screens (Choose your Hero, hero status)
ignore `scale` and show the model at its drawn size, a bit larger than the player. Only the rendering is scaled; the
hero hitbox is fixed at 0.6 x 1.8 blocks, the same as the player.

```json
"scale": 0.72
```

---

# 💬 Personality

Optional flavor/personality field for future systems and interactions.

Example:

```json
"personality": "Honorable and calm"
```

---

# 💥 Ultimate

Optional. Id of the Hero's Ultimate (default `althera:explosion`). Ultimates are their own JSON files, so several
Heroes can share one; see [Creating Custom Ultimates](Creating-Custom-Ultimates).

```json
"ultimate": "althera:explosion"
```

---

# 🏹 Attack

Optional. How the Hero fights. Without it the Hero attacks in melee once per second with no extra effects.

Every hit deals the Hero's attack damage (it grows with the Hero level, class, rank and nature). On top of that,
`on_attack` and `on_hit` run **effects**, the same ones Ultimates use (damage, knockback, fire, mob effects,
explosions, particles, sounds, dashes...); see [Creating Custom Ultimates](Creating-Custom-Ultimates#-effects).

A **melee** Hero walks up to its target and strikes every `cooldown_ticks`.

A **ranged** Hero keeps its distance: it walks closer while the target is out of `range` or out of sight, backs off
when the target gets closer than half the `range`, and shoots a projectile every `cooldown_ticks` while it can see the
target. The projectile flies straight, leaves a trail of `projectile_trail` and never hits the Hero's owner, so Heroes
of different players can fight each other. The Hero plays its `attack` animation on every shot.

```json
"attack": {
  "type": "ranged",
  "range": 12,
  "cooldown_ticks": 30,
  "shoot_delay_ticks": 5,
  "projectile_speed": 1.2,
  "projectile_origin": [0.13, 2.72, 2.64],
  "projectile_trail": { "type": "minecraft:flame" },
  "on_attack": [
    { "type": "sound", "sound": "minecraft:entity.blaze.shoot", "volume": 0.8, "pitch": 1.1 }
  ],
  "on_hit": [
    { "type": "ignite", "target": "target", "ticks": 40 },
    { "type": "particles", "particle": { "type": "minecraft:lava" }, "count": 3, "spread": [0.2, 0.2, 0.2] },
    { "type": "sound", "sound": "minecraft:entity.generic.burn", "volume": 0.6, "pitch": 1.4 }
  ]
}
```

| Field | Default | Description |
|---|---|---|
| `type` | `melee` | `melee` or `ranged`. |
| `cooldown_ticks` | `20` | Ticks between attacks (20 ticks = 1 second). |
| `on_attack` | none | Effects when the Hero strikes (melee) or shoots (ranged). |
| `on_hit` | none | Effects when the attack lands: a melee hit, a projectile hitting a creature, or a projectile hitting a block (then `"target"` hits nobody). A dodged attack doesn't trigger them. |
| `range` | `12` | Ranged only. Distance in blocks from which the Hero shoots. |
| `shoot_delay_ticks` | `0` | Ranged only. Ticks between the start of the `attack` animation and the shot, so the projectile leaves at the frame where the weapon is thrust forward. The animation plays at 1.2× speed, so a frame at `t` seconds in Blockbench is `t / 1.2 × 20` ticks. |
| `projectile_speed` | `1.2` | Ranged only. Blocks per tick. |
| `projectile_origin` | `[0, 1.26, 0]` | Ranged only. Where the projectile appears, in blocks from the Hero's feet: `[right, up, forward]`, at model size (multiplied by `scale`). It turns with the Hero's body. If a wall is in the way, the projectile starts in front of the wall. |
| `projectile_trail` | `{ "type": "minecraft:flame" }` | Ranged only. Trail and impact particle, in the same format as the `particles` effect (dust colors work too). |

Inside `on_attack` and `on_hit`:

- the `"target"` target is the creature being attacked (or hit);
- positions (particles, sounds, explosions, `area`) are the impact point in `on_hit`, and the Hero in `on_attack`;
- lines and dashes point towards the creature being attacked;
- `knockback` pushes away from the Hero; for ranged attacks set `"scale_with_distance": false`, or the push grows
  with the distance of the shot.

To find `projectile_origin`, add an empty bone called `projectile_origin` at the tip of the weapon in Blockbench
(child of the weapon bone, so it follows the animation), go to the frame of the shot in the `attack` animation and read
the bone's position in pixels: Blockbench +X is the model's right, +Y up and −Z forward. Divide by 16:
`[x / 16, y / 16, −z / 16]`.

---

# ⭐ Choose your Hero

Players without a Hero pick their first one on the **Choose your Hero** screen (on login, or when pressing the summon
or hero key). Every Hero with its own `model`, `texture` and `animations` shows up there, so a new Hero with complete
visuals becomes selectable automatically.

---

# ⚠ Notes

- Invalid paths may cause missing models or textures
- Heroes missing `model`, `texture` or `animations` are never rolled
- The game log reports how many Heroes loaded, failed and are rollable
- Animation support is still actively evolving
- Hero combat systems may continue changing during development

Future updates may introduce:

- Skills
- More Hero abilities
- Unique AI behaviors
- Expanded class systems
- Advanced combat logic

---

# 🔗 Related Pages

- [Core Systems](Core-Systems)
- [Creating Custom Ultimates](Creating-Custom-Ultimates)