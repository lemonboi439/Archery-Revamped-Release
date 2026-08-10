# Archery Revamped Changelog

All notable changes to Archery Revamped are documented here.

---

## 1.6.2 - 26.1 Port and Release Polish

### Changed

- Ported the full 1.6 feature set to Minecraft 26.1 with official Mojang mappings.
- Added normal-level custom enchanted books to the Archery Revamped creative tab. The optional infinite-level setting never populates creative inventory with high-level books.
- Corrected the bundled Shockwave and Impulse defaults to `radius: 1.0` and `strength: 1.0`, matching their configured wind-charge-equivalent baseline.

### Fixed

- Verified the Fletching Table's virtual output flow: inputs are consumed only when crafting succeeds and only real inputs return when the screen closes.
- Restored the full potion-tint rendering path for custom tipped arrows after the 26.1 mapping migration.

---

## 1.6 - Fletching Table Rework

### Changed

- Replaced the temporary vanilla-anvil-style Fletching Table art with the supplied 256×256 interface texture.
- The Fletching Table now behaves as a two-input crafting table: four arrows in the first slot, a modifier in the second slot, and a non-consuming four-arrow output preview.
- Input slots now reject invalid items: the first slot accepts arrows only, and the second accepts recognised recipe modifiers only.
- Disabled vanilla crafting-table tipped-arrow conversion; every non-blank arrow is now made through the Fletching Table.
- Matched vanilla tipped-arrow quantities in the Fletching Table: eight arrows plus any valid potion now yields eight tipped arrows.
- Quiver selection is now hold-to-select: hold `V`, cycle with `Q`/`E`, and release `V` to confirm.
- Echo Arrow sculk conversion now uses the slain entity's vanilla experience value for its spread budget, adds sparse sculk veins, and has a 1% chance to place a functional shrieker, sensor, or catalyst above the new sculk.
- Left-clicking the output crafts one batch into the cursor; repeated clicks continue crafting and merge compatible results into that cursor stack.
- Shift-clicking the output crafts the maximum possible number of batches into the player inventory.
- The screen now explicitly renders the `Fletching Table` title independently of the texture.
- Added optional Trinkets Updated support: the dedicated chest/quiver slot accepts only a Quiver and can override inventory Quiver priority through configuration.
- Added `general.limitless_anvil`, disabled by default, for servers that want to remove the vanilla "Too Expensive" anvil gate while retaining normal costs and compatibility checks.
- Added `fletching.allow_modded_arrow_inputs`, enabled by default, so other mods' ArrowItem stacks can be converted through the Fletching Table.
- Smoothed the experimental trajectory speed-colour gradient and added `/archeryrevamped trajectory colour <true|false>`.

### Fixed

- Fletching recipes no longer auto-craft each server tick.
- Closing the screen returns only the two real input stacks; the output preview cannot be duplicated or returned as an extra item.

---

## 1.5.2 - Quiver and Tipped Arrow Polish

### Changed

- Tipped arrows now tint their entire in-flight arrow model to match their potion colour.
- Scaled and compacted the Quiver selector's selected-arrow name and keyboard hints.
- Added `run-client.bat` for launching the development client without an IDE task.

### Fixed

- Returned or redirected player-fired arrows can once again damage and apply potion effects to their shooter, using vanilla owner-collision timing.
- Shockwave and Impulse landing effects now include the firing player.

---

## 1.5.1 - Tipped Arrow Compatibility Fix

### Fixed

- Fixed normal vanilla tipped arrows losing their potion effects after Archery Revamped replaced the projectile entity.
- Tipped-arrow potion contents and duration scaling now carry over exactly to the custom projectile.
- Normal potion arrows now use vanilla's tipped-arrow in-flight rendering.
- Replaced the Quiver item texture with the supplied artwork.

---

## 1.5 - Quiver and Projectile Polish

### Added

- Added the craftable Quiver, which stores nine complete arrow stacks and supplies the selected stack before regular inventory arrows.
- Added Bundle-style Quiver storage and retrieval, with clockwise stack ordering.
- Added a keyboard radial Quiver selector: `V` opens/confirms, and `Q`/`E` cycle stored arrow types.
- Added an editable Fletching Table GUI texture template based on the vanilla anvil layout.

### Changed

- Rebuilt special in-flight arrow textures on the vanilla crossed-arrow layout for stable rendering.
- Reworked loaded bow and crossbow arrow visuals to use intact vanilla weapon textures with special-arrow colour cues.
- Tidal Arrows now use vanilla air movement with only slightly higher gravity; their torpedo spin and bubbles are water-only.
- Echo Arrow sculk spreading now converts only existing solid ground. It does not place sculk in air or replace grass and flowers.
- Improved Overdraw's random failure handling and visual configuration support.

### Fixed

- Fixed Quiver persistence so stored arrow stacks remain in their visible storage positions.
- Fixed special projectile rendering that could stretch or distort item textures in flight.

---

## 1.4.1 - Arrow Texture and Flight Polish

### Added

- Added dedicated in-flight crossed-arrow rendering for every special arrow type.
- Added the new Shockwave Arrow texture supplied for the 1.4.1 release.
- Added the previous Shockwave texture as the Impulse Arrow texture.
- Added the supplied Shattering Arrow texture.
- Added dedicated 32×32 projectile textures so special arrows render correctly in flight.

### Changed

- Tidal Arrows now fly with slightly higher gravity, reduced speed, no acceleration, and continuous torpedo spin.
- Shattering Arrows now launch 6–8 shards per impact with a higher and farther spread.
- Updated the release version to `1.4.1`.

## 1.4 - New Arrow Types and Impact Fixes

### Added

- Added Tidal Arrows, which accelerate underwater, use normal air drag underwater, emit bubbles, and spin like torpedoes.
- Added Shattering Arrows, crafted with Amethyst Shards, which create individual small amethyst shard projectiles on impact.
- Added Echo Arrows, crafted with Echo Shards, which apply five seconds of Darkness and spread sculk when their hit kills an entity.
- Added fletching recipes, item-group entries, dispenser support, and custom entity rendering for the new arrow types.

### Fixed

- Shockwave and Impulse effects now activate after a short random 2–5 tick delay, including after the arrow has sunk into a block or entity.
- Shattering Arrow parents remain collectible as normal arrows after block impact, while their individual shards despawn on block impact.

### Changed

- Updated the development release version to `1.4`.

## 1.3 - Dispenser Projectile Support

### Added

- Special arrows can now be fired from dispensers.
- Ender, Shockwave, Impulse, and Explosive arrows spawned by dispensers use the custom arrow entity and retain their full effects.
- Dispenser-launched arrows preserve vanilla dispenser launch velocity, pickup behavior, and item consumption.
- Explosive-arrow anti-grief protection is disabled by default so explosive arrows behave as expected out of the box.

### Changed

- Updated the release version to `1.3`.

## 1.2.1 - Explosive Arrow Anti-Grief

### Added

- Added configurable explosive-arrow anti-grief protection, enabled by default.
- Added `/archeryrevamped explosive_antigrief` to view the current protection state.
- Added `/archeryrevamped explosive_antigrief <true|false>` to change the setting in-game.
- Protected explosions still affect entities but do not destroy blocks.
- Added the setting to the Cloth Config screen and packaged JSON template.

### Changed

- Updated the release version to `1.2.1`.

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
