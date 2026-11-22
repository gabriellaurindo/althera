# Spells

Players can learn spells and cast them with a **Spell Book**. Spells are learned by **training** them: casting a
spell you are still learning costs more and does nothing, but every cast brings you closer to mastering it.

> [!NOTE]
> Player spells are a work in progress. Eight spells exist today (below); the other nodes are `?` placeholders for
> spells still to come.

---

## 🌳 Spell Tree

New here? Craft a **Spell Book** (book, 2 amethyst shards and lapis lazuli; the recipe unlocks once you pick up an
amethyst shard or lapis), find a [Spell Tome](#-spell-tome) (Nobles drop them) to discover a spell, then open the
spell tree and click the yellow spell to start training it. While the book has no spells it tells you what to do
next: "Find a Spell Tome", then the Spell Tree key.

Press **K** (or click **Spell Tree** on the Player tab of the character sheet, **G**) to open the spell tree.

- **Knowledge** sits in the centre and is always learned: every spell grows from it
- **Damage** spells go up, **Defense** spells go left, **Utility** spells go down
- Each branch splits into elements: **Fire**, **Lightning** and **Ice**
- At first the tree only shows the tier 1 spells, all **hidden** (`?`): find a spell's [Spell Tome](#-spell-tome) to
  discover it. Discovering a spell reveals the next one after it (as `?`), and so on up the tree. The next time you
  open the tree, the spells you just discovered and revealed glow for a few seconds
- A spell can only be trained once it is discovered **and** the spell before it is learned (a tier 4 needs the tier 3,
  the tier 3 needs the tier 2...). Higher tiers are harder to learn

Node borders show each spell's state:

| Border | State |
|---|---|
| 🟩 Green | Learned |
| 🟩 Green with a gold star | Learned and mastered (level 10, see [Proficiency](#-proficiency)) |
| 🟦 Blue | In training |
| 🟨 Yellow | Can be trained (the spell before it is learned) |
| 🟥 Red, faded icon | Discovered, but locked: learn the spell before it first |
| 🟧 Orange | Learned, but not usable until the spells before it are learned |
| `?` | A spell you haven't discovered yet (its tooltip shows only its element and tier) |
| `X` | A spell that isn't in the game yet (**Coming soon**) |

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
| **Fire Bolt** | Fire | 20 | A ball of fire (7.2 damage) that sets the target ablaze for 4 seconds and everything within 1.5 blocks of it for 2 seconds |
| **Lightning Strike** | Lightning | 25 | A spark that calls lightning down on what it hits (9.6 damage) and hurts everything around it (3.6), even when it hits a wall |
| **Frost Shard** | Ice | 20 | A shard of ice (6 damage) that gives Slowness II for 3 seconds. Hitting a target that is already slowed makes it Slowness III |
| **Frost Ward** (Defense) | Ice | 25 | Wraps you in a shell of ice: Absorption II for 10 seconds, and enemies within 3 blocks are pushed back and get Slowness II for 3 seconds. If the shell breaks before it melts (the absorption is used up), it shatters: Slowness II again to the enemies around you. 10 second cooldown |
| **Chain Lightning** (tier 2, after Lightning Strike) | Lightning | 35 | A bolt (6 damage) that leaps from its target to up to 3 enemies within 5 blocks of each other (4 damage each). 1 second cooldown |
| **Glacial Spike** (tier 2, after Frost Shard) | Ice | 40 | **Hold** right-click for 1 second to charge, release to fire a spike of ice (10.4 damage) that gives Slowness IV for 4 seconds and chills everything next to it (3.2 damage, Slowness II). Releasing early wastes the charge. 2 second cooldown |
| **Mend** (Utility) | None | 30 | Heals 3 hearts and gives Regeneration I for 5 seconds. 5 second cooldown |
| **Flame Jet** (tier 2, after Fire Bolt) | Fire | 24 / s | **Hold** right-click to pour a stream of fire 5 blocks ahead: 3.6 damage twice a second to everything in it and sets it ablaze. 1 second cooldown after you stop |

Every spell has its own look: a rune circle at your feet when you cast, a visible projectile (a ball of fire, a
crackling spark, a shard of ice), and a burst and a ring of light where it lands. Glacial Spike gathers ice around
you while it charges, Flame Jet pours a real stream of embers, Frost Ward and Mend leave an aura around you while they
last.

Spells fly where you look. The basic attack spells have **no cooldown**: you can cast them as often as your mana
allows. Mana is shared with your Hero, so casting while your Hero is summoned drains the same pool that
keeps it in the world.

While casting, your character raises the book with both hands, and a puff in the spell's element colour shows at
your hand.

Spells never hurt your own Hero and follow the server's PvP and team rules: with PvP off, or between teammates without
friendly fire, they don't hurt other players.

The damage above is at level 1. See [Proficiency](#-proficiency).

---

## 🏅 Proficiency

Learning a spell is quick; mastering it takes a long time. Every learned spell has a **level** from 1 to 10, and every
element (**Fire**, **Lightning**, **Ice**) has its own **mastery** level from 1 to 10.

- Both grow with the **mana you spend** on real casts: the spell's level grows with the mana spent on that spell, the
  element's mastery with the mana spent on every spell of that element. Training casts give nothing
- Each **spell level** adds **+10% damage** (and healing), **+5% size** (area, reach, particle spread), denser particles and
  **-3% mana cost**
- Each **mastery level** adds **+5% damage** to every spell of that element
- In Creative mode spells cost no mana, so they gain no proficiency
- A spell can't level up past the spell before it: with Fire Bolt at level 1, Flame Jet stays at level 1 until Fire
  Bolt reaches level 2. The mana spent meanwhile still counts for the element's mastery

| Level | Spell, total mana spent | Time* | Mastery, total mana spent | Time* |
|---|---|---|---|---|
| 2 | 1,500 | 5 min | 3,000 | 10 min |
| 3 | 3,750 | 12 min | 7,500 | 25 min |
| 5 | 12,188 | 41 min | 24,375 | 1 h 21 |
| 8 | 48,259 | 2 h 40 | 96,516 | 5 h 21 |
| 10 | 112,331 | 6 h 14 | 224,661 | 12 h 28 |

\* Casting non-stop at the rate your mana regenerates (10 mana every 2 seconds, while your Hero is not summoned).

At spell level 10 and mastery 10, Fire Bolt deals **19.8** damage instead of 7.2 and costs 15 mana instead of 20.

A spell at level 10 is **mastered**: its node gets a gold star in the corner, its casts sparkle and its projectiles leave a
trail of light.

You get a message when a spell or a mastery goes up a level. The spell tree tooltip shows a learned spell's level,
its progress to the next one and its current bonuses; the book shows the level next to the cost; the Player tab of the
character sheet (**G**) shows the three masteries (hover one for details).

---

## 📜 Spell Tome

Every Noble can drop a **Spell Tome** of a random spell: green robes **25%** tier 1, yellow robes **25%** tier 1 and
**25%** tier 2, red robes **25%** tier 2 and **10%** tier 3 (see [Nobles](Nobles)). The creative tab has one tome of each spell.

**Right-click** a tome to study it: the spell is **discovered** and shows up in the spell tree. If the spell before it
is learned you can start training it right away; otherwise it stays locked (faded) until you learn that one. A tome
doesn't add training: the spell still has to be trained.

Once the spell is **learned**, more tomes of it add **10% of its current level** to its
[proficiency](#-proficiency) (ten tomes = one level; the element's mastery doesn't change). They can't be used while
the spell is discovered but not learned yet, at level 10, or while it is held back by the spell before it.

A tome's tooltip tells you where you stand with its spell: **Unknown to you**, **Discovered**, **Learned** with its
level (and the spell holding it back, if any) or **Mastered**.

A tome whose spell no longer exists becomes a **Faded Tome**.

---

## 🏆 Advancements

| Advancement | How |
|---|---|
| **Hidden Knowledge** | Discover a spell |
| **First Words** | Learn your first spell |
| **Adept** | Raise a spell to level 5 |
| **Archmage** (challenge) | Master Fire, Lightning and Ice to level 10 |

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
