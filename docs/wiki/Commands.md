# Commands

All Althera chat commands follow the pattern `/althera_<feature> <subcommand>`.

Commands marked with **cheats** require cheats enabled (singleplayer) or operator permission (servers).

---

## 🧍 Hero — `/althera_hero`

| Command | Permission | Description |
|---|---|---|
| `/althera_hero unstuck` | Everyone | Fixes your summon if it gets stuck in an inconsistent state |
| `/althera_hero unstuck <player>` | Cheats | Same, for another player |
| `/althera_hero summon` | Cheats | Assigns you a random Hero |
| `/althera_hero rank <rank>` | Cheats | Assigns you a random Hero of the given rank (`F`, `E`, `D`, `C`, `B`, `A`, `S`, `EX`) |
| `/althera_hero divine` | Cheats | Assigns you a random Divine Hero |
| `/althera_hero level <level>` | Cheats | Sets your Hero's level (1-50), resets its experience and heals it to full |
| `/althera_hero level <level> <player>` | Cheats | Same, for another player |
| `/althera_hero catalyst <class> <rank>` | Cheats | Gives you a [Hero Catalyst](Ritual-System#-hero-catalyst) of the given class (for example `althera:saber`) and rank |
| `/althera_hero catalyst <class> <rank> <player>` | Cheats | Same, for another player |

### What `unstuck` does

- Removes any copy of your Hero left in the world
- Puts your Hero in **spirit form** with zero health, recovering over time
- Keeps your Hero, level and experience

Use it if your Hero cannot be summoned or behaves inconsistently.

---

## 🔮 Mana — `/althera_mana`

| Command | Permission | Description |
|---|---|---|
| `/althera_mana refill` | Cheats | Refills your mana |
| `/althera_mana refill <players>` | Cheats | Refills the mana of the given players |

---

## ✋ Command Seal — `/althera_seal`

| Command | Permission | Description |
|---|---|---|
| `/althera_seal color` | Everyone | Rolls a new colour for your Command Seal tattoo (see [Command Seals & Ultimates](Command-Seals-and-Ultimates#-command-seal-tattoo)) |

---

## 📖 Spells — `/althera_spell`

Every command takes an optional `<player>` at the end (default: you). `<node>` is a spell tree node id, for example
`althera:fire_bolt` (see [Spells](Spells)).

| Command | Permission | Description |
|---|---|---|
| `/althera_spell info` | Cheats | Lists every node of the spell tree with its state, training progress and level, then the element masteries |
| `/althera_spell reset` | Cheats | Forgets every spell, training progress, book slot, spell level and mastery |
| `/althera_spell progress <node> <value>` | Cheats | Sets a spell's training progress (`0` to `1`; `1` = learned) |
| `/althera_spell discover <node>` | Cheats | Discovers a spell, as its Spell Tome does |
| `/althera_spell forget <node>` | Cheats | Hides a spell again: stops its training and clears its progress (proficiency is kept) |
| `/althera_spell train <node>` | Cheats | Discovers a spell and starts training it (the spell before it must be learned) |
| `/althera_spell untrain <node>` | Cheats | Pauses a spell's training (progress is kept) |
| `/althera_spell cast <node>` | Cheats | Casts a spell without a Spell Book (instant spells only), paying its mana |
| `/althera_spell equip <slot> <node>` | Cheats | Puts a spell in a book slot (1-9) |
| `/althera_spell unequip <slot>` | Cheats | Clears a book slot (1-9) |
| `/althera_spell level <node> <level>` | Cheats | Sets a spell's [proficiency](Spells#-proficiency) level (1-10) |
| `/althera_spell element <element> <level>` | Cheats | Sets an element's mastery level (1-10): `fire`, `lightning` or `ice` |

Spell Tomes have no command: take them from the creative tab, or use
`/give @s althera:spell_tome[althera:spell_tome={node:"althera:fire_bolt"}]`.

---

## 🧙 Enemy mages — `/althera_mage`

| Command | Permission | Description |
|---|---|---|
| `/althera_mage spawn <mage>` | Cheats | Spawns an enemy mage where you stand, for example `althera:noble` (see [Creating Custom Enemy Mages](Creating-Custom-Enemy-Mages)) |

---

## 🏫 Academy — `/althera_academy`

See [Noble Magic Academy](Noble-Magic-Academy).

| Command | Permission | Description |
|---|---|---|
| `/althera_academy tp [player]` | Cheats | Sends a player to the academy's arrival point |
| `/althera_academy return [player]` | Cheats | Sends a player back to the world spawn |
| `/althera_academy invite [player]` | Cheats | Gives a player the invitation (lets them through the portal) |
| `/althera_academy uninvite [player]` | Cheats | Takes the invitation back |
| `/althera_academy rebuild` | Cheats | Builds the academy island again (overwrites its blocks) |

---

## 📜 Quests — `/althera_quest`

| Command | Permission | Description |
|---|---|---|
| `/althera_quest complete <quest> [player]` | Cheats | Finishes a quest and gives its rewards, whatever its tasks |
| `/althera_quest reset [player]` | Cheats | Forgets every quest (rewards already given are kept) |

---

## 🔗 Related Pages

- [Core Systems](Core-Systems)
- [Command Seals & Ultimates](Command-Seals-and-Ultimates)
- [Spells](Spells)
