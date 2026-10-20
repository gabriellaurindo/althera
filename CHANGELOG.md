# Changelog

All notable changes to Althera are documented here.
Entries go under `[Unreleased]` while developing; the release flow turns that section into a versioned one
and generates the GitHub Release, CurseForge and Discord notes from it.

## [Unreleased]

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

### Changed
- Class balance: Sabers have slightly less health, attack and armor (×1.1 instead of ×1.15), Archers and Casters a
  bit more health (×0.9 and ×0.8) and Shielders more attack (×0.85), so no class wins most duels on stats alone.
- Heroes now move at their class speed: Assassins and Lancers are faster, Shielders slower. The speed shown on the
  Choose your Hero screen was never applied before.
- Attack animations play in full instead of being cut short (Aria, Clare and Zaya).
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
