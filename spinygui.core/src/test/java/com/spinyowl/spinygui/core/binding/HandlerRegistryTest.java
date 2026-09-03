package com.spinyowl.spinygui.core.binding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class HandlerRegistryTest {

  @Test
  void resolvesOnlyTheRegisteredExactEventType() {
    HandlerRegistry registry = new HandlerRegistry();
    EventListener<ActionEvent> listener = event -> {};

    registry.register("save", ActionEvent.class, listener);

    assertSame(listener, registry.lookup("save", ActionEvent.class).orElseThrow());
    assertTrue(registry.lookup("missing", ActionEvent.class).isEmpty());
    assertThrows(
        IllegalArgumentException.class, () -> registry.lookup("save", MouseClickEvent.class));
  }

  @Test
  void rejectsDuplicateAndInvalidRegistrationsWithoutChangingRevision() {
    HandlerRegistry registry = new HandlerRegistry();
    EventListener<ActionEvent> listener = event -> {};

    assertThrows(
        NullPointerException.class, () -> registry.register(null, ActionEvent.class, listener));
    assertThrows(
        IllegalArgumentException.class, () -> registry.register("  ", ActionEvent.class, listener));
    assertEquals(0, registry.revision());

    registry.register("save", ActionEvent.class, listener);

    assertEquals(1, registry.revision());
    assertThrows(
        IllegalArgumentException.class,
        () -> registry.register("save", ActionEvent.class, event -> {}));
    assertEquals(1, registry.revision());
  }

  @Test
  void replacementIsVisibleToEveryEventTimeReference() {
    HandlerRegistry registry = new HandlerRegistry();
    EventListener<ActionEvent> first = event -> {};
    EventListener<ActionEvent> replacement = event -> {};
    Supplier<EventListener<ActionEvent>> firstReference =
        () -> registry.lookup("save", ActionEvent.class).orElseThrow();
    Supplier<EventListener<ActionEvent>> secondReference =
        () -> registry.lookup("save", ActionEvent.class).orElseThrow();
    registry.register("save", ActionEvent.class, first);

    assertSame(first, firstReference.get());
    assertSame(first, secondReference.get());

    registry.replace("save", ActionEvent.class, replacement);

    assertSame(replacement, firstReference.get());
    assertSame(replacement, secondReference.get());
    assertEquals(2, registry.revision());
  }

  @Test
  void replacementRejectsAbsentNamesAndEventTypeChanges() {
    HandlerRegistry registry = new HandlerRegistry();
    registry.register("save", ActionEvent.class, event -> {});

    assertThrows(
        IllegalArgumentException.class,
        () -> registry.replace("missing", ActionEvent.class, event -> {}));
    assertThrows(
        IllegalArgumentException.class,
        () -> registry.replace("save", MouseClickEvent.class, event -> {}));
    assertEquals(1, registry.revision());
  }

  @Test
  void replacementValidatesNullableArgumentsBeforeNamePresence() {
    HandlerRegistry registry = new HandlerRegistry();

    assertThrows(
        NullPointerException.class,
        () -> registry.replace("missing", (Class<ActionEvent>) null, event -> {}));
    assertThrows(
        NullPointerException.class,
        () -> registry.replace("missing", ActionEvent.class, null));
    assertEquals(0, registry.revision());
  }

  @Test
  void removalAdvancesRevisionOnlyWhenRegistryStateChanges() {
    HandlerRegistry registry = new HandlerRegistry();
    registry.register("save", ActionEvent.class, event -> {});

    assertTrue(registry.remove("save"));
    assertEquals(2, registry.revision());
    assertTrue(registry.lookup("save", ActionEvent.class).isEmpty());
    assertFalse(registry.remove("save"));
    assertEquals(2, registry.revision());
  }
}
