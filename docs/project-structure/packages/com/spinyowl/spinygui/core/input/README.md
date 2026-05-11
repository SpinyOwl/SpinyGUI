# com.spinyowl.spinygui.core.input

Input domain model for keyboard, mouse, shortcuts, and user-facing key mappings.

- Modules: core
- Source sets: main
- Direct classes: 10
- Descendant packages: 1

## Classes

### KeyAction

- Kind: enum
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/KeyAction.java`
- Declaration: `public enum KeyAction`
- Responsibility: Enumerates supported values for the named domain concept.

### Keyboard

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/Keyboard.java`
- Declaration: `public class Keyboard`
- Responsibility: Represents keyboard in this package.

### KeyboardKey

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/KeyboardKey.java`
- Declaration: `public class KeyboardKey`
- Responsibility: Represents keyboard key in this package.

### KeyboardLayout

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/KeyboardLayout.java`
- Declaration: `public interface KeyboardLayout`
- Responsibility: Used to store key mapping to native keys.

### KeyCode

- Kind: enum
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/KeyCode.java`
- Declaration: `public enum KeyCode`
- Responsibility: Key code is code value of the physical key represented by the event.

### KeyMod

- Kind: enum
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/KeyMod.java`
- Declaration: `public enum KeyMod`
- Responsibility: Enumerates supported values for the named domain concept.

### MouseButton

- Kind: enum
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/MouseButton.java`
- Declaration: `public enum MouseButton`
- Responsibility: Enumerates supported values for the named domain concept.

### MouseService

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/MouseService.java`
- Declaration: `public interface MouseService`
- Responsibility: Provides ability to get and set cursor positions.

### Shortcut

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/Shortcut.java`
- Declaration: `public class Shortcut`
- Responsibility: Represents shortcut in this package.

### ShortcutRegistry

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/input/ShortcutRegistry.java`
- Declaration: `public interface ShortcutRegistry`
- Responsibility: Used to store shortcuts.

## Child Packages

- [com.spinyowl.spinygui.core.input.impl](impl/README.md) - This reference describes Default mutable implementations of input services, lists 3 direct classes, and aggregates 0 descendant packages.

## Aggregated Contents

This package aggregates 1 descendant package(s) with 3 descendant class(es).
