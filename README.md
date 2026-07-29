# Archery Revamped

Archery Revamped is a vanilla+ Fabric mod for Minecraft 1.21.11. It adds custom arrow entities, special arrow types, configurable projectile physics, archery enchantments, a fletching table, villager trades, trajectory debugging, and optional recipe-viewer compatibility.

## Compatibility

- Minecraft: `1.21.11`
- Yarn mappings: `1.21.11+build.6`
- Fabric Loader: `0.19.3` or newer
- Fabric API: `0.141.5+1.21.11`
- Java: `21`

The mod is server-safe and does not require JEI, REI, EMI, Cloth Config, or Mod Menu. Those mods are optional suggestions in `fabric.mod.json`.

## Installation

1. Install Fabric Loader for Minecraft 1.21.11 and Java 21.
2. Put `archery-revamped-1.1.jar` in the `mods` directory.
3. Install Fabric API for 1.21.11.
4. Start the game. The mod creates `config/archery_revamped-config.json` on the first server start.

The release JAR is produced at `build/libs/archery-revamped-1.1.jar`.

## Features

### Special arrows

All bow and crossbow shots use the custom `archery-revamped:archery_arrow` entity while preserving vanilla projectile state such as velocity, critical hits, pickup behavior, fire, pierce level, and weapon damage.

| Arrow | Effect |
| --- | --- |
| Ender Arrow | Teleports the shooter to a safe impact position. |
| Impulse Arrow | Applies radial knockback with distance falloff and splash effects. |
| Explosive Arrow | Creates a configurable impact explosion. |
| Sticky Arrow | Applies configurable Slowness to a living target. |

The special arrows are available from the Archery Revamped item group, the Combat item group, the fletching table, and villager trades.

### Enchantments

- **Ricochet I–V**: reflects an arrow from solid blocks, with one bounce per level.
- **Overdraw I–II**: increases bow damage while the bow is held beyond normal charge time.
- **Longshot I**: scales damage at 16, 32, 48, and 64 blocks of travelled distance.
- **Fracture I–II**: splits an arrow into child arrows after a speed-scaled delay. Disabled by default.
- **Burst I–III**: fires additional arrows with staggered timing and charges ammunition/durability appropriately.
- **Sharpshooter I–III**: reduces draw movement slowdown while worn on leggings. Disabled by default.

All custom enchantments are included in the non-treasure enchantment tag. Because vanilla includes that tag in `in_enchanting_table`, they can be offered by enchanting tables when compatible with the item. They are also included in random-loot selection.

Rare custom enchanted books are added to built-in late-game loot tables: End City, Bastion, Ancient City, Trial Chambers, Nether Fortress, Stronghold Library, and Woodland Mansion chests. Vanilla loot tables are modified through Fabric's loot event and are not replaced.

### Fletching table

The vanilla Fletching Table opens the Archery Revamped screen when used. Crafting takes 20 ticks by default and produces four results:

| Input | Ingredient | Result |
| --- | --- | --- |
| 1 arrow | Ender Pearl | 4 Ender Arrows |
| 1 arrow | Feather | 4 Impulse Arrows |
| 1 arrow | Gunpowder | 4 Explosive Arrows |
| 1 arrow | Honeycomb | 4 Sticky Arrows |
| 4 arrows | Potion | 4 matching tipped arrows |

The output is a real inventory item. Closing the screen returns input and output items to the player.

### Debug trajectory

The trajectory command renders a client-side connected trail for each custom arrow while enabled, while retaining the live pre-shot aiming preview. The trail begins at the server-captured spawn position, records the complete physics path, remains connected through ricochets, and predicts forward until an entity/block impact or the configured arrow lifetime. The prediction uses the same position, drag, gravity, terminal-velocity, water, and configured physics order as the real arrow, raycasts every segment, stops at entities/blocks, and reflects ricochets. The oldest trail section fades first, finished trails fade out and are removed after ten seconds, and it uses depth-aware tube rendering rather than particles. An experimental speed-colour mode is disabled by default and transitions from red for fastest movement to blue for slowest movement.

## Commands

Commands require permission level 2.

