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

| Class | Style | Knockback resistance | Dodge |
|---|---|---|---|
| Saber | Balanced offense and defense | 80% | 5% |
| Caster | Very high attack, fragile | 50% | 0% |
| Lancer | Fast, high attack | 75% | 5% |
| Shielder | Highest health and armor, slow | 100% | 0% |
| Archer | High attack, lower durability | 60% | 5% |
| Assassin | Fastest, high attack, fragile | 60% | 10% |

Heroes move at their class speed: Assassins and Lancers are faster, Shielders slower. Knockback resistance keeps
crowds of mobs from pushing a Hero away from its target. Dodging avoids the whole attack (explosions can't be
dodged), and a dodge during an intervention still protects you. The hero screen (**G**) shows your Hero's dodge
chance.

Classes matter when Heroes fight each other: every class beats two classes and loses to two, dealing **+25%** damage
to the classes it beats (with a burst of crits) and **−25%** to the ones that beat it:

**Saber → Caster → Lancer → Shielder → Archer → Assassin → Saber** (each class beats the next two in the cycle)

Datapacks can tweak the built-in classes or add new ones (see [Creating Custom Heroes](Creating-Custom-Heroes)).

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

## 🗡 Combat

Heroes fight whichever enemy is attacking them within reach, keep fighting nearby enemies that attack them or you
when their target dies, and help you against what you hit.

### Signature attacks

| Hero | Attack |
|---|---|
| Aria | **Judgment**: her strikes deal 50% extra damage to hostile mobs and another 50% to the undead, with a flash of holy light |
| Clare | Her blades inflict **Wither I** for 3 seconds |
| Valka | **Piercing Thrust**: her spear goes through her target, hitting whoever stands up to 4.5 blocks behind it for 60% of her damage, and pushes the target back |
| Zaya | Fights from a distance: hurls **fire bolts** from the tip of her staff at enemies up to 12 blocks away, backing off when they get close. Her bolts set what they hit on fire |

### Hero vs Hero and PvP

- Heroes of different players can always fight each other, with class advantages applied
- A Hero remembers the player whose Hero (or who) fought it for **30 seconds**, renewed on every hit, and keeps
  chasing that Hero up to **32 blocks**. When the enemy Hero falls or is dismissed, it goes after its owner instead,
  if PvP allows it
- Like tamed wolves, Heroes follow the server's PvP setting: with PvP off (or between teammates without friendly
  fire) they never attack other players, even when hit by one, and their shots and Ultimates don't hurt them

---

## 🔮 Mana System

Mana is the primary resource used to maintain your Hero while summoned.

### Mana Mechanics

- Heroes consume mana while active
- Running out of mana prevents maintaining the summon
- When the Hero is dismissed for lack of mana, your mana is **exhausted**: the Hero can't be summoned again (not even
  by an intervention) until you recover **half of your max mana**. The mana bar pulses red and shows a mark at the
  halfway point while exhausted
- Efficient mana management is essential during extended combat

This system creates a balance between combat power and resource management.

In **Creative** mode mana is infinite: summoning, keeping the Hero out, Command Seals and the Ultimate cost nothing,
and the mana bar shows **∞**.

The **Hero HUD** in the top-left corner shows your hero's face and level, its health, your mana bar and the
interventions left (green diamonds). The face is dimmed while the hero is not summoned and turns red when it is
defeated. The Hero HUD is hidden with **F1**; its hero part can also be hidden in the hero screen settings.
Its corner (top/bottom, left/right) and distance from it can be changed in the client config: **Mods > Althera >
Config**, the **Open Config** key (unbound by default, set it in Controls) or `config/althera-client.toml`.

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
- Heroes gain health, attack and **armor** as they level up (0.15 armor per level before the class, rank and nature
  multipliers: at level 50 an EX Shielder has about 21 armor, an EX Saber about 14 and an EX Caster about 9)
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
> At the current stage of development, only four fully playable Heroes are publicly available.
>
> This temporary limitation exists because the new model, texture, and animation pipeline is still being stabilized and expanded.
>
> Aria (EX Rank, Saber), Clare (EX Rank, Assassin), Zaya (EX Rank, Caster) and Valka (EX Rank, Lancer) are currently
> the showcase Heroes featuring:
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