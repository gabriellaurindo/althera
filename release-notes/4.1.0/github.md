## Althera 4.1.0 — The Nobles Rise

Minecraft 1.21.1 · NeoForge 21.1.219 · requires GeckoLib 4.7+

> Network protocol changed: clients and servers must both run 4.1.0.

### Added
- **Command Seal wheel**: hold **R** to open a wheel with Overdrive, Revive and Heal. Point at one with the mouse and
  release R to cast it; release in the centre to cancel. Each seal shows its mana cost and whether it is ready, active,
  on cooldown or blocked (no charges, not enough mana, hero summoned or not, hero not defeated for Revive). J/K/V
  still work as direct shortcuts.
- **Hero blink**: a melee Hero that can't get any closer to its target for 5 seconds (you hid in water, on a pillar,
  in a boat...) vanishes and reappears next to it. With no ground there, it hovers in the air beside its target for up
  to 4 seconds. This works for your Hero and for a Noble's Hero alike.
- **Mark a target**: tap **R** while looking at a creature or player (up to 32 blocks) and your hero attacks it,
  ignoring other fights, until it dies, gets more than 48 blocks away or 30 seconds pass. Only you see the mark.
  Marking a player only works where your hero could hurt them (PvP on, not on your team).
- **Command Seal tattoo**: every player gets a seal tattoo on the back of the right hand, in a random
  colour. Casting a Command Seal or your hero's Ultimate makes it glow in that colour and raises your hand in first
  person, with sparks and a chime. Other players see the tattoo and the glow too. Don't like the colour? `/althera_seal color`
  rolls a new one.
- New wiki page **Heroes**: every hero you can summon, with rarity, class matchups, combat style, signature attack and
  Ultimate.
- `/althera_hero level <level> [player]` (cheats): sets a hero's level (1-50), resets its XP and heals it to full
  (a defeated hero stays in spirit form).
- **Lightning effect** for custom Ultimates and hero attacks: `{ "type": "lightning" }` calls a lightning bolt down
  where the effect happens. It is only visual (no fire, no damage), so pair it with a `damage` effect.
- **Spells**: datapacks can define spells in `data/<namespace>/spell/` (name, range, cooldown, cast time and a list of
  effects on cast, the same effects Ultimates use). Fire Bolt, Frost Shard and Lightning Strike come with the mod and are cast by Nobles. See the wiki page "Nobles".
- **Noble**, a mini boss and the first member of the Nobles faction: a white-haired pureblood mage who attacks players, their
  heroes and villagers with one spell (Fire Bolt, Frost Shard or Lightning Strike) from range, backing off only when a target gets right next to
  him. While his hero is fighting he follows the fight away from his shrine. He is bound
  to a level 20, 30 or 40 EX hero that waits beside him as a spirit and manifests when he finds a target. His robe
  shows the hero's level: green (20), yellow (30) or red (40). He has a player's base stats plus a quarter of his
  hero's health, attack and armor. While his hero lives, the Noble only takes a quarter of the damage: the rest hits his hero, even in spirit form.
  Each day he has three Command Seals: he heals his hero when it drops to 20% health, revives it once when it
  falls and, after that, overdrives it at 30% health (stronger, tougher and faster for 30 seconds, then it falls for
  good). When his hero drops to half health he casts its own Ultimate, once per day. If his hero falls for good, it
  never comes back. A Noble slain by a player or their hero drops a reward based on his robe: 1 diamond (green),
  3-4 diamonds (yellow) or a diamond block (red).
- **Noble shrines**: rare quartz and gold platforms in the Overworld, with four lantern-topped pillars and a **Noble
  Sigil** in the middle. A Noble guards each shrine and stays near it; when he is slain, a new Noble (with a new hero
  and spell) appears one in-game day later, as soon as a player comes close. The sigil can be found in the creative tab.
- **Custom enemy mages**: datapacks can define enemy mages in `data/<namespace>/enemy_mage/` (name, faction, stats,
  spells, targets, Hero, robes per Hero level, Command Seals, Ultimate and shrine settings). The Noble is one of them
  (`althera:noble`), so a datapack can also rebalance him. Mages of different factions fight each other, and a
  datapack can add shrines that spawn its own mage. See the wiki page "Creating Custom Enemy Mages".
- `/althera_mage spawn <mage>` (cheats): spawns an enemy mage (for example `althera:noble`) where you stand.
- When a Noble's Hero manifests, players within 32 blocks see it in chat ("The Noble calls forth Aria!").
- An **Althera** advancements tab: **Noble Hunter** (defeat a Noble) and the challenge **High Noble Hunter** (defeat a
  High Noble).

### Changed
- **Hero health regeneration grows with level**: every level adds 0.1 health per 2 seconds in spirit form and 0.05
  while summoned (level 1: 2 and 1, level 50: 6.9 and 3.45). High-level heroes no longer take minutes to recover from
  a defeat.
- **Hero balance and power buff**: heroes gain much more health (1.65 per level instead of 1) and attack (0.18 per
  level instead of 0.125) as they level up, but less armor (0.06 per level instead of 0.15), so high-level heroes
  hit harder and survive through health instead of armor. A level 50 Aria now has about 213 health and 16 attack
  (was 133 and 12). Classes were rebalanced so every Hero fares about the same against strong mobs: Clare and other
  Assassins no longer end a fight against an Iron Golem almost dead while Aria keeps most of her health. Class
  matchups stay the same (each class still beats the next two), and Saber vs Shielder, Caster vs Archer and Lancer
  vs Assassin are now close fights. Casters gain a 5% dodge chance and Archers 10%.
- New icon for the Althera creative tab: the golden sword of the Althera emblem.
- Removed the unused **Summon seal** item from the creative tab. Command Seals are cast from the new wheel instead.
- **New hero HUD** in the top-left corner: your hero's face, its level, a health bar, your mana bar and the
  interventions as three green diamonds. The mana bar moved here from above the hotbar. Pick its corner and
  offset in the mod's config (Mods > Althera > Config, or the new "Open Config" key, unbound by
  default).
- **Ultimates now scale with your hero's attack** instead of dealing fixed damage, so they keep up as your hero levels
  up. Explosion deals 180% of the hero's attack to everything around it, Crimson Requiem 210% along its path. At low
  levels they hit softer than before, at high levels much harder. The Ultimate tab of the Hero screen (G) now shows
  its damage, for example "180% ATK (21.2)".
- **Noble spells scale with the Noble's attack**: Fire Bolt, Frost Shard and Lightning Strike hit harder on Nobles
  with stronger heroes (yellow and red robes).
- Datapack fields renamed: `attack_ratio` is now `attack_multiplier` (`damage` and `projectile` effects) and
  `max_health_ratio` is now `max_health_multiplier` (`heal` effect). **The old names still work for now, but support
  will be removed in a future version**: update your datapacks.

### Fixed
- Heroes swim about ten times faster: they used to float almost still in water, an easy target for anyone fighting
  them from there.
- Heroes no longer stand still staring at each other in melee fights: after a hit pushes the enemy out of reach,
  the Hero now walks back to it right away.

**Full Changelog**: https://github.com/gabriellaurindo/althera/compare/althera-v4.0.0-neoforge-1.21.1...althera-v4.1.0-neoforge-1.21.1

💬 Join the community on Discord: https://discord.gg/mvkUSnWtm6
