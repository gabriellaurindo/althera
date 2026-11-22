# Noble Magic Academy

> The academy is a work in progress: more rooms, teachers and quests are still to come.

The **Noble Magic Academy** is a school of magic on a giant floating island, in a dimension of its own. It is where
summoners go to learn spells: its quests teach you new spells and send you to train them.

---

# 🏫 The Island

- A **hall** in the middle (where you arrive, with the portal home), a **library** to the north, the **Fire**,
  **Lightning** and **Ice** classrooms to the west, east and south, and a **training yard** to the south-east
- It is a **safe place**: no monsters, no PvP (players and their Heroes can't hurt each other), you can't break or
  place blocks, and explosions don't break anything. Your Hero can be summoned as usual
- Days and nights pass, but you **can't sleep** or set your respawn point there
- A **magic ward**, a faint violet dome over the island, follows its edge: walk up to it and it glows and pushes you
  back. Fall off and the ward brings you back to the hall
- The training yard has three **Training Dummies** (see [Spells](Spells#-training-dummy))
- Classrooms have desks for the students, the island has cherry, birch and oak trees, flowers and lamps along the
  paths, and its own music

---

# 🧙 Teachers

| Teacher | Where | Teaches |
|---|---|---|
| **Aldren**, Headmaster | Hall, in front of you when you arrive | The academy's main quests |
| **Ignis**, Master of Fire | Fire classroom (west) | Fire Bolt |
| **Volta**, Master of Lightning | Lightning classroom (east) | Lightning Strike |
| **Glacia**, Master of Ice | Ice classroom (south) | Frost Shard |
| **Mirel**, Librarian | Library (north) | A random basic spell, for a few books |

**Right-click** a teacher to talk: you see their quests (the same ones as in your [Journal](#-journal)) and can accept
or claim them there. Teachers can't be hurt and always come back to their place.

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
| `giver` | none | A teacher id (`althera:headmaster`): the quest shows up when talking to that teacher and the Journal shows their name. Any other text is shown as is |
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
| `kill` | `entity` (id or `#tag`), `count` (default 1), `name` (optional label) | You or your Hero defeat that many, after the quest shows up (also creatures that die soon after you or your Hero hurt them or set them on fire with a spell) |
| `item` | `item`, `count` (default 1), `consume` (default `true`) | You carry them; they are taken on claim unless `consume` is `false` |
| `visit_academy` | | You are in the academy |

### Rewards

| Type | Fields | Gives |
|---|---|---|
| `invitation` | | The invitation to the academy |
| `discover_spell` | `node` (optional), `tier` (default 1) | Discovers that spell, or a random undiscovered spell of that tier (a Spell Tome of that tier when all are known) |
| `item` | `item`, `count` (default 1) | Items |

### Teachers (datapacks)

Teachers are data-driven too: `data/<namespace>/academy_teacher/<name>.json` (the file name is the id used by quests'
`giver`).

```json
{
  "name": "Ignis",
  "title": "Master of Fire",
  "texture": "althera:textures/entity/teacher/fire.png",
  "position": [-19.5, 101, 0.5],
  "yaw": -90,
  "greeting": "Fire is patience that suddenly runs out."
}
```

| Field | Default | Description |
|---|---|---|
| `name` | required | Teacher's name |
| `title` | none | Shown after the name ("Ignis, Master of Fire") |
| `texture` | required | A 64x64 player skin texture |
| `position` | required | `[x, y, z]` in the academy (the hall's centre is `0.5, 101, 0.5`) |
| `yaw` | `0` | Facing: `0` south, `90` west, `180` north, `-90` east |
| `greeting` | `""` | What they say when you talk to them |

---

## 🔗 Related Pages

- [Spells](Spells)
- [Commands](Commands)
