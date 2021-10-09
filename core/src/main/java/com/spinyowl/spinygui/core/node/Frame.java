package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.NonNull;
import lombok.ToString;

/** The root element that holds all stylesheets and other nodes. */
@ToString(exclude = {"styleSheets"})
public class Frame extends Element {

  public static final String FRAME_TAG_NAME = "winframe";

  /** List of stylesheets attached to frame. */
  protected final List<StyleSheet> styleSheets = new CopyOnWriteArrayList<>();

  /** Default constructor which provide {@link #FRAME_TAG_NAME} string to super constructor. */
  public Frame() {
    super(FRAME_TAG_NAME);
  }

  /**
   * Returns editable list of stylesheets attached to frame.
   *
   * @return editable list of stylesheets attached to frame.
   */
  public List<StyleSheet> styleSheets() {
    return styleSheets;
  }

  /**
   * Shorthand to add window close event listener.
   *
   * @param listener window close event listener to add.
   */
  public void addWindowCloseEventListener(EventListener<WindowCloseEvent> listener) {
    Objects.requireNonNull(listener);

    addListener(WindowCloseEvent.class, listener);
  }

  /**
   * Shorthand to remove window close event listener.
   *
   * @param listener window close event listener to remove.
   */
  public void removeWindowCloseEventListener(EventListener<WindowCloseEvent> listener) {
    Objects.requireNonNull(listener);

    removeListener(WindowCloseEvent.class, listener);
  }

  /**
   * Shorthand to get all window close event listeners assigned to this layer.
   *
   * @return all window close event listeners assigned to this layer.
   */
  public List<EventListener<WindowCloseEvent>> getWindowCloseEventListeners() {
    return getListeners(WindowCloseEvent.class);
  }

  /**
   * Used to search focused element. If there are more - returns first in the tree. Note that system
   * will use only first focused element.
   *
   * @return first focused element.
   */
  public Element getFocusedElement() {
    return findFocused(this);
  }

  private Element findFocused(Element element) {
    if (element.focused()) {
      return element;
    }
    return findFocused(element.children());
  }

  private Element findFocused(List<? extends Element> elements) {
    for (Element element : elements) {
      Element focused = findFocused(element);
      if (focused != null) {
        return focused;
      }
    }
    return null;
  }

  /**
   * Used to find element with specified id. Returns first found occurrence or null if no such
   * element.
   *
   * @param id id to search.
   * @return first found occurrence or null if no such element.
   */
  public Element getElementById(@NonNull String id) {
    return getElementsWithAttribute("id", id, true).stream().findFirst().orElse(null);
  }

  /**
   * Returns list of elements which contain specified attribute.
   *
   * @param attribute attribute to check.
   * @return list of elements with specified attribute.
   */
  public List<Element> getElementsWithAttribute(String attribute) {
    return getElementsWithAttribute(this, new ArrayList<>(), attribute);
  }

  /**
   * Returns list of elements which contain specified attribute with specified value.
   * @param attribute attribute to check.
   * @param value attribute value to check.
   * @return list of elements with specified attribute-value pair.
   */
  public List<Element> getElementsWithAttribute(String attribute, String value) {
    return getElementsWithAttribute(attribute, value, false);
  }

  private List<Element> getElementsWithAttribute(
      Element element, List<Element> foundElements, String attribute) {
    checkAttributeAndAdd(element, foundElements, attribute);
    element.children().forEach(e -> getElementsWithAttribute(e, foundElements, attribute));
    return foundElements;
  }

  private void checkAttributeAndAdd(
      Element element, List<Element> foundElements, String attribute) {
    if (element.hasAttribute(attribute)) {
      foundElements.add(element);
    }
  }

  private List<Element> getElementsWithAttribute(
      String attribute, String value, boolean stopAtFirst) {
    return getElementsWithAttribute(this, new ArrayList<>(), attribute, value, stopAtFirst);
  }

  private List<Element> getElementsWithAttribute(
      Element element,
      List<Element> foundElements,
      String attribute,
      String value,
      boolean stopAtFirst) {
    if (checkAttributeValueAndAdd(element, foundElements, attribute, value) && stopAtFirst) {
      return foundElements;
    }
    element
        .children()
        .forEach(e -> getElementsWithAttribute(e, foundElements, attribute, value, stopAtFirst));
    return foundElements;
  }

  private boolean checkAttributeValueAndAdd(
      Element element, List<Element> foundElements, String attribute, String value) {
    if (element.hasAttribute(attribute) && Objects.equals(value, element.getAttribute(attribute))) {
      foundElements.add(element);
      return true;
    }
    return false;
  }

  @Override
  public Frame frame() {
    return this;
  }
}
