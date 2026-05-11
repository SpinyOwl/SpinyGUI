# com.spinyowl.spinygui.core.system.event

Raw platform/window/input events before conversion into application-level events.

- Modules: core
- Source sets: main, test
- Direct classes: 16
- Descendant packages: 3

## Classes

### SystemCharEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemCharEvent.java`
- Declaration: `public class SystemCharEvent extends SystemEvent`
- Responsibility: Unicode character is input.

### SystemCharModsEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemCharModsEvent.java`
- Declaration: `public class SystemCharModsEvent extends SystemEvent`
- Responsibility: Event on Unicode character input regardless of what modifier keys are used.

### SystemCursorEnterEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemCursorEnterEvent.java`
- Declaration: `public class SystemCursorEnterEvent extends SystemEvent`
- Responsibility: Event that generated when the cursor enters or leaves the client area of the window.

### SystemCursorPosEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemCursorPosEvent.java`
- Declaration: `public class SystemCursorPosEvent extends SystemEvent`
- Responsibility: Will be generated when the cursor is moved.

### SystemEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemEvent.java`
- Declaration: `public class SystemEvent`
- Responsibility: Defines tree of system events.

### SystemFileDropEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemFileDropEvent.java`
- Declaration: `public class SystemFileDropEvent extends SystemEvent`
- Responsibility: Event payload object for the named input/window/node change.

### SystemFramebufferSizeEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemFramebufferSizeEvent.java`
- Declaration: `public class SystemFramebufferSizeEvent extends SystemEvent`
- Responsibility: Will be generated when the framebuffer of the specified window is resized.

### SystemKeyEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemKeyEvent.java`
- Declaration: `public class SystemKeyEvent extends SystemEvent`
- Responsibility: Will be generated when a key is pressed, repeated or released.

### SystemMouseClickEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemMouseClickEvent.java`
- Declaration: `public class SystemMouseClickEvent extends SystemEvent`
- Responsibility: Will be generated when a mouse button is pressed or released.

### SystemScrollEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemScrollEvent.java`
- Declaration: `public class SystemScrollEvent extends SystemEvent`
- Responsibility: Will be generated when a scrolling device is used, such as a mouse wheel or scrolling area of a touchpad.

### SystemWindowCloseEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemWindowCloseEvent.java`
- Declaration: `public class SystemWindowCloseEvent extends SystemEvent`
- Responsibility: Will be generated when the user attempts to close the specified window, for example by clicking the close widget in the title bar.

### SystemWindowFocusEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemWindowFocusEvent.java`
- Declaration: `public class SystemWindowFocusEvent extends SystemEvent`
- Responsibility: Event payload object for the named input/window/node change.

### SystemWindowIconifyEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemWindowIconifyEvent.java`
- Declaration: `public class SystemWindowIconifyEvent extends SystemEvent`
- Responsibility: Will be generated when the specified window is iconified or restored.

### SystemWindowPosEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemWindowPosEvent.java`
- Declaration: `public class SystemWindowPosEvent extends SystemEvent`
- Responsibility: Will be generated when the specified window moves.

### SystemWindowRefreshEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemWindowRefreshEvent.java`
- Declaration: `public class SystemWindowRefreshEvent extends SystemEvent`
- Responsibility: Will be generated when the client area of the specified window needs to be redrawn, for example if the window has been exposed after having been covered by another window.

### SystemWindowSizeEvent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/SystemWindowSizeEvent.java`
- Declaration: `public class SystemWindowSizeEvent extends SystemEvent`
- Responsibility: Will be generated when the specified window is resized.

## Child Packages

- [com.spinyowl.spinygui.core.system.event.listener](listener/README.md) - This reference describes Adapters that translate raw system events into core event processing and state changes, lists 27 direct classes, and aggregates 0 descendant packages.
- [com.spinyowl.spinygui.core.system.event.processor](processor/README.md) - This reference describes System-event processor contract and implementation for dispatching platform events, lists 2 direct classes, and aggregates 0 descendant packages.
- [com.spinyowl.spinygui.core.system.event.provider](provider/README.md) - This reference describes Provider for mapping raw system event classes to listener instances, lists 2 direct classes, and aggregates 0 descendant packages.

## Aggregated Contents

This package aggregates 3 descendant package(s) with 31 descendant class(es).