```text
/archeryrevamped reload
/archeryrevamped config
/archeryrevamped help
/archeryrevamped physics
/archeryrevamped physics get
/archeryrevamped physics reset
/archeryrevamped physics gravity <double>
/archeryrevamped physics drag <double>
/archeryrevamped physics speed <double>
/archeryrevamped physics randomness <double>
/archeryrevamped physics terminal_velocity <double>
/archeryrevamped physics lifetime <int>
/archeryrevamped regular infinite_levels
/archeryrevamped regular infinite_levels <true|false>
/archeryrevamped trajectory
/archeryrevamped trajectory <true|false>
```

There is intentionally no `/ar` alias. `/archeryrevamped reload` reloads the JSON configuration without restarting the server. `/archeryrevamped regular infinite_levels` displays the current setting, and the boolean form changes it. When enabled, Archery Revamped enchantments no longer use their normal maximum level; the setting is disabled by default.

`/archeryrevamped config` explains how to open the optional GUI. Install Cloth Config and Mod Menu, then open Archery Revamped from Mod Menu. The GUI shows the live configuration, including every physics value exposed by the physics commands, and saves changes when the screen is closed. Without those optional mods, use the JSON file or commands.

## Configuration

The packaged valid template is [`src/main/resources/config/archery_revamped-config.json`](src/main/resources/config/archery_revamped-config.json). The live file is `config/archery_revamped-config.json` in the instance directory. JSON does not support comments, so the template is documented here rather than containing comments.

| Section | Key | Default |
| --- | --- | ---: |
| physics | gravity | `0.05` |
| physics | drag | `0.99` |
| physics | speed_multiplier | `1.0` |
| physics | randomness | `0.0` |
| physics | terminal_velocity | `999.0` |
| physics | max_lifetime_ticks | `1200` |
| physics | ricochet_velocity_loss_percent | `10.0` |
| ricochet | velocity_loss_percent | `10.0` |
| overdraw | damage_increase_per_tick_percent | `1.0` |
| overdraw | max_damage_bonus_percent | `100.0` |
| overdraw | self_damage_hearts | `2.0` |
| overdraw | durability_loss_percent | `25.0` |
| overdraw | auto_fire_delay_ticks | `100` |
| longshot | thresholds | `16`, `32`, `48`, `64` blocks |
| longshot | multipliers | `1.5`, `2.0`, `2.5`, `3.0` |
| burst | stagger_delay_ticks | `3` |
| burst | arrows_per_level | `1` |
| fracture | enabled | `false` |
| fracture | split_delay_ticks | `2` before speed scaling |
| fracture | split_angle_degrees | `15.0` |
| fracture | reference_release_speed | `3.0` |
| fracture | min_split_delay_ticks | `1` |
| fracture | max_split_delay_ticks | `40` |
| arrow_types.ender | enabled | `true` |
| arrow_types.impulse | enabled | `true` |
| arrow_types.impulse | blast_radius / knockback_strength | `4.0` / `2.0` |
| arrow_types.explosive | enabled | `true` |
| arrow_types.explosive | explosion_size | `2.5` |
| arrow_types.sticky | enabled | `true` |
| arrow_types.sticky | movement_reduction / duration / slowness | `50.0%` / `100` / `1` |
| sharpshooter | enabled | `false` |
| sharpshooter | level reductions | `70.0%`, `40.0%`, `10.0%` |
| fletching | crafting_time_ticks / recipe_output_count | `20` / `4` |
| trajectory | colour_visualisation | `false` |
| regular | infinite_levels | `false` |
| general | mod_enabled | `true` |

Values are validated when loaded. Invalid or missing values fall back to defaults; physics commands also save their changes to disk. Drag is an air-resistance setting: higher values apply more resistance, with `0.99` retaining vanilla behavior.

## Recipe viewers

JEI, REI, and EMI remain optional. The registered arrow items can be indexed by those viewers, and the shared fletching definitions are exposed through `FletchingRecipeRegistry` for a client integration without loading optional APIs on a dedicated server.

## Development

See [`docs/DEVELOPER_GUIDE.md`](docs/DEVELOPER_GUIDE.md) for the package layout, adding arrow types, persistence, client rendering, and release commands.

## Validation and release

Run the following from the project root:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
./gradlew.bat clean build --no-daemon
```

The build produces the remapped runtime JAR and a sources JAR in `build/libs`. Before publishing, test the runtime JAR in a clean Fabric 1.21.11 instance with a new world and an existing test world containing arrows.
