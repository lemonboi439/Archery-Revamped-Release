# Archery Revamped Changelog

All notable changes to Archery Revamped are documented here.

---

## 1.2.0 - Arrow force, cleanup, and Headshot update

### Added

- Added experimental **Headshot I–III** enchantment support for bows and crossbows.
  - Detects the exact entity collision point against a configurable eye-centered region.
  - Works with any `LivingEntity`, including modded entities.
  - Applies configurable PvE and PvP damage bonuses after vanilla critical damage calculation.
  - Includes optional crit-particle and sound feedback.
  - Disabled by default with `enableHeadshot: false`.
- Added a dedicated Archery Revamped item group for special arrows and mod features.
- Added Headshot books to enchanting-table eligibility and late-game structure loot.

### Changed

- Renamed the former outward **Impulse Arrow** to **Shockwave Arrow**.
- Shockwave Arrows are crafted with Wind Charges and push entities away from their landing point.
- Added inward-pulling **Impulse Arrows**, crafted with Iron Nuggets.
- Improved Shockwave and Impulse area force:
  - affects entities across the configured radius;
  - uses gentler distance falloff;
  - applies stronger force to players.
- Fletching Table recipes now craft instantly instead of using a crafting-time delay.
- Fletching Table output continues stacking correctly and returns all table contents when closed.
- Fracture child counts scale with enchantment level and preserve projectile state.
- Custom enchantment level handling supports the safe maximum of 255 when enabled in configuration.
- Removed the `/archeryrevamped regular infinite_levels` command; the setting remains available in configuration.
- Updated the release version to `1.2`.

### Removed

- Removed Sticky Arrows and their Slowness behavior.
- Removed the Sharpshooter enchantment and its leggings behavior.
- Removed the unused global enchantment-limit mixin.

### Fixed

- Prevented Burst and Fracture child arrows from immediately colliding with the shooter.
- Preserved vanilla projectile state for custom, Burst, and Fracture arrows, including pickup behavior.
- Fixed custom arrow inheritance and persistence for world save/load.
- Fixed fletching output item creation and inventory return handling.

---

## 1.0.0 - Initial Release

### Added

- Custom `ArcheryArrowEntity` replacement for bow and crossbow projectiles while preserving vanilla behavior.
- Configurable projectile gravity, drag, speed, randomness, terminal velocity, and lifetime.
- Ricochet, Overdraw, Longshot, Fracture, and Burst enchantments.
- Ender, Impulse, Explosive, and the original Sticky Arrow features.
- Fletching Table interface and special-arrow recipes.
- Trajectory preview, live trails, collision prediction, and ricochet prediction.
- JSON configuration, Cloth Config, Mod Menu, hot reload, and `/archeryrevamped` commands.
- Enchanting-table support, late-game enchanted-book loot, villager trades, and optional JEI/REI/EMI compatibility.
