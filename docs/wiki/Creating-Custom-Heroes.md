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

  "personality": "Honorable and calm"
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

Class advantage relationships currently exist between:

- Saber > Lancer
- Lancer > Archer
- Archer > Saber

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

These animation names should exist inside the animation JSON file.

---

# 💬 Personality

Optional flavor/personality field for future systems and interactions.

Example:

```json
"personality": "Honorable and calm"
```

---

# ⚠ Notes

- Invalid paths may cause missing models or textures
- Heroes missing `model`, `texture` or `animations` are never rolled
- The game log reports how many Heroes loaded, failed and are rollable
- Animation support is still actively evolving
- Hero combat systems may continue changing during development

Future updates may introduce:

- Skills
- Hero abilities
- Unique AI behaviors
- Expanded class systems
- Advanced combat logic

---

# 🔗 Related Pages

- [Core Systems](Core-Systems)