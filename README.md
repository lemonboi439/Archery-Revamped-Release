# Archery Revamped

### The Ultimate Vanilla-Friendly Archery Overhaul!

Archery Revamped expands Minecraft's ranged combat with new enchantments, specialised arrows, configurable projectile physics, an experimental fletching system, and much more.

## Features

### Enchantments

- **Ricochet I–V** - Bounce arrows off blocks.
- **Overdraw I–II** - Overcharge bows for more powerful shots.
- **Longshot I** - Deal increased damage at longer distances.
- **Fracture I–II** - Split arrows into multiple projectiles mid-flight.
- **Burst I–III** - Fire additional delayed arrows.
- **Sharpshooter I–III** - Reduce movement slowdown while drawing ranged weapons.

### Special Arrows

- **Ender Arrow** - Teleports the shooter to the impact location.
- **Impulse Arrow** - Launchs nearby entities away.
- **Explosive Arrow** - Explodes on impact.
- **Sticky Arrow** - Slows living targets.

### Arrow Physics

Archery Revamped includes configurable:

- Gravity
- Drag
- Projectile speed
- Randomness / spread
- Terminal velocity
- Arrow lifetime

### Experimental Features

- Custom Fletching Table interface
- Special arrow crafting
- Tipped Arrow crafting
- Pre-shot trajectory prediction
- Live arrow trajectory rendering
- Ricochet path prediction
- Velocity colour visualisation

These systems are functional, but are still experimental and may receive major changes.

## Configuration

The main configuration file is:

`config/archery_revamped-config.json`

Archery Revamped also supports an in-game configuration screen when **Cloth Config API** and **Mod Menu** are installed.

Configuration can be reloaded without restarting Minecraft using:

`/archeryrevamped reload`

## Optional Compatibility

Archery Revamped includes optional support for:

- Cloth Config API
- Mod Menu
- Just Enough Items (JEI)
- Roughly Enough Items (REI)
- EMI

None of these are required to use the mod.

## Downloads

- [Modrinth](https://modrinth.com/project/archery-revamped)

## Development

- [Changelog](Changelog.md)
- [Roadmap](ROADMAP.md)
- [Porting Guidelines](PORTING.md)
- [Report a Bug](https://github.com/lemonboi439/Archery-Revamped-Release-/issues)
- [Suggest a Feature](https://github.com/lemonboi439/Archery-Revamped-Release-/issues)

## Community Ports

Archery Revamped is licensed under the MIT License.

Community ports to Minecraft versions not officially supported by Archery Revamped are welcome!

Please avoid publishing separate ports for versions that are already officially maintained by Archery Revamped.

Currently planned / officially maintained versions include:

- Minecraft 1.21.11
- Minecraft 1.21.1
- Minecraft 26.1+

If you create a port, crediting Archery Revamped and linking back to the original project is greatly appreciated.

## Building From Source

### Windows

```bash
gradlew.bat build
```

### Linux / macOS

```bash
./gradlew build
```

Compiled JARs can be found in:

`build/libs/`

## Issues & Suggestions

Found a bug?

Please open a [GitHub Issue](https://github.com/lemonboi439/Archery-Revamped-Release-/issues) and include:

- Minecraft version
- Archery Revamped version
- Fabric Loader version
- Fabric API version
- Installed mods
- Steps to reproduce
- `latest.log` or crash report where relevant

Feature suggestions are welcome too!

## Links

- [Modrinth](https://modrinth.com/project/archery-revamped)
- [YouTube](https://www.youtube.com/@LemonGDYT)
- [GitHub](https://github.com/lemonboi439/Archery-Revamped-Release-)
- [GitHub Issues](https://github.com/lemonboi439/Archery-Revamped-Release-/issues)
- [Development Roadmap](ROADMAP.md)

## License

Archery Revamped is licensed under the **MIT License**.

See [LICENSE](LICENSE) for the full licence text.
