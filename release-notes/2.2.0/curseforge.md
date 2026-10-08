**Althera 2.2.0 — Seals, Ultimate & Stability** brings your hero its first active abilities, plus a big round of stability fixes.

## ✨ What's new

**Command Seals** — 3 charges per day (they recharge at the start of a new day or after sleeping):
- **Overdrive** (J · 100 mana): your hero gains Strength, Resistance and Speed for 30 seconds. Power comes at a price: when it ends, your hero falls and can't be revived until it fully recovers.
- **Revive** (K · 150 mana): brings a defeated hero back at full health, right by your side.
- **Heal** (V · 50 mana): fully heals your summoned hero.

**Ultimate — Explosion** (U · 200 mana): your hero channels for a moment and then explodes, damaging, burning and knocking back nearby enemies. Once per day.

All new keys can be changed in Options → Controls (Althera category).

**Your hero always finds you**: after teleports (`/tp`, ender pearls), logging in, respawning or changing dimensions, your hero — or its spirit — comes back to you.

**New commands**
- `/althera_hero unstuck` — if your summon ever gets stuck, this resets it to a recovering state (you keep your hero, level and xp).
- `/althera_mana refill` — refills mana (cheats only).

## 🔧 Changes
- Commands were renamed: `/hero summon` → `/althera_hero summon`, `/hero rank` → `/althera_hero rank`.
- Logging out dismisses your hero.
- Skills only spend mana and charges when they can actually be used.
- Revive only costs its own mana.

## 🐛 Fixes
- Fixed a server crash when Overdrive ended.
- Your hero no longer disappears when you change dimensions.
- Fixed heroes getting left behind when they died while you were offline or in another dimension.
- The spirit orb now comes back after logging in or respawning.
- Fixed heroes that never finished recovering after being defeated.
- Damage interception no longer wastes an intervention, and ignores self-inflicted damage like ender pearls.
- Performance improvement for the spirit orb.
