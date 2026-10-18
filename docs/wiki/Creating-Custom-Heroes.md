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

Class advantage relationships currently exist between:

- Saber > Lancer
- Lancer > Archer
- Archer > Saber
- Assassin > Caster
- Shielder > Assassin

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

# ⭐ Choose your Hero

Players without a Hero pick their first one on the **Choose your Hero** screen (on login, or when pressing the summon
or hero key). Every Hero with its own `model`, `texture` and `animations` shows up there, so a new Hero with complete
visuals becomes selectable automatically.

In the current version only, players who already have a Hero can also use this screen once to switch Heroes while
keeping their level; this will be removed in the next version.

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