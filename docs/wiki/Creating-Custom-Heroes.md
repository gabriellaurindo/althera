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
  "animations": "althera:animations/hero/aria.animation.json",

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

Defines Hero rarity and overall power scaling.

Current supported values:

- `EX`

Additional ranks are planned for future versions.

Example:

```json
"rank": "EX"
```

---

## `nature`

Internal Hero alignment/category system.

Current supported values:

- `COMMON`

Example:

```json
"nature": "COMMON"
```

---

# 🎨 Visual Assets

All visual assets are optional.

If omitted, the Hero automatically uses fallback/default assets when possible.

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
"animations": "althera:animations/hero/aria.animation.json"
```

Expected asset location:

```text
assets/althera/animations/hero/aria.animation.json
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
- Missing visual assets automatically fallback to default assets when possible
- Animation support is still actively evolving
- Hero combat systems may continue changing during development

Future updates may introduce:

- Skills
- Hero abilities
- Unique AI behaviors
- Additional ranks
- Expanded class systems
- Advanced combat logic

---

# 🔗 Related Pages

- [Core Systems](Core-Systems)