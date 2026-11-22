# Noble Magic Academy

> The academy is a work in progress. Teachers and more of the school are still to come.

The **Noble Magic Academy** is a school of magic on a giant floating island, in a dimension of its own. It is where
summoners go to learn spells: its quests teach you new spells and send you to train them.

---

# 🏫 The Island

- A **hall** in the middle (where you arrive, with the portal home), a **library** to the north, the **Fire**,
  **Lightning** and **Ice** classrooms to the west, east and south, and a **training yard** to the south-east
- It is a **safe place**: no monsters, no PvP (players and their Heroes can't hurt each other), you can't break or
  place blocks, and explosions don't break anything. Your Hero can be summoned as usual
- Days and nights pass, but you **can't sleep** or set your respawn point there
- A **magic ward** follows the edge of the island: walk up to it and it pushes you back. Fall off and the ward brings
  you back to the hall

---

# 🌀 Academy Portal

1. Build a frame of **amethyst blocks** like a Nether portal: at least **2 wide and 3 tall** inside (up to 21x21).
   The corners are optional
2. Right-click the frame with a **Spell Book**: it costs **100 mana** and opens the portal
3. Stand in the portal for **2 seconds**

Only players **invited** to the academy get through: the invitation comes from the first quest of your
[Journal](#-journal). The portal in the hall takes you back where you came from.

Breaking a frame block closes the portal. Datapacks can change the frame blocks with the block tag
`althera:academy_portal_frame`.

### Starting in the academy

New players start in the academy's hall the first time they join. Servers and modpacks with their own starting map
can turn it off in the server config (`academy.spawnInAcademy` in `althera-server.toml`).

---

# 📜 Journal

Press **J** (or the **Journal** button on the Player tab of the character sheet, **G**) to open your Journal. It
lists your quests: the ones you can do now and the ones you finished.

- Your first quest, **An Invitation**, is waiting from the start: open the Journal and click **Accept** to get your
  invitation. Until you do, it stays there
- A quest's tasks update on their own (spells you discover, learn or level up, creatures you defeat, items you
  carry). When they are all done, click **Claim** to get the rewards; items asked by a quest are taken then
- Finishing a quest unlocks the next ones
- When you join with a quest ready to accept or claim, a message in the chat reminds you

---

# 🧪 Creating Quests (datapacks)

Quests are data-driven: `data/<namespace>/academy_quest/<name>.json`. The file name is the id
(`althera:invitation`). They reload with `/reload`.

```json
{
  "title": "Practice Makes Perfect",
  "giver": "Headmaster of the Noble Magic Academy",
  "order": 30,
  "requires": ["althera:first_words"],
  "description": "A spell grows with every cast. Use one until it reaches level 2.",
  "tasks": [ { "type": "spell_level", "level": 2 } ],
  "rewards": [ { "type": "discover_spell", "tier": 1 } ]
}
```

| Field | Default | Description |
|---|---|---|
| `title` | required | Name shown in the Journal |
| `giver` | none | Who gives the quest ("From: ...") |
| `description` | `""` | Quest text |
| `order` | `0` | Position in the Journal (lowest first) |
| `requires` | `[]` | Quests that must be finished before this one shows up |
| `tasks` | `[]` | What to do. A quest without tasks is accepted at once |
| `rewards` | `[]` | What finishing it gives |

### Tasks

| Type | Fields | Done when |
|---|---|---|
| `discover_spell` | `node` (optional) | The spell (or any spell) is discovered |
| `learn_spell` | `node` (optional) | The spell (or any spell) is learned |
| `spell_level` | `node` (optional), `level` (2-10) | The spell (or any spell) reaches that level |
| `kill` | `entity` (id or `#tag`), `count` (default 1), `name` (optional label) | You or your Hero defeat that many, after the quest shows up |
| `item` | `item`, `count` (default 1), `consume` (default `true`) | You carry them; they are taken on claim unless `consume` is `false` |
| `visit_academy` | | You are in the academy |

### Rewards

| Type | Fields | Gives |
|---|---|---|
| `invitation` | | The invitation to the academy |
| `discover_spell` | `node` (optional), `tier` (default 1) | Discovers that spell, or a random undiscovered spell of that tier (a Spell Tome of that tier when all are known) |
| `item` | `item`, `count` (default 1) | Items |

---

## 🔗 Related Pages

- [Spells](Spells)
- [Commands](Commands)
