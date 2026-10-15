# Known Issues

Tracked bugs and limitations that are known but not fixed yet. Ordered by severity.

## Medium

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
