package com.spinyowl.spinygui.core.binding;

import com.spinyowl.spinygui.core.event.Event;
import java.util.Objects;

/**
 * Structured warning produced when an XML event declaration cannot resolve its handler.
 *
 * @param reason unresolved registry state
 * @param eventAttribute XML event attribute that declared the handler
 * @param handlerName current declared handler name
 * @param eventClass exact event type requested by the declaration
 * @param elementReference human-readable element identity or deterministic tree path
 * @param registryRevision current registry revision, or {@code -1} when no registry is available
 */
public record BindingDiagnostic(
    Reason reason,
    String eventAttribute,
    String handlerName,
    Class<? extends Event> eventClass,
    String elementReference,
    long registryRevision) {

  /**
   * Creates a validated diagnostic. A revision of {@code -1} denotes an unavailable registry.
   *
   * @throws IllegalArgumentException when text fields are blank or the revision is less than -1
   * @throws NullPointerException when a required component is {@code null}
   */
  public BindingDiagnostic {
    Objects.requireNonNull(reason, "reason");
    eventAttribute = requireText(eventAttribute, "eventAttribute");
    handlerName = requireText(handlerName, "handlerName");
    Objects.requireNonNull(eventClass, "eventClass");
    elementReference = requireText(elementReference, "elementReference");
    if (registryRevision < -1) {
      throw new IllegalArgumentException("registryRevision must be -1 or greater");
    }
  }

  private static String requireText(String value, String label) {
    Objects.requireNonNull(value, label);
    if (value.isBlank()) {
      throw new IllegalArgumentException(label + " must not be blank");
    }
    return value;
  }

  /** Resolution state that caused a warning-policy diagnostic. */
  public enum Reason {
    /** No registry was available at event-dispatch time. */
    REGISTRY_UNAVAILABLE,
    /** The current registry had no entry for the declared handler name. */
    HANDLER_MISSING
  }
}
