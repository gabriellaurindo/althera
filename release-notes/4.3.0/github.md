## Althera 4.3.0 — Ritual Rework

Minecraft 1.21.1 · NeoForge 21.1.219 · requires GeckoLib 4.7+

> New blocks and items: clients and servers must both run 4.3.0.

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

**Full Changelog**: https://github.com/gabriellaurindo/althera/compare/althera-v4.2.0-neoforge-1.21.1...althera-v4.3.0-neoforge-1.21.1

💬 Join the community on Discord: https://discord.gg/mvkUSnWtm6
