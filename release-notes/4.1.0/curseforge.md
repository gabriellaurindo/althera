**Althera 4.1.0 — The Nobles Rise** brings the first enemy faction: proud pureblood mages who guard shrines across the Overworld with heroes of their own. You also get a new way to command your hero, a new HUD and much stronger heroes at high levels.

## ✨ What's new

**The Noble** — a mini boss and the first member of the Nobles faction. He attacks players, their heroes and villagers with one spell (Fire Bolt, Frost Shard or Lightning Strike) and is bound to a level 20, 30 or 40 EX hero, shown by his robe (green, yellow or red). His hero waits beside him as a spirit and manifests when he finds a target, taking most of the damage meant for him. Each day he heals, revives and overdrives his hero with his own Command Seals and casts its Ultimate once. Slay him for 1 diamond, 3-4 diamonds or a diamond block, depending on his robe.

**Noble shrines** — rare quartz and gold platforms in the Overworld, each guarded by a Noble. When he falls, a new Noble with a new hero and spell appears one in-game day later.

**Seal key (R)** — tap **R** while looking at a creature or player to mark it as your hero's target. Hold **R** to open the **Command Seal wheel**: point at Overdrive, Revive or Heal and release to cast. J/K/V still work.

**Command Seal tattoo** — every player gets a seal tattoo on the right hand that glows in its colour when you cast a Command Seal or an Ultimate. Other players see it too. `/althera_seal color` rolls a new colour.

**Hero blink** — a melee hero that can't reach its target for 5 seconds (on a pillar, in water, in a boat...) vanishes and reappears next to it, hovering in the air if there is no ground.

**New hero HUD** — your hero's face, level, health, mana and interventions in the top-left corner. Move it in the mod's config (Mods > Althera > Config, or the new "Open Config" key).

**Advancements** — a new Althera tab with **Noble Hunter** and the challenge **High Noble Hunter**.

**For datapack creators** — custom spells (`spell/`), custom enemy mages and their shrines (`enemy_mage/`), and a `lightning` effect. See the wiki.

## 🔧 Changes
- Heroes grow much stronger with level: more health and attack, less armor (a level 50 Aria has about 213 health and 16 attack)
- Classes rebalanced so every hero fares about the same against strong mobs; Casters now dodge 5% and Archers 10%
- Ultimates scale with your hero's attack, and the Ultimate tab (G) shows their damage
- Hero health regeneration grows with level, so high-level heroes recover from a defeat much faster
- New `/althera_hero level <level>` command (cheats)
- The unused Summon seal item was removed; new creative tab icon
- Datapacks: `attack_ratio` / `max_health_ratio` are now `attack_multiplier` / `max_health_multiplier` (old names still work for now)

## 🐛 Fixes
- Heroes swim about ten times faster instead of floating almost still in water
- Heroes no longer stand still staring at each other after a hit pushes the enemy out of reach

⚠️ Clients and servers must both run 4.1.0.

💬 Join the community on Discord: https://discord.gg/mvkUSnWtm6
