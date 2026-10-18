# Changelog

All notable changes to Althera are documented here.
Entries go under `[Unreleased]` while developing; the release flow turns that section into a versioned one
and generates the GitHub Release, CurseForge and Discord notes from it.

## [Unreleased]

### Added
- **Custom Ultimates**: Ultimates are now data-driven. Datapacks can create their own in `data/<namespace>/ultimate/`
  by combining effects (damage, knockback, fire, mob effects, explosions, particles, sounds and the hero's
  ultimate animation) on activation, while channeling and when channeling ends, and give one to each hero with the
  new `ultimate` field of the hero JSON. See the wiki page "Creating Custom Ultimates".

### Changed
- Each hero now has its own Ultimate. Aria and Clare still use Explosion, which works exactly as before.
- Using the Ultimate without your hero summoned now tells you to summon it.

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
