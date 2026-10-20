# Known Issues

Tracked bugs and limitations that are known but not fixed yet. Ordered by severity.

## Low

### Hero-changing commands while the summon is out
`/althera_hero summon` and `/althera_hero rank` update `HeroData` while the entity is alive: the entity picks up the new hero id on its
next tick, but attributes are not re-applied and the entity health overwrites the new max health stored in `HeroData`.
