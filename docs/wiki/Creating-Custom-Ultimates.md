# Creating Custom Ultimates

Ultimates are data-driven: each one is a JSON file built from small, reusable **effects** (damage, knockback,
particles, sounds...). A Hero picks its Ultimate with the `ultimate` field of its JSON, so several Heroes can share one.

---

# 📁 Ultimate File Location

```text
data/<your_namespace>/ultimate/
```

The file name is the Ultimate id. Example: `data/althera/ultimate/explosion.json` is `althera:explosion`.

Point a Hero at it in the Hero JSON (see [Creating Custom Heroes](Creating-Custom-Heroes)):

```json
"ultimate": "althera:explosion"
```

Heroes without `ultimate` use `althera:explosion`. Ultimates reload with `/reload`.

---

# 🧩 Structure

```json
{
  "name": "Explosion",
  "mana_cost": 200,
  "channel_ticks": 20,
  "cooldown_ticks": 20,
  "on_start": [ ... ],
  "on_tick": [ ... ],
  "on_end": [ ... ]
}
```

| Field | Required | Description |
|---|---|---|
| `name` | No | Display name (default `Ultimate`) |
| `mana_cost` | Yes | Mana spent on activation |
| `channel_ticks` | Yes | How long the Hero channels before `on_end` (20 ticks = 1 second) |
| `cooldown_ticks` | No | Cooldown after activation (default `0`) |
| `on_start` | No | Effects applied when the Ultimate is activated |
| `on_tick` | No | Effects applied every tick while channeling |
| `on_end` | No | Effects applied when channeling finishes |

`on_end` only runs if the channel completes: if the Hero is defeated or dismissed while channeling, the Ultimate is
cancelled. Every Ultimate can still be used only **once per day**.

Effects run in the order they are listed.

---

# 🎯 Targets

Effects that act on creatures take a `target`:

| Target | Who |
|---|---|
| `"self"` | The Hero |
| `"owner"` | The Hero's owner (you) |
| `{ "area": 6 }` | Every creature within 6 blocks of the Hero, except the Hero and the owner |

---

# ✨ Effects

Positions (sounds, particles, explosions) are taken from the Hero.

### `damage`
| Field | Default | |
|---|---|---|
| `target` | — | Who takes damage |
| `amount` | — | Damage (2 = one heart) |

### `knockback`
Pushes targets away from the Hero, stronger the farther they are.

| Field | Default | |
|---|---|---|
| `target` | — | |
| `horizontal` | `0` | Multiplier of the distance to the Hero |
| `vertical` | `0` | Upward push |

### `ignite`
| Field | Default | |
|---|---|---|
| `target` | — | |
| `ticks` | — | Burn time |

### `mob_effect`
| Field | Default | |
|---|---|---|
| `target` | — | |
| `effect` | — | Effect id, e.g. `minecraft:slowness` |
| `duration` | — | Ticks |
| `amplifier` | `0` | `0` = level I |
| `show_particles` | `true` | |

### `explosion`
A visual/sound explosion at the Hero. It never breaks blocks.

| Field | Default | |
|---|---|---|
| `power` | `0` | Above 0 it also damages creatures nearby, including you |

### `particles`
| Field | Default | |
|---|---|---|
| `particle` | — | e.g. `{ "type": "minecraft:flame" }` |
| `count` | `1` | |
| `spread` | `[0, 0, 0]` | Spread on X, Y, Z |
| `speed` | `0` | |
| `height` | `0` | Height above the Hero's feet |

### `sound`
| Field | Default | |
|---|---|---|
| `sound` | — | Sound id, e.g. `minecraft:entity.generic.explode` |
| `volume` | `1` | |
| `pitch` | `1` | |

### `ultimate_animation`
Plays the `ultimate` animation of the Hero model (if it has one). No fields.

---

# 💥 Example: Explosion

```json
{
  "name": "Explosion",
  "mana_cost": 200,
  "channel_ticks": 20,
  "cooldown_ticks": 20,
  "on_start": [
    { "type": "ultimate_animation" },
    { "type": "mob_effect", "target": "self", "effect": "minecraft:slowness", "duration": 20, "amplifier": 4, "show_particles": false },
    { "type": "sound", "sound": "minecraft:block.beacon.power_select", "volume": 2.0, "pitch": 0.5 }
  ],
  "on_end": [
    { "type": "knockback", "target": { "area": 6 }, "horizontal": 0.35, "vertical": 0.6 },
    { "type": "ignite", "target": { "area": 6 }, "ticks": 60 },
    { "type": "damage", "target": { "area": 6 }, "amount": 12 },
    { "type": "explosion" },
    { "type": "particles", "particle": { "type": "minecraft:explosion_emitter" }, "height": 1 },
    { "type": "particles", "particle": { "type": "minecraft:flame" }, "count": 80, "spread": [1.5, 1.0, 1.5], "speed": 0.05, "height": 1 },
    { "type": "particles", "particle": { "type": "minecraft:smoke" }, "count": 50, "spread": [1.2, 0.8, 1.2], "speed": 0.03, "height": 1 },
    { "type": "sound", "sound": "minecraft:entity.generic.explode", "volume": 3.0, "pitch": 0.8 }
  ]
}
```

---

# ⚠ Notes

- An invalid Ultimate file fails to load and is reported in the game log; Heroes pointing to it have no Ultimate
- More effects and targets will be added over time

---

# 🔗 Related Pages

- [Creating Custom Heroes](Creating-Custom-Heroes)
- [Command Seals & Ultimates](Command-Seals-and-Ultimates)
