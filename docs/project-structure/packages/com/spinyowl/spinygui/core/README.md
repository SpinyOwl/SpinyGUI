# com.spinyowl.spinygui.core

Top-level core configuration and shared entry points for the GUI engine.

- Modules: core, core.backend, core.backend.lwjgl.nanovg
- Source sets: main, test
- Direct classes: 1
- Descendant packages: 54

## Classes

### Configuration

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/Configuration.java`
- Declaration: `final class Configuration<T>`
- Responsibility: Configuration class that used to define configuration for GUI system.

## Child Packages

- [com.spinyowl.spinygui.core.animation](animation/README.md) - Frame-time animation contracts and a simple animator loop.
- [com.spinyowl.spinygui.core.backend](backend/README.md) - Backend namespace folder for renderer APIs and implementations.
- [com.spinyowl.spinygui.core.clipboard](clipboard/README.md) - Clipboard abstraction used by platform integrations.
- [com.spinyowl.spinygui.core.cursor](cursor/README.md) - Cursor model and cursor service abstraction.
- [com.spinyowl.spinygui.core.event](event/README.md) - Application-level events emitted to nodes and event targets.
- [com.spinyowl.spinygui.core.font](font/README.md) - CSS-like font value objects: family, size, stretch, style, and weight.
- [com.spinyowl.spinygui.core.image](image/README.md) - Image abstraction used by style and rendering layers.
- [com.spinyowl.spinygui.core.input](input/README.md) - Input domain model for keyboard, mouse, shortcuts, and user-facing key mappings.
- [com.spinyowl.spinygui.core.layout](layout/README.md) - Layout contracts, layout context, and text/element layout interfaces.
- [com.spinyowl.spinygui.core.node](node/README.md) - DOM-like node hierarchy: frames, elements, empty elements, text nodes, and builders.
- [com.spinyowl.spinygui.core.parser](parser/README.md) - Parser interfaces for HTML-like node trees and stylesheets.
- [com.spinyowl.spinygui.core.style](style/README.md) - Resolved style state applied to nodes after rule matching and property conversion.
- [com.spinyowl.spinygui.core.system](system/README.md) - Package for system related classes.
- [com.spinyowl.spinygui.core.time](time/README.md) - Time service abstraction for animation and frame timing.
- [com.spinyowl.spinygui.core.util](util/README.md) - Small utilities for class-key maps, IO, node visibility, references, and text handling.

## Aggregated Contents

This package aggregates 54 descendant package(s) with 261 descendant class(es).
