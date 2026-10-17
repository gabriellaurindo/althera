# Core Systems

This page explains the main gameplay systems of Althera, including Heroes, mana management, spirit form, progression, and combat behavior.

---

## 🧍 Heroes (Summoned Companions)

Heroes are powerful summoned companions that fight alongside the player and represent the core of progression in Althera.

### Basic Features

- Can be summoned using **H**
- Automatically follows and protects the player
- Engages enemies during combat
- Gains experience by defeating mobs
- Levels up over time
- Becomes stronger through progression
- Must land the **final hit** to gain experience
- Follows you through teleports, ender pearls, respawns and dimension changes
- Is dismissed when you log out

Heroes are persistent companions and are designed to evolve alongside the player throughout the game.

---

## ⚔ Hero Classes & Ranks

Each Hero belongs to a specific class and possesses an internal rank that influences overall power scaling.

### Current Classes

- Saber
- Assassin
- Archer *(planned)*
- Lancer *(planned)*
- More classes planned

Classes are intended to define combat archetypes, future abilities, and gameplay specialization.

### Hero Ranks

Ranks, from most common to rarest:

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

Players without a Hero choose their first one on the **Choose your Hero** screen, which opens on login (and when
pressing the summon or hero key). Every Hero with its own model, texture and animations can be chosen; for now that
is Aria (Saber) and Clare (Assassin), both EX.

> [!NOTE]
> **Temporary, current version only:** players who already have a Hero also get this screen once on login and can
> switch to another Hero, keeping their Hero level and XP, or keep their current one. Closing the screen keeps the
> current Hero and the screen returns on the next login until a choice is made. This one-time switch will be removed
> in the next version.

The ritual swaps your Hero: it rolls a new one, different from your current Hero, and your Hero level carries over
(see [Ritual System](Ritual-System)). Rolls only consider Heroes with their own model, texture and animations, and only
ranks that have at least one such Hero, so while Aria and Clare (both EX) are the only ones, every roll gives one of
them.

Higher-ranked Heroes generally possess:

- Increased survivability
- Higher damage output
- Better internal scaling

Some Heroes may also feature:
- Unique models
- Custom textures
- Exclusive animations
- Distinct combat behavior

---

## 🔮 Mana System

Mana is the primary resource used to maintain your Hero while summoned.

### Mana Mechanics

- Heroes consume mana while active
- Running out of mana prevents maintaining the summon
- Efficient mana management is essential during extended combat

This system creates a balance between combat power and resource management.

Your mana is shown in the mana bar on screen. The mana bar and the Hero HUD are hidden together with the rest of the
interface when you press **F1**.

---

## 👻 Spirit Form

When dismissed, Heroes enter a spirit state instead of remaining physically active in the world.

### While in Spirit Form

- The Hero is no longer physically present
- Health regeneration becomes significantly faster
- The player gains:
  - **Resistance I**

Spirit form allows Heroes to recover safely before being summoned again.

The spirit orb always stays with you: it comes back after teleports, respawns, dimension changes and logging in.

---

## 📈 Progression

Progression in Althera is continuous and centered around long-term Hero development.

### Progression Features

- Heroes grow stronger through combat
- Experience is gained primarily through mob kills
- Progression increases survivability and combat effectiveness
- New systems become available as development progresses

Althera is designed around long-term expansion and customization, with future plans including:

- Additional Hero classes
- Expanded rituals
- Unique combat behaviors
- Advanced animations
- More progression systems

---

## 🧩 Data-Driven Hero System

Heroes in Althera are built using a fully data-driven system.

This allows Heroes to dynamically load:

- Models
- Textures
- Animations
- Personality data
- Combat information

The system is designed to support future expansion and potentially community-created Heroes.

---

## 🔗 Related Systems

- [Command Seals & Ultimates](Command-Seals-and-Ultimates)
- [Commands](Commands)
- [Ritual System](Ritual-System)

More systems will be added as development continues.

> [!IMPORTANT]
> At the current stage of development, only two fully playable Heroes are publicly available.
>
> This temporary limitation exists because the new model, texture, and animation pipeline is still being stabilized and expanded.
>
> Aria (EX Rank, Saber) and Clare (EX Rank, Assassin) are currently the showcase Heroes featuring:
>
> - Custom model
> - Custom textures
> - Custom animations
> - Unique combat behavior
>
> Additional Heroes will be added progressively as the system continues to evolve.
>
> ---
>
> Interested in creating your own Hero?
>
> Althera features a fully data-driven Hero system that allows custom Heroes to be created through JSON configuration files.
>
> Learn more here:
>
> - [Creating Custom Heroes](Creating-Custom-Heroes)