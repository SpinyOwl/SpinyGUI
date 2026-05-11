# com.spinyowl.spinygui.core.event

Application-level events emitted to nodes and event targets.

- Modules: core
- Source sets: main
- Direct classes: 21
- Descendant packages: 2

## Classes

### ChangePositionEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/ChangePositionEvent.java`
- Declaration: `public class ChangePositionEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### ChangeSizeEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/ChangeSizeEvent.java`
- Declaration: `public class ChangeSizeEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### ChangeTextEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/ChangeTextEvent.java`
- Declaration: `public class ChangeTextEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### CharEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/CharEvent.java`
- Declaration: `public class CharEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### CursorEnterEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/CursorEnterEvent.java`
- Declaration: `public class CursorEnterEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### CursorExitEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/CursorExitEvent.java`
- Declaration: `public class CursorExitEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### Event

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/Event.java`
- Declaration: `public class Event`
- Responsibility: Event payload object for the named input/window/node change.

### EventTarget

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/EventTarget.java`
- Declaration: `public interface EventTarget`
- Responsibility: Event target interface.

### FileDropEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/FileDropEvent.java`
- Declaration: `public class FileDropEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### FocusInEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/FocusInEvent.java`
- Declaration: `public class FocusInEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### FocusOutEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/FocusOutEvent.java`
- Declaration: `public class FocusOutEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### KeyboardEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/KeyboardEvent.java`
- Declaration: `public class KeyboardEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### MouseClickEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/MouseClickEvent.java`
- Declaration: `public class MouseClickEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### MouseDragEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/MouseDragEvent.java`
- Declaration: `public class MouseDragEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### ScrollEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/ScrollEvent.java`
- Declaration: `public class ScrollEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### WindowCloseEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/WindowCloseEvent.java`
- Declaration: `public class WindowCloseEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### WindowFocusEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/WindowFocusEvent.java`
- Declaration: `public class WindowFocusEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### WindowIconifyEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/WindowIconifyEvent.java`
- Declaration: `public class WindowIconifyEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### WindowPosEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/WindowPosEvent.java`
- Declaration: `public class WindowPosEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### WindowRefreshEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/WindowRefreshEvent.java`
- Declaration: `public class WindowRefreshEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

### WindowSizeEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/event/WindowSizeEvent.java`
- Declaration: `public class WindowSizeEvent extends Event`
- Responsibility: Event payload object for the named input/window/node change.

## Child Packages

- [com.spinyowl.spinygui.core.event.listener](listener/README.md) - Generic event listener contract for application events.
- [com.spinyowl.spinygui.core.event.processor](processor/README.md) - Dispatch logic for routing application events to node listeners.

## Aggregated Contents

This package aggregates 2 descendant package(s) with 3 descendant class(es).
