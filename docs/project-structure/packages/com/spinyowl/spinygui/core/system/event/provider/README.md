# com.spinyowl.spinygui.core.system.event.provider

Provider for mapping raw system event classes to listener instances.

- Modules: core
- Source sets: main
- Direct classes: 2
- Descendant packages: 0

## Classes

### SystemEventListenerProvider

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/provider/SystemEventListenerProvider.java`
- Declaration: `public interface SystemEventListenerProvider`
- Responsibility: Used to store system event class to system event listener mapping that would be used by SpinyGUI.

### SystemEventListenerProviderImpl

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/provider/SystemEventListenerProviderImpl.java`
- Declaration: `public class SystemEventListenerProviderImpl implements SystemEventListenerProvider`
- Responsibility: Default implementation based on HashMap.
