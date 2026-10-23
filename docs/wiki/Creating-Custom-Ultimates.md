# Creating Custom Ultimates

Ultimates are data-driven: each one is a JSON file built from small, reusable **effects** (damage, knockback,
particles, sounds...). A Hero picks its Ultimate with the `ultimate` field of its JSON, so several Heroes can share one.
The same effects also power Hero attacks (`on_attack` / `on_hit`, see
[Creating Custom Heroes](Creating-Custom-Heroes#-attack)).

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
  "description": "Your hero stands still and gathers power for 1 second, then explodes...",
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
| `description` | No | Short explanation shown in the Ultimate tab of the hero screen (**G**) |
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
| `"target"` | Attacks only: the creature being attacked or hit. In an Ultimate it hits nobody |
| `{ "area": 6 }` | Every creature within 6 blocks of the Hero (in an attack's `on_hit`: of the impact point) |
| `{ "line": 8, "width": 1.5 }` | Every creature on a straight line in front of the Hero, up to 8 blocks (stops at walls), within `width` blocks of it (default `1`) |

`area` and `line` never hit the Hero, the owner or their allies, and only hit other players when the owner could hurt
them (server PvP on, not teammates without friendly fire), like tamed wolves.

**Direction**: towards the point you (the owner) are looking at, up to 32 blocks away: the creature or block under
your crosshair, otherwise straight ahead of your view. It is taken when each phase starts, so for `on_end` you can aim
while the Hero channels. Lines, dashes and
particle trails all start where the Hero stood when the phase began, so in `on_end` you can damage the line before
or after the `dash`.

**Filter**: `damage`, `knockback`, `ignite` and `mob_effect` take an optional `filter` that narrows their target down.
Every field is optional and they all have to match:

| Field | Default | |
|---|---|---|
| `entity_tag` | none | Only creatures in this entity type tag, e.g. `minecraft:undead`, `minecraft:arthropod` |
| `hostile` | `false` | Only hostile creatures (zombies, skeletons, creepers, slimes...) |
| `exclude_target` | `false` | Skip the creature being attacked (attacks only), e.g. to hit only what stands behind it |

```json
{ "type": "damage", "target": "target", "filter": { "entity_tag": "minecraft:undead" }, "attack_ratio": 0.5 }
```

---

# ✨ Effects

Positions (sounds, particles, explosions) are taken from the Hero's current position (after a `dash`, its new one).

### `damage`
| Field | Default | |
|---|---|---|
| `target` | — | Who takes damage |
| `filter` | none | See **Filter** above |
| `amount` | `0` | Flat damage (2 = one heart) |
| `attack_ratio` | `0` | Extra damage as a share of the Hero's attack damage (`0.5` = half of it), so it grows with the Hero level |

In an attack's `on_hit`, damage to the attacked creature always lands on top of the hit itself (it ignores the
short invulnerability after being hit).

### `knockback`
Pushes targets away from the Hero, stronger the farther they are.

| Field | Default | |
|---|---|---|
| `target` | — | |
| `filter` | none | |
| `horizontal` | `0` | Multiplier of the distance to the Hero |
| `vertical` | `0` | Upward push |
| `scale_with_distance` | `true` | `false` makes the push the same at any distance (`horizontal` is then the push itself); use it for ranged attacks |

### `ignite`
| Field | Default | |
|---|---|---|
| `target` | — | |
| `filter` | none | |
| `ticks` | — | Burn time |

### `mob_effect`
| Field | Default | |
|---|---|---|
| `target` | — | |
| `filter` | none | |
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
| `line` | `0` | Above 0, draws a trail along the line in front of the Hero (same path as `line` targets and `dash`), with `count` particles every half block |

Colored dust: `{ "type": "minecraft:dust", "color": [0.75, 0.05, 0.1], "scale": 1.5 }` (RGB from 0 to 1).

### `sound`
| Field | Default | |
|---|---|---|
| `sound` | — | Sound id, e.g. `minecraft:entity.generic.explode` |
| `volume` | `1` | |
| `pitch` | `1` | |

### `dash`
Moves the Hero instantly in a straight line in front of it, stopping before walls.

| Field | Default | |
|---|---|---|
| `distance` | — | Blocks |

### `heal`
| Field | Default | |
|---|---|---|
| `target` | — | Who is healed, e.g. `"self"` or `"owner"` |
| `filter` | none | |
| `amount` | `0` | Flat healing (2 = one heart) |
| `max_health_ratio` | `0` | Extra healing as a share of the target's max health (`0.25` = a quarter) |

### `projectile`
Shoots projectiles from the Hero (from its attack's `projectile_origin`) towards the attacked creature, or in the
Ultimate's direction. They fly straight, never hit the owner and run their own `on_hit` effects where they land
(`"target"` there is the creature they hit, positions are the impact point).

| Field | Default | |
|---|---|---|
| `count` | `1` | How many projectiles |
| `spread` | `0` | Fan angle in degrees, split evenly between them |
| `speed` | `1.2` | Blocks per tick |
| `amount` | `0` | Flat damage of each projectile |
| `attack_ratio` | `0` | Extra damage as a share of the Hero's attack damage |
| `trail` | `{ "type": "minecraft:flame" }` | Trail and impact particle |
| `on_hit` | none | Effects applied where each projectile lands |

```json
{ "type": "projectile", "count": 5, "spread": 40, "attack_ratio": 1.5,
  "trail": { "type": "minecraft:soul_fire_flame" },
  "on_hit": [ { "type": "ignite", "target": "target", "ticks": 60 } ] }
```

### `lightning`
Calls a lightning bolt down where the effect happens (the impact point, or the Hero). Only visual: no fire and no
damage, so pair it with a `damage` effect. No fields.

```json
{ "type": "lightning" }
```

### `ultimate_animation`
Plays the `ultimate` animation of the Hero model (if it has one). No fields.

---

# 💥 Example: Explosion

```json
{
  "name": "Explosion",
  "description": "Your hero stands still and gathers power for 1 second, then explodes: every creature within 6 blocks takes heavy damage, is set on fire and is knocked back. You are never hit.",
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

# 🗡 Example: Crimson Requiem (Clare)

Channels for 1.5 seconds over a crimson mist, then dashes 8 blocks through everything in front of her.

```json
{
  "name": "Crimson Requiem",
  "description": "Clare stands still over a crimson mist for 1.5 seconds, then dashes 8 blocks towards where you are looking, stopping before walls. Everything in her path takes heavy damage and Wither II. Aim while she channels.",
  "mana_cost": 200,
  "channel_ticks": 30,
  "cooldown_ticks": 20,
  "on_start": [
    { "type": "ultimate_animation" },
    { "type": "mob_effect", "target": "self", "effect": "minecraft:slowness", "duration": 30, "amplifier": 4, "show_particles": false },
    { "type": "sound", "sound": "minecraft:block.respawn_anchor.charge", "volume": 1.5, "pitch": 1.3 }
  ],
  "on_tick": [
    { "type": "particles", "particle": { "type": "minecraft:dust", "color": [0.75, 0.05, 0.1], "scale": 1.2 }, "count": 3, "spread": [1.0, 0.05, 1.0], "height": 0.1 }
  ],
  "on_end": [
    { "type": "mob_effect", "target": { "line": 8, "width": 1.5 }, "effect": "minecraft:wither", "duration": 80, "amplifier": 1 },
    { "type": "damage", "target": { "line": 8, "width": 1.5 }, "amount": 18 },
    { "type": "particles", "particle": { "type": "minecraft:dust", "color": [0.75, 0.05, 0.1], "scale": 1.5 }, "count": 4, "spread": [0.2, 0.4, 0.2], "height": 1, "line": 8 },
    { "type": "particles", "particle": { "type": "minecraft:crit" }, "count": 2, "spread": [0.3, 0.3, 0.3], "speed": 0.1, "height": 1, "line": 8 },
    { "type": "dash", "distance": 8 },
    { "type": "particles", "particle": { "type": "minecraft:sweep_attack" }, "count": 1, "height": 1 },
    { "type": "sound", "sound": "minecraft:item.trident.riptide_1", "volume": 1.5, "pitch": 1.4 },
    { "type": "sound", "sound": "minecraft:entity.player.attack.sweep", "volume": 2.0, "pitch": 0.7 }
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
- [Nobles](Nobles) (spells use the same effects)
