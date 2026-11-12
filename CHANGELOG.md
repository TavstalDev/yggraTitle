# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-11-12

Initial release.

### Added

- Version-independent `ITitle` API for sending title and subtitle messages to players.
- String overloads that accept legacy color codes (`&6`, `§6`), MiniMessage tags, or a mix of both in the same message.
- `%key%` placeholder replacement through an argument map. Keys may be written with or without percent signs, and unmatched placeholders are left untouched.
- Adventure `Component` overloads for sending pre-built components without parsing or placeholder replacement.
- Custom fade-in, stay, and fade-out timings through Adventure's `Title.Times`, with a default of 500 ms fade-in, 2 second stay, and 1 second fade-out.
- Automatic server version detection with implementations for:
  - Minecraft 1.11 and newer, using the native `Player#sendTitle(String, String, int, int, int)` API.
  - Minecraft 1.8 through 1.10.x, using reflective NMS `PacketPlayOutTitle` packets.
- Graceful fallback when an implementation cannot be loaded: a warning is logged and `send` calls become no-ops instead of failing.
- Single shaded library JAR containing the API and every supported implementation.
- Adventure is not bundled and can be loaded at runtime with Libby or a similar dependency manager.
- Java 8 runtime compatibility, with Adventure 4.26.1 as the runtime dependency.
- Example `plugin-test` plugin demonstrating runtime dependency loading with Libby.

[Unreleased]: https://github.com/TavstalDev/yggraTitle/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/TavstalDev/yggraTitle/releases/tag/v0.1.0
