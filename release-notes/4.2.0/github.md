## Althera 4.2.0 — Hero Insight

Minecraft 1.21.1 · NeoForge 21.1.219 · requires GeckoLib 4.7+

> Network protocol changed: clients and servers must both run 4.2.0.

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

**Full Changelog**: https://github.com/gabriellaurindo/althera/compare/althera-v4.1.0-neoforge-1.21.1...althera-v4.2.0-neoforge-1.21.1

💬 Join the community on Discord: https://discord.gg/mvkUSnWtm6
