# Archery Revamped Roadmap

## v1.4.1 - Published

- [x] Apply the supplied Shockwave, Impulse, and Shattering arrow textures.
- [x] Render special arrows with the vanilla crossed in-flight arrow model.
- [x] Stabilize Tidal Arrow flight with slightly higher gravity, reduced speed, no acceleration, and torpedo spin.
- [x] Increase Shattering Arrow spread to 6–8 higher, farther shards.
- [x] Update release documentation and build a v1.4.1 JAR.

## v1.4 - Published

- [x] Add a short delayed activation window for Shockwave and Impulse impacts.
- [x] Add Tidal Arrows with underwater acceleration, bubbles, and torpedo spin.
- [x] Add Shattering Arrows with individual amethyst shard projectiles.
- [x] Add Echo Arrows with Darkness and lethal-hit sculk spread.
- [x] Add recipes, item-group entries, dispenser support, and rendering for the new arrows.
- [ ] Playtest all new arrow types in bows, crossbows, dispensers, and the fletching table.

## v1.3 - Complete

- [x] Make special arrows fire correctly from dispensers.
- [x] Preserve vanilla dispenser launch, pickup, and consumption behavior.
- [x] Update release documentation and build a v1.3 JAR.

## v1.2.1 - Complete

- [x] Add configurable explosive-arrow anti-grief protection, enabled by default.
- [x] Add `/archeryrevamped explosive_antigrief` status and toggle commands.
- [x] Expose explosive anti-grief protection in the Cloth Config screen.
- [x] Update release documentation and build a v1.2.1 JAR.

## v1.2 — Complete

- [x] Remove Sticky Arrows.
- [x] Remove Sharpshooter.
- [x] Rename outward Impulse behavior to Shockwave.
- [x] Add inward-pulling Impulse Arrows.
- [x] Improve area-force falloff and player knockback.
- [x] Add configurable, disabled-by-default Headshot I–III.
- [x] Make Headshot work generically with all living entities.
- [x] Scale Fracture safely up to enchantment level 255.
- [x] Fix Burst/Fracture child-arrow spawning and inheritance.
- [x] Remove Fletching Table crafting delay.
- [x] Keep fletching output stacking and close-screen inventory return safe.
- [x] Remove the obsolete `regular infinite_levels` command.
- [x] Update release documentation and build a v1.2 JAR.

## v1.5 - Planned

- [ ] Add focused automated tests for arrow persistence, Headshot collision detection, force falloff, and fletching batches.
- [ ] Improve Burst release-angle consistency and add stronger in-game diagnostics.
- [ ] Review trajectory rendering performance in long-distance and high-arrow-count scenes.
- [ ] Expand recipe-viewer surfaces for JEI, REI, and EMI where their APIs are present.
- [ ] Add optional client-side configuration feedback for experimental features.
- [ ] Profile dedicated-server behavior with large numbers of custom arrows.

## Long-term

- [ ] Add more modular arrow behaviors without changing the core entity pipeline.
- [ ] Add configurable enchantment compatibility and balancing presets.
- [ ] Provide a complete release checklist and CurseForge/Modrinth publishing workflow.
