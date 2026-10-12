# Known Issues

Tracked bugs and limitations that are known but not fixed yet. Ordered by severity.

## Medium

### Hero loader fallbacks and empty registry
- `HeroLoader` defaults a missing `rank` to `"COMMON"`, which is not a valid `HeroRank`, so a hero without `rank`
  always fails to load.
- If no hero loads at all (broken datapack), `HeroRollSystem#rollHero` calls `nextInt(0)` and crashes on the first summon.
- `/althera_hero divine` is commented out in `HeroCommand`: no divine hero can load while only rank `EX` is supported.

### Ritual does not end active skills
`RitualCoreBlock#performRitual` removes the summon with `HeroEntity#remove()` instead of
`HeroSummonSystem#dismissSummon`, so active command seal skills/ultimates are not ended
(e.g. an active Overdrive stays frozen).

## Low

### Hero-changing commands while the summon is out
`/althera_hero summon` and `/althera_hero rank` update `HeroData` while the entity is alive: the entity picks up the new hero id on its
next tick, but attributes are not re-applied and the entity health overwrites the new max health stored in `HeroData`.

### Hero removed from the datapack
If a player's hero id no longer exists, `getHeroDefinition()` returns `null` and the next summon silently rolls a new
random hero (level and xp are kept). No message is shown.

### Skill timers are not synced every tick
Active/cooldown timers are decremented server-side without marking the attachment dirty; the client only receives them
when a skill starts/ends. Any future HUD that shows remaining time will display stale values.
