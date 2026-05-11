# com.spinyowl.spinygui.core.system.event.processor

System-event processor contract and implementation for dispatching platform events.

- Modules: core
- Source sets: main
- Direct classes: 2
- Descendant packages: 0

## Classes

### SystemEventProcessor

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/processor/SystemEventProcessor.java`
- Declaration: `public interface SystemEventProcessor`
- Responsibility: System event processor.

### SystemEventProcessorImpl

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/event/processor/SystemEventProcessorImpl.java`
- Declaration: `public class SystemEventProcessorImpl implements SystemEventProcessor`
- Responsibility: Default implementation based on two ConcurrentLinkedQueue queues which swapped every time during processing.
