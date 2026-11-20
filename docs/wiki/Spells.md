# Spells

Players can learn spells and cast them with a **Spell Book**. Spells are learned by **training** them: casting a
spell you are still learning costs more and does nothing, but every cast brings you closer to mastering it.

> [!NOTE]
> Player spells are a work in progress. Fire Bolt, Lightning Strike and Frost Shard are the first spells; the
> Defense and Utility branches are `?` placeholders for spells still to come.

---

## 🌳 Spell Tree

Press **K** (or click **Spell Tree** on the Player tab of the character sheet, **G**) to open the spell tree.

- **Knowledge** sits in the centre and is always learned: every spell grows from it
- **Damage** spells go up, **Defense** spells go left, **Utility** spells go down
- Each branch splits into elements: **Fire**, **Lightning** and **Ice**
- A spell can only be trained once the spell before it is learned. Higher tiers are harder to learn

Node borders show each spell's state:

| Border | State |
|---|---|
| 🟩 Green | Learned |
| 🟦 Blue | In training |
| 🟨 Yellow | Can be trained (the spell before it is learned) |
| 🟥 Red | Locked: learn the spell before it first |
| 🟧 Orange | Learned, but not usable until the spells before it are learned (see [Spell Tome](#-spell-tome)) |
| `?` | A spell still to come |

Hover a node to see its cost, cooldown, element, tier, description and how many training casts are left at your
current max mana.

- **Click** a yellow node to start training it. **Click** a blue node to pause its training (the progress is kept)
- Drag the background to move around, scroll to zoom

Several spells can be trained at the same time, but a training cast only advances the spell that was cast.

---

## 📖 Spell Book

Spells are cast with the **Spell Book**, so your free hand stays free for everything else.

**Recipe** (shapeless): book + 2 amethyst shards + lapis lazuli.

The book has **9 spell slots**. A spell joins the first free slot when you start training it or when you learn it.
The slots belong to you, not to the book: any Spell Book casts your spells.

| Key | Action |
|---|---|
| **Right-click** | Cast the selected spell |
| **V** (hold) | Open the spell wheel: point at a slot and release to select it |
| **Z** / **B** | Select the previous / next spell |
| **Left Shift** + mouse wheel | Scroll through the spells while holding a book (instead of the hotbar) |
| **K** | Open the spell tree |

Every key can be changed in **Controls**. Holding a book shows the selected spell next to the hotbar: its cost,
training progress and recharge.

### Managing the slots

The 9 slots are shown at the bottom of the spell tree:

- **Drag** a spell onto a slot, or **right-click** the spell, to equip it
- Drag a slot's spell elsewhere, or **right-click** the slot, to clear it
- **Click** a slot to select it

Pausing a spell's training also removes it from the book.

---

## 🎓 Training

A spell in training (blue) can be cast from the book like any other spell, but:

- It costs **double mana**
- It has **no real effect**: only a puff of magic
- Every cast fills part of the spell's training. When it is full the spell is **learned**: the node turns green and
  the spell casts at its normal cost with its full effect

The number of casts depends on the spell's cost, its tier and your **max mana**: the more mana you have, the faster
you learn. Your max mana grows while it regenerates, and the progress you already made is never lost when the
requirement changes.

| Spell | Cost | Casts at 200 max mana | At 300 | At 500 | At 1000 |
|---|---|---|---|---|---|
| Fire Bolt | 20 | 10 | 7 | 4 | 3 |
| Frost Shard | 20 | 10 | 7 | 4 | 3 |
| Lightning Strike | 25 | 13 | 9 | 5 | 3 |

Every spell needs at least **3** casts. A tier 2 spell takes twice as many casts as a tier 1 spell of the same cost,
a tier 3 spell three times as many. Training progress is kept on death.

In **Creative** mode spells cost no mana and have no cooldown, but they still need to be trained.

---

## ⚔ Spells

| Spell | Element | Cost | Effect |
|---|---|---|---|
| **Fire Bolt** | Fire | 20 | A bolt of fire (7.2 damage) that sets the target ablaze for 4 seconds |
| **Lightning Strike** | Lightning | 25 | A spark that calls lightning down on what it hits (9.6 damage) and hurts everything around it (3.6), even when it hits a wall |
| **Frost Shard** | Ice | 20 | A shard of ice (6 damage) that gives Slowness II for 3 seconds |

Spells fly where you look. They have **no cooldown**: you can cast them as often as your mana allows. Mana is shared
with your Hero, so casting while your Hero is summoned drains the same pool that keeps it in the world.

Spells never hurt your own Hero and follow the server's PvP and team rules: with PvP off, or between teammates without
friendly fire, they don't hurt other players.

The damage above is at level 1. See [Proficiency](#-proficiency).

---

## 🏅 Proficiency

Learning a spell is quick; mastering it takes a long time. Every learned spell has a **level** from 1 to 10, and every
element (**Fire**, **Lightning**, **Ice**) has its own **mastery** level from 1 to 10.

- Both grow with the **mana you spend** on real casts: the spell's level grows with the mana spent on that spell, the
  element's mastery with the mana spent on every spell of that element. Training casts give nothing
- Each **spell level** adds **+10% damage**, **+5% size** (area, reach, particle spread), denser particles and
  **-3% mana cost**
- Each **mastery level** adds **+5% damage** to every spell of that element
- In Creative mode spells cost no mana, so they gain no proficiency

| Level | Spell, total mana spent | Time* | Mastery, total mana spent | Time* |
|---|---|---|---|---|
| 2 | 1,500 | 5 min | 3,000 | 10 min |
| 3 | 3,750 | 12 min | 7,500 | 25 min |
| 5 | 12,188 | 41 min | 24,375 | 1 h 21 |
| 8 | 48,259 | 2 h 40 | 96,516 | 5 h 21 |
| 10 | 112,331 | 6 h 14 | 224,661 | 12 h 28 |

\* Casting non-stop at the rate your mana regenerates (10 mana every 2 seconds, while your Hero is not summoned).

At spell level 10 and mastery 10, Fire Bolt deals **19.8** damage instead of 7.2 and costs 15 mana instead of 20.

You get a message when a spell or a mastery goes up a level. The spell tree tooltip shows a learned spell's level,
its progress to the next one and its current bonuses; the book shows the level next to the cost; the Player tab of the
character sheet (**G**) shows the three masteries (hover one for details).

---

## 📜 Spell Tome

High Nobles (red robe) have a **5%** chance to drop a **Spell Tome** of a random basic spell (see [Nobles](Nobles)).
The creative tab has one tome of each spell.

**Right-click** a tome to study it: it adds **+50%** of that spell's training at once. A tome works whatever the
spell's state: you don't need to be training it, and the spell before it doesn't have to be learned. The progress
just accumulates.

A spell finished with tomes before the spells leading to it are learned counts as learned, but stays **unusable**
(orange in the tree, faded in the book and wheel) until the spells before it are learned.

Tomes of a spell you already know can't be used. A tome whose spell no longer exists becomes a **Faded Tome**.

---

## 🧩 Datapacks

Spells and the spell tree are data-driven: a datapack can change the built-in spells or add new ones. See
[Creating Custom Spells](Creating-Custom-Spells).

---

## 🔗 Related Pages

- [Core Systems](Core-Systems)
- [Creating Custom Spells](Creating-Custom-Spells)
- [Nobles](Nobles)
- [Commands](Commands)
