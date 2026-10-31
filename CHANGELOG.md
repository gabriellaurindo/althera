# Changelog

All notable changes to Althera are documented here.
Entries go under `[Unreleased]` while developing; the release flow turns that section into a versioned one
and generates the GitHub Release, CurseForge and Discord notes from it.

## [Unreleased]

### Added
- **Skill timers**: every Command Seal or Ultimate that is running shows a small see-through square under the
  Hero HUD, with its icon and the seconds left: **green** while active, **red** while on cooldown, with a fill that
  drains as time runs out. The **Skill timers** setting (hero screen gear) moves them next to the crosshair instead.
- **Live hero stats**: while your Hero is summoned, the hero screen (G) shows its current health, damage and armor
  with every buff and debuff applied (Overdrive, thrown potions...): green with the bonus when above normal, red when
  below. Its active effects show as icons on its portrait, with name, level and time left on hover.
- The hero screen's status also shows **speed** and **damage resistance** (from Resistance, e.g. Overdrive).
- **Hero details panel**: the arrow on the right edge of the hero screen opens a side panel with knockback
  resistance, dodge, regeneration, Command Seal charges left today and whether the Ultimate is ready, plus the class
  multipliers (HP, ATK, DEF, SPD) and which classes it is strong (+25%) and weak (-25%) against.
- **Creative mode**: Command Seals and the Ultimate are unlimited (no charges, cooldowns or once-per-day limit).
- **Ultimate ready again**: when a new day restores a used Ultimate, a chime and a chat message let you know.
- **HUD settings in the hero screen**: the gear tab (G) now has every option in one place: Disable interventions,
  Hide HUD, HUD corner, horizontal/vertical offset and skill timers position, with a description on hover. The
  NeoForge config screen no longer repeats them.
- **Custom hero sounds and hitbox**: hero JSONs accept `sounds` (`hurt`, `death`, `step`) and `hitbox` (`width`,
  `height`). A disguised hero keeps the default sounds and size.

### Changed
- Command Seals are now cast only through the **Command Seal wheel** (hold **R**). The J/K/V shortcuts were removed.
- The **Open Config** key was removed: the options live in the hero screen gear tab (G).
  The crosshair and the skill timers next to it are hidden while the wheel is open.
- The skill slots next to the hotbar are gone. The Ultimate now shows as a **golden sword** beside your Hero's portrait
  in the Hero HUD, faded once used today; seal charges show in the Command Seal wheel.

### Fixed
- Valka no longer inflicts Wither: she shared Clare's Crimson Requiem. Her new Ultimate, **Tempest Charge**, is the
  same 8-block charge in teal, without the Wither, and throws enemies aside with a lancer's knockback.
- The Ultimate key was listed as "Explosion" in the Controls menu; it is now "Ultimate".

## [4.1.0] - 2026-10-29 - The Nobles Rise (feature + hotfix)

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

## [4.0.0] - 2026-10-20 - Data-Driven Combat (feature + hotfix)

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

## [3.0.1] - 2026-10-17 - Clean Screenshots (hotfix)

### Fixed
- The mana bar and the hero HUD now hide together with the rest of the interface when you press F1, like the skill
  HUD already did.

## [3.0.0] - 2026-10-17 - Choose your Hero (feature + hotfix)

### Added
- **Skill HUD**: slots next to the hotbar for Overdrive (J), Revive (K), Heal (V) and Explosion (U). Each slot shows
  its key, the remaining time while the skill is active or on cooldown, and grays out when you lack mana or Command
  Seal charges. Ultimates already used today show a red ✕. Remaining Command Seal charges appear above the slots.
  The HUD follows the hero HUD visibility setting.
- **Choose your Hero** screen: players without a hero now pick their first one instead of getting a random roll. It
  opens on login (or when you press the summon or hero key) and shows each hero with an animated preview, rank,
  class, description and class stats. Every hero with its own model, texture and animations can be chosen; for now
  that is Aria (Saber) and Clare (Assassin).
  - **This version only:** players who already have a hero also get the screen once on login and can switch to
    another hero, keeping their hero level and XP (or keep their current one). Closing the screen keeps your hero
    and shows it again on your next login until you decide. This one-time switch will be removed in the next
    version.
- All hero ranks are enabled: F, E, D, C, B, A, S and EX, each with its own roll chance. Only heroes with their own
  model, texture and animations can be rolled, so for now every roll gives one of the two EX heroes, Aria or Clare;
  new heroes join the roll as their visuals are added.
- **Clare**, a new EX hero of the new Assassin class: a gothic heroine with long silver hair, crimson eyes, X
  hairpins, a black and crimson dress with a lace jabot, detached sleeves, thigh-high buckled boots, a torn
  crimson-lined overskirt and a gothic rapier with a ruby on the guard. Her hair, locks and overskirt sway as she
  moves, and she has her own idle, walk, attack, ultimate and death animations. During her ultimate a spinning red
  magic circle, about 4 blocks wide, opens at her feet.
- **Assassin class**: fast and hard-hitting but fragile (less health and armor, more attack and speed). Strong
  against Casters, weak against Shielders.
