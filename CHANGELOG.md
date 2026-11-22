# Changelog

All notable changes to Althera are documented here.
Entries go under `[Unreleased]` while developing; the release flow turns that section into a versioned one
and generates the GitHub Release, CurseForge and Discord notes from it.

## [Unreleased]

### Added
- **Player spells (work in progress)**: players can now learn and cast spells. Fire Bolt (20 mana), Lightning Strike
  (25) and Frost Shard (20) are the first ones; the other branches of the spell tree are `?` placeholders for now.
  - A spell in **training** casts at **double mana** and has no real effect, only a puff of magic; each training cast
    fills part of its progress. More max mana means fewer casts: a 20 mana spell takes 10 casts at 200 max mana and
    only 3 at 1000. Harder (higher tier) spells take longer.
  - Once **learned**, a spell casts at its normal cost with its full effect. Player spells hit as hard as a strong
    weapon (Fire Bolt 7.2, Frost Shard 6, Lightning Strike 9.6 plus 3.6 around the target), never hurt your own Hero
    and follow the server's PvP and team rules.
  - Training progress is kept on death.
- **Spell Tree** screen (**K**): Knowledge in the centre, Damage up, Defense left and Utility down. Node borders show
  each spell's state: green learned, blue training, yellow can be learned, red locked (learn the spell before it
  first), orange learned but not usable yet; `?` nodes are spells still to come. Hover a node for its cost, cooldown,
  tier, description and how many training casts are left at your current max mana.
  - **Click** a yellow node to start training it, click a blue one to pause (progress is kept).
  - The 9 book slots sit at the bottom: **drag** a spell onto a slot (or **right-click** it) to equip it, drag a slot's
    spell elsewhere or right-click the slot to clear it, click a slot to select it. Drag the background to move
    around, scroll to zoom.
- **Spell Book**: crafted from a book, two amethyst shards and a lapis lazuli. It has **9 spell slots**: a spell joins
  the first free slot when you start training it or learn it. **Right-click** with the book casts the selected spell
  (at double mana while training). Holding the book shows the selected spell, its cost, training progress and
  recharge next to the hotbar.
  - **V** (hold) opens the **spell wheel** with the 9 slots: point at one and release to select it. **Z** / **B**
    select the previous / next spell. With a book in hand, hold **Left Shift** and use the **mouse wheel** to scroll
    through the spells instead of the hotbar. All four can be changed in Controls.
  - Held spells are supported for datapacks: `charge` spells drain their mana while charging (`cast_ticks`) and fire
    on release, `continuous` spells drain their `mana_cost` per second while held (Flame Jet is the first).
- **Character sheet** (**G**): the hero screen now has two tabs on top, **Player** and **Hero**. The Player tab shows
  you, your mana, your Command Seal charges in your seal's colour, your spells (learned, training, selected) and a
  **Spell Tree** button (closing the tree brings you back to the sheet). The settings gear is on both tabs. The sheet
  reopens on the last tab you used.
  Without a Hero, **G** now opens the sheet on the Player tab, and the Hero tab opens Choose your Hero.
- **Spell Tome**: every Noble can drop one (see Changed below). The spell tree starts
  with only the tier 1 spells, hidden (`?`): right-click a spell's tome to **discover** it, then train it.
  Discovering a spell reveals the next one in the tree, and the new nodes glow the next time you open it. A tome's
  tooltip shows whether you know its spell (unknown, discovered, learned with its level, mastered). A spell
  discovered before the
  spell leading to it is learned stays locked (faded) until that one is learned. Once the spell is learned, each
  tome of it adds **10% of its current level** to its proficiency (not to the element's mastery). The creative tab
  has one tome per spell.
- A spell can't level up past the spell before it in the tree (with Fire Bolt at level 1, the next fire spell stays
  at level 1).
