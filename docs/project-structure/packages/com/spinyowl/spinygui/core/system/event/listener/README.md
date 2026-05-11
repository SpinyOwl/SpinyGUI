# com.spinyowl.spinygui.core.system.event.listener

Adapters that translate raw system events into core event processing and state changes.

- Modules: core
- Source sets: main, test
- Direct classes: 27
- Descendant packages: 0

## Classes

### AbstractSystemEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/AbstractSystemEventListener.java`
- Declaration: `abstract class AbstractSystemEventListener<E extends SystemEvent> implements SystemEventListener<E>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemCharEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemCharEventListener.java`
- Declaration: `public class SystemCharEventListener extends AbstractSystemEventListener<SystemCharEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemCharEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemCharEventListenerTest.java`
- Declaration: `class SystemCharEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemCursorEnterEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemCursorEnterEventListener.java`
- Declaration: `public class SystemCursorEnterEventListener extends AbstractSystemEventListener<SystemCursorEnterEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemCursorPosEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemCursorPosEventListener.java`
- Declaration: `public class SystemCursorPosEventListener extends AbstractSystemEventListener<SystemCursorPosEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemCursorPosEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemCursorPosEventListenerTest.java`
- Declaration: `class SystemCursorPosEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemEventListener

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemEventListener.java`
- Declaration: `public interface SystemEventListener<E extends SystemEvent>`
- Responsibility: Used to listen, process and translate system event to gui event.

### SystemFileDropEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemFileDropEventListener.java`
- Declaration: `public class SystemFileDropEventListener extends AbstractSystemEventListener<SystemFileDropEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemFileDropEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemFileDropEventListenerTest.java`
- Declaration: `class SystemFileDropEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemKeyEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemKeyEventListener.java`
- Declaration: `public class SystemKeyEventListener extends AbstractSystemEventListener<SystemKeyEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemKeyEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemKeyEventListenerTest.java`
- Declaration: `class SystemKeyEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemMouseClickEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemMouseClickEventListener.java`
- Declaration: `public class SystemMouseClickEventListener extends AbstractSystemEventListener<SystemMouseClickEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemMouseClickEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemMouseClickEventListenerTest.java`
- Declaration: `class SystemMouseClickEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemScrollEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemScrollEventListener.java`
- Declaration: `public class SystemScrollEventListener extends AbstractSystemEventListener<SystemScrollEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemScrollEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemScrollEventListenerTest.java`
- Declaration: `class SystemScrollEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemWindowFocusEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowFocusEventListener.java`
- Declaration: `public class SystemWindowFocusEventListener extends AbstractSystemEventListener<SystemWindowFocusEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemWindowFocusEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowFocusEventListenerTest.java`
- Declaration: `class SystemWindowFocusEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemWindowIconifyEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowIconifyEventListener.java`
- Declaration: `public class SystemWindowIconifyEventListener extends AbstractSystemEventListener<SystemWindowIconifyEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemWindowIconifyEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowIconifyEventListenerTest.java`
- Declaration: `class SystemWindowIconifyEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemWindowPosEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowPosEventListener.java`
- Declaration: `public class SystemWindowPosEventListener extends AbstractSystemEventListener<SystemWindowPosEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemWindowPosEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowPosEventListenerTest.java`
- Declaration: `class SystemWindowPosEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemWindowRefreshEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowRefreshEventListener.java`
- Declaration: `public class SystemWindowRefreshEventListener extends AbstractSystemEventListener<SystemWindowRefreshEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemWindowRefreshEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowRefreshEventListenerTest.java`
- Declaration: `class SystemWindowRefreshEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemWindowsCloseEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowsCloseEventListener.java`
- Declaration: `public class SystemWindowsCloseEventListener extends AbstractSystemEventListener<SystemWindowCloseEvent>`
- Responsibility: Generates WindowCloseEvent for frame and pushes it to EventProcessor.

### SystemWindowsCloseEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowsCloseEventListenerTest.java`
- Declaration: `class SystemWindowsCloseEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.

### SystemWindowSizeEventListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowSizeEventListener.java`
- Declaration: `public class SystemWindowSizeEventListener extends AbstractSystemEventListener<SystemWindowSizeEvent>`
- Responsibility: System-event listener/adapter for translating platform events into core behavior.

### SystemWindowSizeEventListenerTest

- Kind: class
- Source: `core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/SystemWindowSizeEventListenerTest.java`
- Declaration: `class SystemWindowSizeEventListenerTest`
- Responsibility: JUnit test fixture for the corresponding production component.
