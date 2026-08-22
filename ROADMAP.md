# Archery Revamped Roadmap

## v1.6 - Fletching Table Update (Complete)

- [x] Replace the temporary anvil-based Fletching Table artwork with the final custom UI.
- [x] Map the two input slots, output slot, recipe label, and feedback indicator to the supplied UI texture.
- [x] Use four-arrow special batches and eight-arrow tipped batches.
- [x] Use a virtual click-to-craft output, shift-craft support, and close-screen input return.
- [x] Reserve tipped-arrow conversion for the Fletching Table.

## v1.6.2 - Release Polish (Complete)

- [x] Add normal-level custom enchantment books to the dedicated creative tab.
- [x] Cross-verify Fletching behavior and bundled force-arrow defaults against the 26.1 port.

## v1.6.3 - Creative Tab Hotfix (Complete)

- [x] Prevent component-identical enchanted books from being added twice to the Archery Revamped creative tab.
- [x] Fix the Creative Inventory crash reported when opening the mod's tab.
- [x] Publish corrected builds for Minecraft 1.20.1, 1.21.1, 1.21.11, 26.1, and 26.2.

## v1.5 - Published

- [x] Add a craftable Quiver that holds nine complete arrow stacks.
- [x] Add Bundle-style clockwise Quiver storage and retrieval.
- [x] Add the `V` / `Q` / `E` keyboard radial selector and selected-arrow ammo priority.
- [x] Make Burst and Fracture consume matching Quiver ammunition first.
- [x] Add an editable Fletching Table GUI texture template for the upcoming redesign.
- [x] Stabilize Tidal Arrow air flight and rebuild special in-flight arrow rendering on vanilla's crossed-arrow layout.
- [x] Restrict Echo Arrow sculk spreading to existing solid ground.

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

## Long-term

- [ ] Add more modular arrow behaviors without changing the core entity pipeline.
- [ ] Add configurable enchantment compatibility and balancing presets.
- [ ] Provide a complete release checklist and CurseForge/Modrinth publishing workflow.