- **Spell proficiency**: every learned spell now has a **level** (1-10) and every element (Fire, Lightning, Ice) a
  **mastery** level (1-10). Both grow with the mana you spend on real casts (training casts don't count; nothing in
  Creative). Spell level 2 takes about 5 minutes of casting, mastery level 2 about 10, and each level after that
  takes 1.5 times longer: level 10 is about 6 hours of use for a spell and 12 for an element.
  - Each spell level adds +10% damage (and healing), +5% size (areas, reach, particle spread), denser particles and -3% mana cost.
    Each mastery level adds +5% damage to every spell of that element. At level 10 with mastery 10, Fire Bolt deals
    19.8 damage instead of 7.2 and costs 15 mana instead of 20.
  - A message and a sound announce every level up. The spell tree tooltip shows the level, progress and bonuses, the
    book and spell wheel show the level, and the Player tab of the character sheet shows the three masteries.
  - Datapacks: the spell's `player` block takes an optional `proficiency` block (`damage_per_level`,
    `size_per_level`, `visual_per_level`, `cost_per_level`).
  - Datapacks and Ultimates: new `chain` effect (leaps from the hit creature to the nearest enemies, running its
    `on_jump` effects on each).
  - Datapacks: new particles `althera:glow`, `ember`, `frost`, `spark`, `ring` and `rune` (colour, scale and lifetime
    of your choice, glowing); projectiles can have a `visual` (a texture that faces the camera or stretches along the
    flight); `particles` gets `direction_speed` and `circle`; new `aura` effect (particles circling a creature for a
    while, `on_break` effects when it ends early); spells get `on_charge` effects while being cast, charged or held;
    `mob_effect` gets `stack` and `max_amplifier`.
  - `/althera_spell level <node> <level>` and `/althera_spell element <element> <level>` (cheats).
- Spells and elements have their own **icons** (Fire Bolt, Lightning Strike, Frost Shard; Fire, Lightning, Ice and
  an arcane rune for the Knowledge root) in the spell tree, the book HUD, the spell wheel and the character sheet.
  Resource packs can add an icon for any spell at `textures/gui/spell/<spell>.png`; spells without one show their
  element's icon.
- New spells: **Frost Ward** (Defense, ice, 25 mana): Absorption II for 10 seconds and pushes back and slows the
  enemies around you, 10 second cooldown. **Flame Jet** (tier 2 fire, after Fire Bolt, 24 mana per second): hold
  right-click to pour a 5 block stream of fire that burns everything in it.
- New spells: **Chain Lightning** (tier 2 lightning, after Lightning Strike, 35 mana): a bolt that leaps from its
  target to up to 3 enemies nearby. **Glacial Spike** (tier 2 ice, after Frost Shard, 40 mana): hold to charge for a
  second, release to fire a spike of ice that hits hard and all but freezes the target. **Mend** (the first Utility
  spell, 30 mana): heals 3 hearts and gives Regeneration I for 5 seconds.
- Getting started with spells: the Spell Book recipe unlocks when you pick up an amethyst shard or lapis lazuli, and an
  empty Spell Book says what to do next ("Find a Spell Tome", then the Spell Tree key).
- The Spell Book and the Spell Tome have new textures.
- **Casting pose**: your character raises the book with both hands while casting (seen by everyone), the book lifts in
  first person while you hold a spell, and every cast puffs in the spell's element colour.
- **Mastered spells** (level 10) get a gold star in the spell tree, sparkle when cast and leave a trail of light.
- Advancements: **Hidden Knowledge** (discover a spell), **First Words** (learn a spell), **Adept** (a spell at level 5) and the challenge **Archmage** (Fire,
  Lightning and Ice mastery at 10).
- **Spells look the part**: a rune circle at your feet when you cast, visible projectiles (a ball of fire, a
  crackling spark, shards and spikes of ice that point where they fly), bursts, flashes and rings of light on impact,
  ice gathering around you while Glacial Spike charges, a real stream of embers for Flame Jet, a shell of ice around
  you during Frost Ward and healing lights during Mend. Nobles' spells get the same looks.
- **Fire Bolt** also sets everything within 1.5 blocks of its target ablaze (2 seconds). **Frost Shard** slows more:
  hitting an already slowed target gives Slowness III. **Frost Ward** shatters when its shell is broken early,
  slowing the enemies around you again.
