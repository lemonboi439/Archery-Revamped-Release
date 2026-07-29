# Archery Revamped Changelog

All notable changes to Archery Revamped are documented here.

---

## 1.0.0 - Initial Release

### Added

#### Enchantments

- Added **Ricochet I–V**
  - Arrows bounce off blocks.
  - Configurable velocity loss.

- Added **Overdraw I–II**
  - Allows bows to charge beyond their normal maximum.
  - Increased damage while overcharging.
  - Self-damage, bow damage and forced firing when held too long.

- Added **Longshot I**
  - Increases arrow damage based on distance travelled.

- Added **Fracture I–II**
  - Splits arrows into additional projectiles during flight.
  - Disabled by default.

- Added **Burst I–III**
  - Fires additional delayed arrows.
  - Each projectile consumes ammunition and durability.

- Added **Sharpshooter I–III**
  - Leggings enchantment reducing movement slowdown while drawing ranged weapons.
  - Disabled by default.

#### Special Arrows

- Added **Ender Arrows**
  - Teleport the shooter to the impact location.

- Added **Impulse Arrows**
  - Push nearby entities away from the impact.

- Added **Explosive Arrows**
  - Explode on impact.

- Added **Sticky Arrows**
  - Apply Slowness to living targets.

#### Arrow System

- Added custom `ArcheryArrowEntity`.
- Added support for Bows and Crossbows.
- Preserved vanilla:
  - Damage
  - Critical hits
  - Power
  - Punch
  - Flame
  - Piercing
  - Multishot
  - Infinity
  - Arrow pickup behaviour
- Added persistent custom projectile data.

#### Projectile Physics

- Added configurable gravity.
- Added configurable drag.
- Added configurable projectile speed.
- Added configurable firing randomness.
- Added configurable terminal velocity.
- Added configurable arrow lifetime.

#### Fletching Table

- Added experimental custom Fletching Table interface.
- Added crafting for:
  - Ender Arrows
  - Impulse Arrows
  - Explosive Arrows
  - Sticky Arrows
  - Tipped Arrows
- Added crafting progress and animation.

#### Trajectory System

- Added experimental pre-shot trajectory preview.
- Added live projectile trails.
- Added future path prediction.
- Added block and entity collision prediction.
- Added Ricochet prediction.
- Added smooth line rendering without particles or preview entities.
- Added optional velocity colour visualisation.

#### World Integration

- Added custom enchantments to the Enchanting Table.
- Added enchanted books to structure loot.
- Added special arrow and ingredient villager trades.

#### Configuration

- Added JSON configuration system.
- Added Cloth Config support.
- Added Mod Menu support.
- Added hot config reload.
- Added `/archeryrevamped` command system.

#### Compatibility

- Added JEI integration.
- Added REI integration.
- Added EMI integration.

### Known Issues

- Burst follow-up arrows may occasionally fire at an incorrect angle.
- Fracture is disabled by default while balancing and behaviour are refined.
- Sharpshooter is disabled by default and may be reworked.
- The Fletching Table interface is experimental.
- Trajectory rendering is experimental.
