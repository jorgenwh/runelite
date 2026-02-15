# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

RuneLite uses Gradle with composite builds. Common commands:

```bash
./gradlew buildAll      # Build all modules
./gradlew testAll       # Run all tests
./gradlew cleanAll      # Clean all modules
./gradlew assembleAll   # Assemble all modules

# Per-module commands (run from root)
./gradlew :runelite-client:build
./gradlew :runelite-client:test
./gradlew :runelite-api:build
./gradlew :cache:build

# Run a single test
./gradlew :runelite-client:test --tests "net.runelite.client.plugins.agility.AgilityPluginTest"

# Run the client
./gradlew :runelite-client:run
# Or run the net.runelite.client.RuneLite class directly from IDE
```

## Code Style

- **Indentation**: Tabs only, not spaces
- **Braces**: Opening brace on new line (Allman style)
- **No trailing whitespace**
- **No `else if` split across lines** - must be `} else if {` on same line
- Checkstyle runs automatically; config in `config/checkstyle/`
- PMD enabled for runelite-client; see `runelite-client/pmd-ruleset.xml`

## Architecture

RuneLite is an OldSchool RuneScape client. The codebase is structured as composite Gradle builds:

- **cache/** - Libraries for reading/writing OSRS cache files
- **runelite-api/** - Interface definitions for accessing game client state. Contains events in `api/events/` and game constants (ItemID, NpcID, ObjectID, etc.)
- **runelite-client/** - Main application with plugin system, UI, and overlays
- **runelite-jshell/** - JShell integration for development
- **runelite-gradle-plugin/** - Custom Gradle plugins for assembly and code generation

### Plugin System

Plugins extend `net.runelite.client.plugins.Plugin` and are annotated with `@PluginDescriptor`. Key plugin patterns:

- Use `@Inject` for dependency injection (Guice)
- Override `startUp()` and `shutDown()` for lifecycle
- Use `@Subscribe` on methods to receive events (from `api/events/`)
- Create `*Config` interfaces with `@ConfigItem` for settings
- Add `Overlay` subclasses for rendering

Example plugin structure:
```
plugins/example/
  ExamplePlugin.java       # Main plugin class
  ExampleConfig.java       # Configuration interface
  ExampleOverlay.java      # Overlay rendering (optional)
```

### Event System

The EventBus dispatches events from `net.runelite.api.events`. Common events:
- `GameTick` - Every game tick (~600ms)
- `ClientTick` - Every client frame
- `GameStateChanged` - Login/logout state changes
- `ChatMessage` - Chat messages received
- `GameObjectSpawned/Despawned` - World objects

### Overlay System

Overlays in `client/ui/overlay/` render on top of the game. Extend `Overlay` or `OverlayPanel` and register via `OverlayManager`.

### Agent Controller Plugin

The `agentcontroller` plugin (`runelite-client/src/main/java/.../plugins/agentcontroller/`) provides a WebSocket bridge for external agent control:

- **AgentControllerPlugin.java** - Main plugin, sends observations and receives actions each GameTick
- **AgentWebSocket.java** - OkHttp WebSocket client connecting to `ws://localhost:8765`
- **ActionHandler.java** - Deserializes JSON actions and executes them (currently supports `toggle_prayer`)
- **PrayerMap.java** - Maps prayer names to Prayer API objects and widget IDs
- **AgentControllerConfig.java** - Config with `active` toggle

**Protocol** (JSON over WebSocket):
- Plugin sends observations each tick: `{tick, hp, hp_max, prayer, prayer_max, x, y, plane}`
- Agent responds with actions: `{"type": "toggle_prayer", "prayer": "PROTECT_FROM_MELEE"}` or `{"type": "none"}`

## Python Agent (`python/`)

The Python package provides the agent server that connects to the RuneLite plugin.

```bash
cd python
uv run agent-serve    # Start the agent WebSocket server
```

Requires Python >= 3.12. Uses `uv` for dependency management.

## Java Version

Target is Java 11 (`options.release = 11` in gradle config).
