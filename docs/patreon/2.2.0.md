# Althera 2.2.0 — Seals, Ultimate & Stability

Hey everyone! 2.2.0 is out, and it's the first update where your hero stops being "just" a companion and starts having real abilities you control.

## ✨ What's new

### Command Seals
In the Fate spirit, you now have **3 Command Seals per day** — limited, powerful orders you give your hero. They recharge at the start of each new day (sleeping counts too).

- **Overdrive (J · 100 mana)** — your hero gets Strength, Resistance and Speed for 30 seconds. It's a real gamble: when it ends, your hero falls, and it can't be revived until it has fully recovered.
- **Revive (K · 150 mana)** — brings a defeated hero back at full health, right next to you.
- **Heal (V · 50 mana)** — fully heals your summoned hero.

### Ultimate — Explosion (U · 200 mana)
Your hero channels for a moment and then explodes, damaging, burning and knocking back everything around it. It's the big "once per day" move, so pick your moment.

### Your hero always finds you
Teleports, ender pearls, respawning, changing dimensions, logging back in — your hero (or its spirit orb) now comes back to you every time, instead of getting lost somewhere in the world.

### New commands
- `/althera_hero unstuck` — a safety net if your summon ever gets into a weird state: it resets it to "recovering" while keeping your hero, level and xp.
- `/althera_mana refill` — for testing with cheats on.

## 🛠️ Behind the scenes
A lot of this update was about making the hero system solid before adding more on top of it:
- The whole summon life cycle (summon, dismiss, defeat, revive) was rebuilt so every case goes through one place — that's what fixed the server crash with Overdrive and the hero disappearing between dimensions.
- Skills now decide for themselves what happens when they end, are cancelled or the hero is defeated. This is the groundwork for defining future skills entirely in data files.
- The spirit orb no longer searches huge areas of the world to find itself, which was heavy on servers.
- Commands were reorganized under `/althera_...` (e.g. `/althera_hero summon`).

## 📥 Download
CurseForge: https://www.curseforge.com/minecraft/mc-mods/althera

Thank you so much for supporting Althera — it genuinely keeps this project going. ❤️
