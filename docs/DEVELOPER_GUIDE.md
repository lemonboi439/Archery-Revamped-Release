# Archery Revamped Developer Guide

## Project layout

```text
src/main/java/me/lemonboi439/archeryRevamped/
├── arrow/        Arrow types, behavior registry, and impact behaviors
├── ammo/         Ammunition accounting for child arrows
├── burst/        Delayed Burst scheduling
├── command/      /archeryrevamped commands
├── config/       JSON configuration and validation
├── effect/       Shared particle and sound helpers
├── enchantment/  Registry keys and level constants
├── entity/       Custom arrow entity and entity registration
├── fracture/     Delayed Fracture scheduling
├── item/         Special arrow items and item group
├── loot/         Late-game vanilla loot-table modification
├── physics/      Per-tick projectile velocity calculations
├── screen/       Fletching table handler, recipes, and viewer surface
└── trade/        Villager trade registration

src/client/java/me/lemonboi439/archeryRevamped/
├── client/       Client initializer, GUI, and client-only effects
└── mixin/        Client camera hooks
```

The custom arrow is created by `RangedWeaponItemMixin`. Its `tick()` calls vanilla movement first, then applies the centralized physics engine and dispatches the registered behavior. Impact handlers preserve that ordering: ricochet is checked first, vanilla impact is retained when no bounce occurs, and the arrow behavior runs afterward.

## Adding a new arrow type

1. Add a constant to `arrow/ArrowType.java`.
2. Add an `Item` subclass if the arrow needs a distinct inventory item. Register it in `item/ModItems.java` with a stable `Identifier` and `RegistryKey`.
3. Implement `ArrowBehavior` in `arrow/`. Keep world mutation on the server side and use `EffectManager` for shared effects.
4. Register the behavior in `ArcheryRevamped.onInitialize()`:

   ```java
   ArrowBehaviorRegistry.register(ArrowType.NEW_TYPE, new NewArrowBehavior());
   ```

5. Map the inventory item to the entity type in `mixin/RangedWeaponItemMixin.java`.
6. Add the item asset definition, model/texture if needed, and language entry.
7. Add a fletching recipe in `screen/FletchingRecipeRegistry.java` if the type is craftable.
8. Add config values to `ConfigManager.PhysicsConfig`, including parsing, serialization, validation, and the packaged template.
9. Add trades or loot only when the resulting economy is intentional.
10. If the entity needs custom client state, add a tracked value to `ArcheryArrowEntity` and keep client-only rendering in `src/client`.

Child arrows should be created through `createBurstChild()` or `createFractureChild()` so owner, pickup, critical, fire, pierce, damage, arrow type, and custom state are copied consistently. Any new persistent field must be saved in both the legacy `NbtCompound` compatibility methods and the current `WriteView`/`ReadView` methods.

## Physics rules

`ArrowPhysicsEngine` is the single place for configurable velocity changes. It may call `setVelocity()`, but it must not move the entity directly. The trajectory renderer should reuse `applyPreviewPhysics()` or actual tracked positions; it must never introduce a second movement implementation.

Default physics are vanilla-compatible. The engine applies configured values as deltas relative to vanilla projectile movement, then applies optional randomness and terminal-velocity clamping.

## Commands and permissions

Register admin commands through `ArcheryCommand`. The root command is `/archeryrevamped` and requires permission level 2. Keep command-side writes routed through `ConfigManager` so changes are validated and persisted.

## Resources and registries

- Dynamic enchantment definitions live under `data/archery-revamped/enchantment`.
- Custom enchantments are added to `data/minecraft/tags/enchantment/non_treasure.json`, which makes them eligible for enchanting tables and random loot through vanilla tag inheritance.
- `LateGameLootManager` uses `LootTableEvents.MODIFY` and only modifies built-in tables. Do not replace vanilla loot-table JSON files unless there is a compelling compatibility reason.
- `fabric.mod.json` must keep the client entrypoint client-only through the split source sets and must not load client classes from the main entrypoint.

## Optional integrations

Do not add hard dependencies on JEI, REI, EMI, Cloth Config, or Mod Menu. The Cloth Config and Mod Menu artifacts are compile-only integrations. `ArcheryRevampedModMenu` checks whether Cloth Config is loaded before returning the screen factory, so dedicated-server startup and clients without either optional mod remain safe. Keep configuration screen values routed through `ConfigManager`; the screen and `/archeryrevamped physics` commands must edit the same values.

The optional configuration screen is in `src/client/java/.../client/config/ArcheryRevampedConfigScreen.java`. Add new settings to the appropriate Cloth Config category and to `ConfigManager`'s parser, serializer, validation, and setters. The `regular/infinite_levels` setting is deliberately separate from physics. When enabled, custom enchantment levels are clamped to the safe finite maximum of 255; there is no unbounded global enchantment mixin.

## Validation checklist

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
./gradlew.bat clean build --no-daemon
```

Before a release, also verify in a clean instance:

- vanilla bow and crossbow shots still move, damage, ignite, pierce, and become pickup-able correctly;
- each special arrow works from a bow and crossbow;
- arrows survive save/reload with custom state;
- ricochet paths and trajectory rendering do not crash the client;
- fletching output and returned inventory items are correct;
- custom books appear in enchanting and selected late-game loot tables;
- the Mod Menu screen displays current physics values and changes made by the physics commands;
- the game starts with JEI, REI, EMI, and Cloth Config absent.
