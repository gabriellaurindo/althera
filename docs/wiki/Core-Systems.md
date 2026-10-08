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
- Archer *(planned)*
- Lancer *(planned)*
- More classes planned

Classes are intended to define combat archetypes, future abilities, and gameplay specialization.

### Hero Ranks

Current ranks include:

- Common
- Rare
- EX

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
> At the current stage of development, only one fully playable Hero is publicly available.
>
> This temporary limitation exists because the new model, texture, and animation pipeline is still being stabilized and expanded.
>
> Aria (EX Rank) is currently the primary showcase Hero featuring:
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