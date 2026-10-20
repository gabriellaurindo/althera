## Althera 4.0.0 — Data-Driven Combat

Minecraft 1.21.1 · NeoForge 21.1.219 · requires GeckoLib 4.7+

> Network protocol changed: clients and servers must both run 4.0.0.

### Added
- **Custom Ultimates**: Ultimates are now data-driven. Datapacks can create their own in `data/<namespace>/ultimate/`
  by combining effects (damage, knockback, fire, mob effects, explosions, dashes, particles, sounds and the hero's
  ultimate animation) on activation, while channeling and when channeling ends, and give one to each hero with the
  new `ultimate` field of the hero JSON. See the wiki page "Creating Custom Ultimates".
- **Dodge**: heroes can now evade attacks, depending on their class. Assassins dodge 10% of attacks; Sabers,
  Lancers and Archers 5%; Casters and Shielders never dodge. Explosions can't be dodged. A dodge during an
  intervention still protects you. The hero screen (G) shows your hero's dodge chance.
- **Crimson Requiem**, Clare's own Ultimate: she channels over a crimson mist for 1.5 seconds, then dashes 8 blocks
  towards where you are looking (aim while she channels), dealing heavy damage and Wither II to everything in her
  path and leaving a crimson trail. She stops before walls.
- **Ultimate tab** in the hero screen (G): click **Ultimate** to see your hero's Ultimate, with its key, mana cost,
  channel time, whether it is ready today and a short explanation of how it works.
- **Zaya**, a new EX hero of the Caster class: a flame-sworn mage in crimson and gold with knee-length red hair, a
  flame staff and a grimoire at her hip. She uses Explosion as her Ultimate.
- **Ranged attacks**: Zaya now fights from a distance, hurling fire bolts from the tip of her staff at enemies up to 12
  blocks away while keeping out of their reach, backing off when they get close. Her bolts set what they hit on fire.
- **Custom attacks**: datapacks can shape how each hero fights with the new `attack` field of the hero JSON: melee or
  ranged, attack speed, range, projectile speed, trail and where it leaves the weapon, plus `on_attack` / `on_hit`
  effects built from the same effects as Ultimates (fire, knockback, mob effects, explosions, particles, sounds...).
  See the wiki page "Creating Custom Heroes".
- Every hero now has a signature attack:
  - **Aria – Judgment**: as a paladin, her strikes deal 50% extra damage to hostile mobs and another 50% to the
    undead, with a flash of holy light.
  - **Valka – Piercing Thrust**: her spear goes through her target, hitting whoever stands a couple of blocks behind it
    for 60% of her damage, and pushes the target back.
  - **Clare**: her blades inflict a short Wither I.
- **Custom classes**: hero classes are now data-driven. Datapacks can tweak the built-in ones or add new ones in
  `data/<namespace>/hero_class/` (health, attack, armor and speed multipliers, knockback resistance, dodge chance and
  which classes they beat).
- **Hero stats and animation timing** in the hero JSON: base health, attack, armor and speed (and how they grow per
  level), plus attack and walk animation speed and the length of the attack and death animations.
- **New effects** for Ultimates and attacks: `heal` (flat or a share of max health) and `projectile` (one or a fan of
  projectiles with their own on-hit effects).
- **Class advantages** now matter when heroes fight each other: a hero deals 25% more damage to the class it beats
  (with a burst of crits) and 25% less to the class that beats it. Every class beats two classes and loses to two:
  Saber beats Caster and Lancer, Caster beats Lancer and Shielder, Lancer beats Shielder and Archer, Shielder beats
  Archer and Assassin, Archer beats Assassin and Saber, Assassin beats Saber and Caster.
- **Valka**, a new EX hero of the Lancer class: a swift spear fighter in light steel armor and teal cloth with a high
  honey-blonde ponytail. She uses Crimson Requiem as her Ultimate.
- **Disguise**: hold the summon key (**H**) to summon your hero disguised as a hooded figure with a shadowed face, or
  to swap a summoned hero between its true form and the disguise. A tap still summons and dismisses as before.
  Hiding is instant, in a puff of smoke; revealing takes half a second of rising particles before a flash of light,
  so everyone nearby sees it coming.
  While disguised your hero loses half of its base health, attack and armor (what it gained from levels is kept, so
  a high-level hero stays strong), half its speed, knockback resistance and dodge chance, can't use its Ultimate and
  attacks without its signature effects, but costs only a quarter of the mana to keep summoned.
  Other players can't tell who it is: its name, model and stats stay hidden, even from tooltip mods like Jade or
  WAILA. The disguise ends when the hero is dismissed or defeated.
