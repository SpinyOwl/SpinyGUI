package com.spinyowl.spinygui.core.binding;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Caller-owned registry of named, exactly typed GUI event listeners.
 *
 * <p>The owning UI thread may mutate the registry between event batches. Every successful
 * registration, replacement, or removal advances {@link #revision()}, allowing consumers to
 * distinguish unresolved states without traversing or rebinding a node tree.
 */
public final class HandlerRegistry {
  /** Current name-to-registration state, owned and mutated by the caller's UI thread. */
  private final Map<String, Registration<?>> registrations = new LinkedHashMap<>();

  /** Mutation generation advanced after every successful state change. */
  private long revision;

  /** Creates an empty registry at revision zero. */
  public HandlerRegistry() {}

  /**
   * Registers a new handler name and exact event type.
   *
   * @throws IllegalArgumentException when the name is blank or already registered
   * @throws NullPointerException when an argument is {@code null}
   */
  public <T extends Event> void register(
      String name, Class<T> eventClass, EventListener<T> listener) {
    String validatedName = requireName(name);
    Registration<T> registration = registration(eventClass, listener);
    if (registrations.putIfAbsent(validatedName, registration) != null) {
      throw new IllegalArgumentException("Handler name is already registered: " + validatedName);
    }
    advanceRevision();
  }

  /**
   * Replaces the listener for an existing name while preserving its exact event type.
   *
   * @throws IllegalArgumentException when the name is blank or absent, or the event type differs
   *     from the registered type
   * @throws NullPointerException when an argument is {@code null}
   */
  public <T extends Event> void replace(
      String name, Class<T> eventClass, EventListener<T> listener) {
    String validatedName = requireName(name);
    Registration<T> replacement = registration(eventClass, listener);
    Registration<?> current = registrations.get(validatedName);
    if (current == null) {
      throw new IllegalArgumentException("Handler name is not registered: " + validatedName);
    }
    requireExactType(validatedName, eventClass, current.eventClass());
    registrations.put(validatedName, replacement);
    advanceRevision();
  }

  /**
   * Resolves a handler by name and exact event type.
   *
   * @return the current listener, or an empty value when the name is not registered
   * @throws IllegalArgumentException when the name is blank or its registered event type differs
   *     from the requested type
   * @throws NullPointerException when an argument is {@code null}
   */
  public <T extends Event> Optional<EventListener<T>> lookup(String name, Class<T> eventClass) {
    String validatedName = requireName(name);
    Objects.requireNonNull(eventClass, "eventClass");
    Registration<?> registration = registrations.get(validatedName);
    if (registration == null) {
      return Optional.empty();
    }
    requireExactType(validatedName, eventClass, registration.eventClass());
    return Optional.of(castListener(registration));
  }

  /**
   * Removes the current registration for a name.
   *
   * @return {@code true} when a registration was removed; {@code false} when the name was absent
   * @throws IllegalArgumentException when the name is blank
   * @throws NullPointerException when the name is {@code null}
   */
  public boolean remove(String name) {
    String validatedName = requireName(name);
    if (registrations.remove(validatedName) == null) {
      return false;
    }
    advanceRevision();
    return true;
  }

  /** Returns the monotonically increasing revision of successful registry mutations. */
  public long revision() {
    return revision;
  }

  private static String requireName(String name) {
    Objects.requireNonNull(name, "name");
    if (name.isBlank()) {
      throw new IllegalArgumentException("Handler name must not be blank");
    }
    return name;
  }

  private static <T extends Event> Registration<T> registration(
      Class<T> eventClass, EventListener<T> listener) {
    return new Registration<>(
        Objects.requireNonNull(eventClass, "eventClass"),
        Objects.requireNonNull(listener, "listener"));
  }

  private static void requireExactType(
      String name, Class<? extends Event> requested, Class<? extends Event> registered) {
    Objects.requireNonNull(requested, "eventClass");
    if (!registered.equals(requested)) {
      throw new IllegalArgumentException(
          "Handler '"
              + name
              + "' is registered for "
              + registered.getName()
              + ", not "
              + requested.getName());
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends Event> EventListener<T> castListener(Registration<?> registration) {
    return (EventListener<T>) registration.listener();
  }

  private void advanceRevision() {
    revision = Math.incrementExact(revision);
  }

  /** Internal invariant pairing a listener with the one exact event type accepted for its name. */
  private record Registration<T extends Event>(
      Class<T> eventClass, EventListener<T> listener) {}
}
