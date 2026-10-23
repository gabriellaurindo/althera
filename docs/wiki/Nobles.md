# Nobles

The **Nobles** are the first enemy faction of Althera: pureblood mages who carry the Goddess's blood and despise
everyone who does not. Each Noble is bound to a Hero of his own, and fights you the same way you fight: with a
servant, Command Seals and an Ultimate.

---

## 🧙 The Noble

A white-haired mage in gold-trimmed robes. He is a mini boss: persistent (he never despawns) and with unlimited
mana. He has a player's base stats (20 health, no armor) plus **25%** of his Hero's health, attack and armor.

### Who he attacks
- Players
- Players' Heroes
- Villagers

Nobles never attack each other or each other's Heroes.

### Spells
Each Noble knows **one spell**, chosen when he appears:

| Spell | Effect |
|---|---|
| **Fire Bolt** | A fire projectile that sets its target on fire |
| **Frost Shard** | A fast ice shard that slows its target |
| **Lightning Strike** | Calls lightning on its target, also hurting whoever stands next to it |

He fights from a distance: he closes in when you are out of range and backs off when you get too close.

---

## 👻 His Hero

Every Noble is bound to a random **EX** Hero of **level 20, 30 or 40**. His robe shows which:

| Robe | Hero level |
|---|---|
| 🟩 Green | 20 |
| 🟨 Yellow | 30 |
| 🟥 Red | 40 |

- While the Noble has no target, his Hero waits beside him in **spirit form** (a small light at his side) and slowly
  recovers health.
- As soon as he finds a target, the Hero **manifests** next to him and attacks it.
- After about 10 seconds without a target, the Hero returns to spirit form, keeping its health.

### Damage split
While his Hero lives, the Noble only takes **25%** of any damage. The other **75%** hits his Hero directly, even in
spirit form, and the Hero turns on whoever dealt it. Defeat the Hero first, or wear it down by hitting the Noble.

If the Hero falls and cannot be revived, it is **gone for good**: that Noble never summons it again.

---

## 📜 Command Seals and Ultimate

A Noble has **3 Command Seals per day**, one of each, used in this order:

1. **Heal**: when his Hero drops to 20% health, it is fully healed.
2. **Revive**: when his Hero falls, it comes back at full health a second later.
3. **Overdrive**: after Revive was used, when his Hero drops to 30% health it gets Strength I, Resistance I and
   Speed I for 30 seconds, and then falls for good.

When his Hero drops to **half health**, the Noble casts the Hero's own **Ultimate**, once per day.

Seals and the Ultimate come back each new day, like a player's.

---

## 🏛 Noble Shrines

Nobles only appear at **Noble shrines**: rare quartz platforms in the Overworld with four quartz pillars topped with
gold and lanterns, and a **Noble Sigil** in the middle.

- Each shrine is guarded by one Noble, who stays near it.
- When he is slain, a new Noble (with a new Hero and spell) appears **one in-game day later**, as soon as a player
  comes within 32 blocks.
- The Noble Sigil can't be mined for loot. It is available in the creative tab.

---

## 🧩 Custom Spells (datapacks)

Spells are data-driven. They live in:

```
data/<namespace>/spell/<spell_name>.json
```

| Field | Default | Description |
|---|---|---|
| `name` | `"Spell"` | Display name |
| `range` | `16` | Distance (blocks) the caster casts from |
| `cooldown_ticks` | `40` | Ticks between casts |
| `cast_ticks` | `0` | Ticks spent casting before the spell goes off |
| `on_cast` | `[]` | Effects applied when the spell goes off |

`on_cast` uses the same effects and targets as Ultimates (see
[Creating Custom Ultimates](Creating-Custom-Ultimates)). The spell's target is the caster's current target.

Example (Fire Bolt, `data/althera/spell/fire_bolt.json`):

```json
{
  "name": "Fire Bolt",
  "range": 16,
  "cooldown_ticks": 40,
  "cast_ticks": 10,
  "on_cast": [
    { "type": "sound", "sound": "minecraft:entity.blaze.shoot", "volume": 1.0, "pitch": 0.9 },
    {
      "type": "projectile",
      "speed": 1.3,
      "amount": 6,
      "trail": { "type": "minecraft:flame" },
      "on_hit": [
        { "type": "ignite", "target": "target", "ticks": 80 }
      ]
    }
  ]
}
```

> **Note:** Nobles currently pick among the three built-in spells (`althera:fire_bolt`, `althera:frost_shard`,
> `althera:lightning_strike`). A datapack can change how they behave by overriding those files; new spell files load
> but are not used by anyone yet.

---

## 🔗 Related Pages

- [Core Systems](Core-Systems)
- [Command Seals & Ultimates](Command-Seals-and-Ultimates)
- [Creating Custom Ultimates](Creating-Custom-Ultimates)
