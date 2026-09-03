package com.spinyowl.spinygui.core.binding;

import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * One validated named-handler declaration from the explicit XML event vocabulary.
 *
 * <p>Only lowercase {@code on-action} and {@code on-click} are recognized. Unsupported attributes
 * remain ordinary node attributes and never trigger event-class discovery or loading.
 */
public final class XmlEventDeclaration {
  /** Immutable built-in attribute-to-event mapping used for every declaration lookup. */
  private static final Map<String, Class<? extends Event>> EVENT_TYPES =
      Map.of("on-action", ActionEvent.class, "on-click", MouseClickEvent.class);

  /** Normalized supported XML attribute that owns this declaration. */
  private final String attributeName;

  /** Nonblank registry key declared by the attribute value. */
  private final String handlerName;

  /** Exact GUI event class associated with the supported attribute. */
  private final Class<? extends Event> eventClass;

  private XmlEventDeclaration(
      String attributeName, String handlerName, Class<? extends Event> eventClass) {
    this.attributeName = attributeName;
    this.handlerName = handlerName;
    this.eventClass = eventClass;
  }

  /**
   * Resolves a supported attribute and validates its declared handler name.
   *
   * <p>Unsupported attributes return an empty value without interpreting their contents.
   *
   * @return a declaration for a supported attribute, or an empty value for an unrelated attribute
   * @throws IllegalArgumentException when a supported declaration has a blank handler name
   * @throws NullPointerException when the attribute name, or a supported handler name, is null
   */
  public static Optional<XmlEventDeclaration> fromAttribute(
      String attributeName, String handlerName) {
    String name = Objects.requireNonNull(attributeName, "attributeName");
    Class<? extends Event> eventClass = EVENT_TYPES.get(name);
    if (eventClass == null) {
      return Optional.empty();
    }
    Objects.requireNonNull(handlerName, "handlerName");
    if (handlerName.isBlank()) {
      throw new IllegalArgumentException("Handler name must not be blank for " + name);
    }
    return Optional.of(new XmlEventDeclaration(name, handlerName, eventClass));
  }

  /** Returns the supported lowercase XML event attribute. */
  public String attributeName() {
    return attributeName;
  }

  /** Returns the validated nonblank registry key declared in XML. */
  public String handlerName() {
    return handlerName;
  }

  /** Returns the exact built-in GUI event class for this declaration. */
  public Class<? extends Event> eventClass() {
    return eventClass;
  }
}
