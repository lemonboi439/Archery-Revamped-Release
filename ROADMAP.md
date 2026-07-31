# Archery Revamped Roadmap

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

## v1.3 — Planned

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