- `/althera_hero divine` (cheats) is enabled again: assigns a random Divine hero, when one is available.

### Changed
- **Notice:** hero models, textures and animations are work in progress. They can and will change constantly between
  versions until they reach their final quality.
- **Aria** has a brand new model, texture and animations: a paladin with a long cape, flowing hair and ponytail, a
  detailed face, and new idle, walk, attack, ultimate and death animations. During her ultimate, golden wings sprout
  behind her and then fade away.
- Aria and Clare are now about as tall as the player, and the hero hitbox matches the player's (1.8 blocks). The hero
  screens still show them at full size. Datapack heroes can set an optional `scale` in their JSON to match.
- The hero's name no longer floats above its head.
- Redesigned the hero screen (G): larger preview with the name, rank and class under it, cleaner level, XP and health
  bars, and damage, armor and interventions left. The settings tab explains each option, and "Disable Saves" is now
  called "Disable interventions". Both hero screens have an x button to close them.
- New look for the mana bar: framed bar with a blue gradient, a soft shine, 10% marks and smooth animation, with a
  light trail showing the mana you just spent. The value now sits to the right of the bar, and the frame pulses red
  when you have less than 20 mana (the cost to summon).
- The ritual now swaps your hero: it rolls a new hero, never the one you already have (rank chances still apply).
  Your hero level and XP carry over, and the new hero starts at full health. If no other hero is available, the
  ritual fails without consuming the diamond block.
- Explosion ultimate: while channeling, the hero plays its ultimate animation (when its model has one) instead of
  getting a white glowing outline.
- Heroes with a death animation (Aria, Clare) now collapse to the ground on defeat. Heroes without one keep the classic
  sideways tip-over.

### Fixed
- Summoning or performing the ritual with no hero available now shows a message instead of crashing.
- Hero datapacks: a hero without `rank` now fails to load with a clear error instead of an invalid default.
- Dedicated servers: the summon HUD, the hero screen (G) and custom hero models/textures (e.g. Aria) now work for
  every player. Hero stats shown on the client also match the server again. Heroes are re-sent after `/reload`.

## [2.2.1] - 2026-10-11 - License Update (maintenance)

### Changed
- New license, split by part like Applied Energistics 2: code under LGPLv3, artwork (textures, models, animations)
  under CC BY-NC-SA 4.0 (credit required, no commercial use), text and translations under CC0. Videos, streams and screenshots of the
  mod are allowed, including monetized ones. Versions up to 2.2.0 stay under MIT.

## [2.2.0] - 2026-10-08 - Seals, Ultimate & Stability (feature + hotfix)

### Added
- **Command Seals**: 3 charges per day (reset at the start of a new day or after sleeping), each with its own key:
  - **Overdrive** (J, 100 mana): Strength, Resistance and Speed for the hero for 30 seconds. When it ends — or if the
    hero is dismissed before — the hero is defeated, and it cannot be revived until it fully recovers.
  - **Revive** (K, 150 mana): revives a defeated hero at full health and summons it.
  - **Heal** (V, 50 mana): fully heals the summoned hero.
- **Ultimate — Explosion** (U, 200 mana): the hero channels for a second and then explodes, damaging, burning and
  knocking back nearby enemies. Each ultimate can be used once per day.
- The new keys (J, K, V, U) can be rebound in Options → Controls, under the Althera category.
- `/althera_hero unstuck [player]`: fixes a summon stuck in an inconsistent state. The hero becomes defeated with zero
  health and recovers over time; level, xp and hero are kept. Any player can use it on themselves.
- `/althera_mana refill [players]` (cheats): refills mana.
- The hero and the spirit orb come back to the player after any teleport (`/tp`, ender pearls, other mods), login,
  respawn and dimension change.

### Changed
- Chat commands now follow `/althera_<feature>`: `/hero summon` → `/althera_hero summon`,
  `/hero rank` → `/althera_hero rank`. `/hero divine` is temporarily disabled.
- Logging out dismisses the summon. Active skills end as if the hero was dismissed (an active Overdrive defeats it).
- Skills check their own requirements before consuming mana or charges (e.g. Revive only works on a defeated hero).
- Revive only costs its own mana; summoning the revived hero needs no extra mana.
- The Explosion ultimate is cancelled if the hero is defeated or dismissed while channeling.
- Hero loading errors are reported through the game log.

### Fixed
- Server crash when Overdrive ended with the hero still alive.
- Hero disappearing instead of following the player through a dimension change.
- Duplicate defeat message when the hero died with Overdrive active.
- Hero left behind (and mana regeneration blocked) when it died while the owner was offline or in another dimension.
- Spirit orb missing after logging in or respawning.
- Possible crash when looking up the spirit orb of a player who is offline.
- Spirit orb lookup scanning a 10,000-block area in every dimension.
- Damage interception using up an intervention when the hero did not absorb the hit, and triggering on self-inflicted
  damage (e.g. ender pearls).
- Defeated heroes whose max health was not a whole number never finished recovering and could not be summoned again.
