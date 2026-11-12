# yggraTitle

![Release (latest by date)](https://img.shields.io/github/v/release/TavstalDev/yggraTitle?style=plastic-square)
![Workflow Status](https://img.shields.io/github/actions/workflow/status/TavstalDev/yggraTitle/release.yml?branch=stable&label=build&style=plastic-square)
![License](https://img.shields.io/github/license/TavstalDev/yggraTitle?style=plastic-square)
![Downloads](https://img.shields.io/github/downloads/TavstalDev/yggraTitle/total?style=plastic-square)
![Stars](https://img.shields.io/github/stars/TavstalDev/yggraTitle?style=plastic-square)
![Issues](https://img.shields.io/github/issues/TavstalDev/yggraTitle?style=plastic-square)
![Forks](https://img.shields.io/github/forks/TavstalDev/yggraTitle?style=plastic-square)

A small, version-independent library for sending **title** and **subtitle** messages from
Spigot/Paper plugins. It picks the right implementation for the running server at runtime, so your
plugin does not need its own NMS or version-specific code.

## Features

- One API for Minecraft **1.8 and newer**.
- Send either pre-built Adventure `Component`s or formatted `String`s.
- String overloads accept legacy color codes (`&6`, `§6`) and MiniMessage tags, and both styles can
  be mixed in the same message.
- Built-in placeholder replacement using `%key%` syntax.
- Custom fade-in, stay, and fade-out timings through Adventure's `Title.Times`.
- Automatic server version detection with a graceful fallback when no implementation is available.
- A single shaded JAR that contains the API and every supported implementation.
- No need to shade Adventure: load it at runtime with Libby or a similar dependency manager.
- Ready-to-use example plugin for manual testing.

## Supported versions

| Minecraft version | Implementation | How it works |
| --- | --- | --- |
| 1.11+ | `impl_v1_11` | Native `Player#sendTitle(String, String, int, int, int)` API |
| 1.8 – 1.10.x | `impl_v1_8_8` | Reflective NMS `PacketPlayOutTitle` packets |

The implementation is selected once, when `YggraTitle` is constructed. If the server version is
unsupported, or the implementation cannot be loaded, a warning is logged and calls are ignored
instead of crashing your plugin.

## Project structure

| Module | Purpose |
| --- | --- |
| `api` | The public `ITitle` interface plus the `AdventureUtils` and `ArgumentUtils` helpers |
| `bootstrap` | Version detection and the `YggraTitle` facade |
| `implementation/impl_v1_8_8` | 1.8 – 1.10.x implementation |
| `implementation/impl_v1_11` | 1.11+ implementation |
| `plugin-test` | Example plugin showing Libby runtime loading |

## Requirements

- A Spigot or Paper server running Minecraft **1.8 or newer**.
- **Java 8+** at runtime. All published modules target Java 8.
- **JDK 25** with the included Gradle wrapper to build the project.
- Adventure **4.26.1** loaded at runtime (see [Usage](#usage)).

## Building from source

```bash
./gradlew build
```

The library JAR is written to:

```text
bootstrap/build/libs/yggraTitle-0.1.0.jar
```

This JAR already contains the API and both version-specific implementations. Adventure is not
bundled; it is loaded at runtime (see [Usage](#usage)).

To build the example plugin:

```bash
./gradlew :plugin-test:shadowJar
```

The result is `plugin-test/build/libs/yggraTitle-test-1.0.0.jar`.

## Usage

### 1. Add the library to your plugin

Shade the produced library JAR into your plugin. If you build this repository as part of your
project, you can depend on the shadow configuration directly:

```kotlin
dependencies {
    implementation(project(":bootstrap", configuration = "shadow")) {
        isTransitive = false
    }
}
```

If you use the pre-built JAR, add it as a local dependency:

```kotlin
dependencies {
    implementation(files("libs/yggraTitle-0.1.0.jar"))
}
```

### 2. Load Adventure at runtime

Do **not** shade Adventure into your plugin. It is a shared library and bundling it can cause class
loader conflicts with other plugins. Instead, declare it as `compileOnly` and load it at runtime
with a dependency manager such as [Libby](https://github.com/AlessioDP/libby) (SlimJar, Quark and
similar tools work the same way).

Add Libby and the Adventure artifacts to your build:

```kotlin
repositories {
    maven("https://repo.alessiodp.com/releases")
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.26.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.26.1")

    implementation("net.byteflux:libby-bukkit:1.3.2")
}

tasks.shadowJar {
    // Always relocate Libby to avoid conflicts with other plugins.
    relocate("net.byteflux", "your.plugin.libs.libby")
}
```

Then load the Adventure modules during `onEnable`, before creating `YggraTitle`:

```java
import io.github.tavstaldev.yggraTitle.api.ITitle;
import io.github.tavstaldev.yggraTitle.bootstrap.YggraTitle;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin {

    private ITitle titles;

    private static final String[][] ADVENTURE_LIBRARIES = {
            {"examination-api", "1.3.0"},
            {"examination-string", "1.3.0"},
            {"adventure-key", "4.26.1"},
            {"adventure-api", "4.26.1"},
            {"adventure-text-serializer-legacy", "4.26.1"},
            {"adventure-text-minimessage", "4.26.1"},
            {"adventure-text-serializer-plain", "4.26.1"},
            {"adventure-text-serializer-gson", "4.26.1"}
    };

    @Override
    public void onEnable() {
        BukkitLibraryManager libraries = new BukkitLibraryManager(this);
        libraries.addMavenCentral();
        libraries.addSonatype();
        libraries.addRepository("https://jitpack.io");

        for (String[] artifact : ADVENTURE_LIBRARIES) {
            libraries.loadLibrary(Library.builder()
                    // "{}" prevents the string from being rewritten during relocation.
                    .groupId("net{}kyori")
                    .artifactId(artifact[0])
                    .version(artifact[1])
                    .build());
        }

        titles = new YggraTitle(this);
    }
}
```

Libby loads each artifact individually and does not resolve transitive dependencies, so every
Adventure module used by yggraTitle is listed explicitly.

### 3. Create the facade

Once the Adventure libraries are loaded, create one `YggraTitle` instance during `onEnable` and
reuse it for the lifetime of the plugin:

```java
titles = new YggraTitle(this);
```

`YggraTitle` implements `ITitle`, so it can be stored as either type. Using the `ITitle` interface
keeps your code independent of the facade implementation.

### 4. Send titles

```java
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

public void greet(Player player) {
    // Plain strings. Legacy codes and MiniMessage tags are both supported.
    titles.send(player, "&6Welcome!", "&7to the server");

    // String message with placeholders.
    Map<String, Object> placeholders = new HashMap<>();
    placeholders.put("player", player.getName());
    placeholders.put("coins", 120);

    titles.send(player, "&e%player%", "&6%coins% coins", placeholders);

    // Pre-built Adventure components.
    titles.send(
            player,
            Component.text("Welcome!", NamedTextColor.GOLD),
            Component.text("Have fun", NamedTextColor.GRAY));

    // Custom timing: fade in, stay, fade out.
    Title.Times times = Title.Times.times(
            Duration.ofMillis(250),
            Duration.ofSeconds(3),
            Duration.ofSeconds(1));

    titles.send(player, "&aHello", "&7custom timing", times);
}
```

If no timing is supplied, the default is a 500 ms fade-in, a 2 second stay, and a 1 second
fade-out. Placeholder keys may be written with or without the surrounding percent signs, so both
`player` and `%player%` work as map keys.

## API overview

```java
public interface ITitle {

    void send(Player player, String title, String subTitle);

    void send(Player player, String title, String subTitle, Map<String, Object> arguments);

    void send(Player player, String title, String subTitle, Title.Times times);

    void send(Player player, String title, String subTitle,
              Map<String, Object> arguments, Title.Times times);

    void send(Player player, Component title, Component subTitle);

    void send(Player player, Component title, Component subTitle, Title.Times times);
}
```

| Method | Description |
| --- | --- |
| `send(Player, String, String)` | Displays strings with the default timing. |
| `send(Player, String, String, Map)` | Replaces `%key%` placeholders, then displays with the default timing. |
| `send(Player, String, String, Times)` | Displays strings with custom timing. |
| `send(Player, String, String, Map, Times)` | Replaces placeholders, then displays with custom timing. |
| `send(Player, Component, Component)` | Displays components with the default timing. |
| `send(Player, Component, Component, Times)` | Displays components with custom timing. |

String overloads parse legacy color codes and MiniMessage before sending. Component overloads send
the components as-is, without parsing or placeholder replacement.

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more details.

## Contact

For issues or feature requests, use the
[GitHub issue tracker](https://github.com/TavstalDev/yggraTitle/issues).