- **Max level**: heroes now stop at level 50. The hero screen (G) shows **MAX** instead of the XP needed.
- **Armor grows with level**: heroes gain armor as they level up (0.15 per level before the class, rank and nature
  multipliers), so at level 50 an EX Shielder has about 21 armor, an EX Saber about 14 and an EX Caster about 9.
  Datapacks can tune it with the new `armor_per_level` field in the hero `stats`.

### Changed
- **Mana exhaustion**: when your hero is dismissed for lack of mana, you must recover half of your max mana before
  it can be summoned again (by you or by an intervention), so it no longer pops in and out every few seconds. The
  mana bar pulses red and marks the halfway point until then.
- **Faster leveling**: heroes earn five times more experience per kill (half the defeated creature's max health:
  10 for a zombie, 50 for an Iron Golem, 250 for the Warden), and monsters you kill yourself while your hero is
  summoned now give it half of that. Reaching level 10 takes about 75 zombies instead of almost 370.
- Class balance: Sabers have slightly less health, attack and armor (×1.1 instead of ×1.15), Archers and Casters a
  bit more health (×0.9 and ×0.8) and Shielders more attack (×0.85), so no class wins most duels on stats alone.
- Heroes now move at their class speed: Assassins and Lancers are faster, Shielders slower. The speed shown on the
  Choose your Hero screen was never applied before.
- Attack animations play in full instead of being cut short (Aria, Clare and Zaya).
- Zaya's upper corset lacing now matches the lower one (crossed laces with gold beads).
- Zaya's attack animation keeps the staff in her hand instead of sliding it out of her grip.
- Zaya's Ultimate animation: she raises her staff with both hands and slams it into the ground, right when the
  explosion goes off.
- Mana is infinite in Creative mode: summoning, keeping your hero out, Command Seals and the Ultimate cost nothing, and
  the mana bar shows ∞.
- Each hero now has its own Ultimate. Aria keeps Explosion, which works exactly as before.
- Using the Ultimate without your hero summoned now tells you to summon it.
- The one-time hero switch from 3.0.0 is over: the **Choose your Hero** screen now only appears for players who don't
  have a hero yet. To change heroes, use the ritual.
- Removed the placeholder heroes that had no model of their own. If your hero was one of them, the **Choose your
  Hero** screen opens on login and your hero level carries over to the hero you pick.

### Fixed
- A resting hero could recover above its max health (e.g. 39/38) because the recovery rounded the max up.
- Hero fights no longer fizzle out: a hero remembers the player whose hero (or who) fought it for 30 seconds, renewed
  on every hit, and keeps going after that hero, chasing it up to 32 blocks. When the enemy hero falls or is dismissed,
  it goes after its owner instead, if PvP allows it.
- Command Seal messages (hero needed, no charges, no mana, cooldown) were in Portuguese; they are now in English and
  say what to do.
- `/althera_hero summon`, `rank` and `divine` with the hero out now update it right away (stats and full health)
  instead of keeping the old max health.
- Heroes now follow the server's PvP setting, like tamed wolves: with PvP off (or between teammates without friendly
  fire) they never attack other players, even when hit by one, and their shots and Ultimates don't hurt them.
  Heroes of different players can still fight each other.
- Heroes fight hordes properly: they now hit whichever enemy is attacking them within reach instead of chasing a single
  target through the crowd, keep fighting nearby enemies that are attacking them or you when their target dies, and no
  longer drop the enemy in front of them to run after a mob you hit some time ago.
- Heroes are now hard to push around: they resist knockback depending on their class, so a crowd of mobs can no
  longer bat them away from their target. Shielders can't be pushed at all, Sabers and Lancers barely move, and
  Archers, Assassins and Casters still get pushed a little.

**Full Changelog**: https://github.com/gabriellaurindo/althera/compare/althera-v3.0.1-neoforge-1.21.1...althera-v4.0.0-neoforge-1.21.1

💬 Join the community on Discord: https://discord.gg/mvkUSnWtm6
