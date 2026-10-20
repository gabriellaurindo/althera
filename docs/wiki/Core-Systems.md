# Core Systems

This page explains the main gameplay systems of Althera, including Heroes, mana management, spirit form, progression, and combat behavior.

---

## 🧍 Heroes (Summoned Companions)

Heroes are powerful summoned companions that fight alongside the player and represent the core of progression in Althera.

### Basic Features

- Can be summoned using **H**
- Can be summoned disguised by holding **H** (see [Disguise](#-disguise))
- Automatically follows and protects the player
- Engages enemies during combat
- Gains experience by defeating mobs
- Levels up over time
- Becomes stronger through progression
- Earns experience equal to half the defeated creature's max health when it lands the **final hit** (a zombie
  gives 10, an Iron Golem 50)
- Earns **half** of that when you kill a monster yourself while it is summoned (animals and other passive
  creatures give nothing)
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
is Aria (Saber), Clare (Assassin), Zaya (Caster) and Valka (Lancer), all EX.

The ritual swaps your Hero: it rolls a new one, different from your current Hero, and your Hero level carries over
(see [Ritual System](Ritual-System)). Rolls only consider Heroes with their own model, texture and animations, and only
ranks that have at least one such Hero, so while every Hero is EX, every roll gives one of them.

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

In **Creative** mode mana is infinite: summoning, keeping the Hero out, Command Seals and the Ultimate cost nothing,
and the mana bar shows **∞**.

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

## 🕶 Disguise

Hold the summon key (**H**) for half a second to hide your Hero's identity. A short tap still summons and dismisses
as usual.

- Holding **H** in spirit form summons the Hero already disguised
- Holding **H** with the Hero summoned swaps it between its true form and the disguise, keeping its health percentage
- Hiding is instant, in a puff of smoke. Revealing takes **half a second**: particles spiral up the Hero before a
  flash of light, so everyone nearby sees it coming. The Hero keeps its disguised stats until the flash

While disguised, the Hero appears as a hooded figure with a shadowed face:

- Loses **half of its base health, attack and armor**, but keeps everything it gained from levels: a high-level
  Hero stays strong while disguised (roughly, it still beats the same Hero 8 to 10 levels below it)
- **50%** of its speed, knockback resistance and dodge chance
- Costs only **¼ of the mana** to summon and keep summoned
- Can't use its **Ultimate** (it can't be disguised while channeling one either)
- Keeps its melee or ranged attack, but without its signature effects (ranged attacks leave a plain smoke trail)
- Command Seals still work

Other players can't tell which Hero it is: its name shows as **Hooded Figure**, and its model, health and armor are
hidden, also from tooltip mods such as Jade or WAILA. Your own hero screen (**G**) and Hero HUD still show its
actual stats.

The disguise ends when the Hero is dismissed or defeated.

---

## 📈 Progression

Progression in Althera is continuous and centered around long-term Hero development.

### Progression Features

- Heroes grow stronger through combat
- Experience is gained primarily through mob kills
- Progression increases survivability and combat effectiveness
- The maximum Hero level is **50** for now; the hero screen (**G**) then shows **MAX** instead of the XP needed
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