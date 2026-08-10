# Archery Revamped Roadmap

## Minecraft 26.2 Port (In Progress)

- [x] Update Minecraft and Fabric API build coordinates.
- [x] Resolve 26.2 GUI, camera, and world-render submission API changes.
- [x] Compile the full common and client source sets.
- [ ] Launch and playtest the 26.2 development client.
- [ ] Package and publish the 26.2 branch.

## v1.6 - Fletching Table Rework (Complete)

- [x] Replace the temporary Anvil-based texture with the supplied Fletching Table UI.
- [x] Render the `Fletching Table` title above the custom screen art.
- [x] Convert the table to four-arrow + modifier inputs and a live, non-consuming four-arrow output preview.
- [x] Craft one batch into the cursor on normal output click.
- [x] Craft the maximum possible batches into inventory on Shift-click.
- [x] Change Quiver selection to hold `V`, cycle with `Q`/`E`, and release to confirm.
- [x] Scale Echo Arrow sculk conversion from the killed entity's vanilla experience value, with sparse veins and rare functional sculk structures.
- [x] Add optional Trinkets Updated Quiver slot support with configurable inventory override priority.
- [x] Add smooth experimental trajectory speed-colour visualisation and command toggle.
- [x] Add a configurable limitless-anvil option that preserves normal costs and enchantment compatibility.
- [x] Validate normal clicks, repeated crafting, Shift-click crafting, and close-screen inventory return in the release build.

## v1.6.2 - 26.1 Port and Polish (Complete)

- [x] Port the complete 1.6 feature set to Minecraft 26.1 using official Mojang mappings.
- [x] Restore special-arrow, tipped-arrow, loaded weapon, Quiver, and Fletching Table rendering paths.
- [x] Add normal-level custom enchanted books to the dedicated creative tab.
- [x] Verify production builds for both the 26.1 port and the 1.21.11 maintenance branch.

## v1.5.2 - Quiver and Tipped Arrow Polish (Built locally)

- [x] Tint the full in-flight tipped-arrow model using the potion colour.
- [x] Prevent Quiver selector text from overflowing the centre panel.
- [x] Restore player self-hits and self-applied arrow effects after vanilla release safety has elapsed.

## v1.5.1 - Tipped Arrow Compatibility Fix (Completed)

- [x] Preserve vanilla tipped-arrow potion contents and duration scaling on custom projectiles.
- [x] Restore vanilla tipped-arrow rendering for normal potion arrows.
- [x] Update the Quiver item artwork.

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