- Player spells have **no cooldown** unless their spell says so: Fire Bolt, Lightning Strike and Frost Shard can be cast
  as often as your mana allows (Nobles keep their own cooldown on the same spells).
- Datapacks: spells take an optional `player` block (`mana_cost`, `description`, `cast_mode`, `cooldown_ticks`, default
  0 = no cooldown, separate from the spell's own `cooldown_ticks` used by mages) and the tree is defined
  in `data/<namespace>/spell_node/*.json` (`spell`, `parent`, `x`, `y`, `branch`, `element`, `tier`, `name`).
  See the new wiki pages "Spells" and "Creating Custom Spells" (the spell fields moved there from "Nobles").
- `/althera_spell info | reset | progress <node> <value> | train <node> | untrain <node> | cast <node> [player]`
  and `/althera_spell equip <slot> <node>` / `unequip <slot>` (cheats) to inspect, set and test spells.

- **Noble Magic Academy** (work in progress): a new dimension, a floating island in the sky with the school of magic
  (a hall, a library, Fire, Lightning and Ice classrooms and a training yard). It is a safe place: no monsters, no
  PvP, no breaking or placing blocks, no sleeping; a magic ward around the island, a faint violet dome that glows as
  you get close, keeps you from leaving or falling. Classrooms with desks and element decorations, trees, flowers,
  lamps along the paths and its own music.
  - **Academy Portal**: build a frame of **amethyst blocks** like a Nether portal (at least 2 wide and 3 tall
    inside) and right-click it with a **Spell Book** to open it (**100 mana**). Stand in it for 2 seconds to travel.
    Only players **invited** to the academy get through. The portal in the academy's hall takes you back where you
    came from.
  - **Journal** (**J**, or the new Journal button on the Player tab of the character sheet): your quests from the
    academy. The first one, **An Invitation**, is ready from the start: click Accept to get the invitation. The next
    ones send you to the academy, to learn and level spells and to defeat a Noble, and reward you with new spells.
    Quests are data-driven (`data/<namespace>/academy_quest/`, see the wiki page "Noble Magic Academy").
  - **Teachers**: the Headmaster in the hall, a master in each classroom (Fire, Lightning, Ice) and the librarian.
    Right-click one to talk and see their quests. The masters teach their element's basic spell, the librarian a
    random one for a few books. Teachers are data-driven too (`data/<namespace>/academy_teacher/`).
  - New players start in the academy the first time they join (server config `academy.spawnInAcademy`, on by
    default; turn it off for modpacks with their own starting map).

### Changed
- Your Hero now joins the fight when you hit a creature with a spell (or any ranged damage), as it does for melee hits.
- Creatures your Hero defeats, and creatures that burn to death from your spells' fire, count as yours: they count
  for quests and drop what a player kill drops (experience, player-only loot), like a tamed wolf's kills.
- **Noble drops**: Nobles no longer drop diamonds. They drop Spell Tomes instead: green **25%** a tier 1 tome; yellow
  **25%** a tier 1 tome and **25%** a tier 2 tome; red its Hero Catalyst, **25%** a tier 2 tome and **10%** a tier 3
  tome (no tier 3 spell exists yet).

## [4.3.0] - 2026-11-03 - Ritual Rework (feature)

### Added
- **Hero Catalyst**: High Nobles (red) now also drop a catalyst holding the **class and rank** of the Hero that
  served them, on top of their diamond block. Its name shows the class ("Saber Catalyst") in the rank's colour and its
  tooltip the rank; each class has its own icon. A catalyst whose class no longer exists (removed datapack) turns into
  a **Faded Catalyst**. Offered to the ritual next to the diamond block, it guarantees a Hero of **that class and
  rank** (which Hero of them is still random).
- `/althera_hero catalyst <class> <rank> [player]` (cheats) gives a catalyst; the creative tab has one EX catalyst
  per class.
- **Ritual Pedestal**: a new block that holds a single item, floating and slowly turning above it. Right-click with
  an item to place one, right-click again to take it back; breaking the pedestal drops what it holds. Crafted from
  four deepslate blocks (polished, bricks or tiles), two gold ingots and an amethyst shard. It offers the ritual its ingredients.

### Changed
- **Ritual rework**: the Ritual Core now draws a glowing **summoning circle** on the ground around it. Place a
  **Ritual Pedestal** on each of its four points (two blocks away from the core, one on each side) and keep the rest
  of the circle clear: each point lights up once it holds a pedestal, and the whole circle glows and pulses when the
  structure is complete. The ritual takes its ingredients from the pedestals: a diamond block rolls a random Hero as
  before; add a Hero Catalyst on another pedestal to choose the class and rank. Every ritual also costs **300 mana**,
  so your max mana must have grown past 300 first (it starts at 200 and grows while you regenerate). Nothing is
  spent when the ritual fails, and each failure tells you what is missing. Once everything is in place the ritual
  **channels for three seconds**: the circle brightens, its inner sigil spins faster and the offerings rise from their
  pedestals into the core, ending in a burst of light. Stay within 8 blocks of the core: walking away, taking an
  offering back, breaking a pedestal or anything that blocks the circle interrupts the ritual at no cost. The old
  structure (gold blocks, netherrack and the diamond block under the core) is no longer used: break it and place the
  pedestals instead.
  Ritual Cores placed before this update may need to be broken and placed again to show the circle.
- **New ritual art**: the Ritual Core is now a stepped altar of dark stone with gold trim and glowing amethyst
  runes, crowned by a floating amethyst crystal that turns above it, lights up once the circle is complete and rises
  and grows while the ritual channels. Ritual Pedestals got the same look: a slim column with a gold band and runes,
  topped by a gold-rimmed dish that holds the offering.
- **Ritual Core recipe** now matches its look: an amethyst block on top of two gold ingots around an obsidian,
  over three deepslate blocks (polished, bricks or tiles).
- **Smaller download**: the mod's textures were recompressed without any visual change and the hero screen's gear
  icon was redrawn as crisp 16x16 pixel art instead of a huge image, cutting the jar by about a third.

## [4.2.0] - 2026-10-31 - Hero Insight (feature + hotfix)

### Added
- **Skill timers**: every Command Seal or Ultimate that is running shows a small see-through square under the
  Hero HUD, with its icon and the seconds left: **green** while active, **red** while on cooldown, with a fill that
  drains as time runs out. The **Skill timers** setting (hero screen gear) moves them next to the crosshair instead.
- **Live hero stats**: while your Hero is summoned, the hero screen (G) shows its current health, damage and armor
  with every buff and debuff applied (Overdrive, thrown potions...): green with the bonus when above normal, red when
  below. Its active effects show as icons on its portrait, with name, level and time left on hover. While disguised,
  the lowered stats show in red against the Hero's normal values.
- The hero screen's status also shows **speed** and **damage resistance** (from Resistance, e.g. Overdrive).
- **Hero details panel**: the arrow on the right edge of the hero screen opens a side panel with knockback
  resistance, dodge, regeneration, Command Seal charges left today and whether the Ultimate is ready, plus the class
  multipliers (HP, ATK, DEF, SPD) and which classes it is strong (+25%) and weak (-25%) against.
- **Creative mode**: Command Seals and the Ultimate are unlimited (no charges, cooldowns or once-per-day limit).
- **Ultimate ready again**: when a new day restores a used Ultimate, a chime and a chat message let you know.
- **HUD settings in the hero screen**: the gear tab (G) now has every option in one place: Disable interventions,
  Hide HUD, HUD corner, horizontal/vertical offset and skill timers position, with a description on hover and a
  **Reset** link for the HUD position. The NeoForge config screen no longer repeats them.
- **Custom hero sounds and hitbox**: hero JSONs accept `sounds` (`hurt`, `death`, `step`) and `hitbox` (`width`,
  `height`). A disguised hero keeps the default sounds and size.

### Changed
- A disguised Hero is faster: it keeps **75%** of its speed (was 50%).
- Command Seals are now cast only through the **Command Seal wheel** (hold **R**). The J/K/V shortcuts were removed.
  The crosshair and the skill timers next to it are hidden while the wheel is open.
- The **Open Config** key was removed: the options live in the hero screen gear tab (G).
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
