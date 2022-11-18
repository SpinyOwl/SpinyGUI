package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.util.Reference.containsReference;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"childNodes", "resolvedStyle", "listeners"})
public class Element extends Node implements EventTarget {

  /** The number of pixels that an element's content is scrolled vertically. */
  private float scrollTop;
  /** The number of pixels that an element's content is scrolled from its left edge. */
  private float scrollLeft;
  /**
   * A measurement of the width of an element's content, including content not visible on the screen
   * due to overflow.
   */
  private float scrollWidth;
  /**
   * A measurement of the height of an element's content, including content not visible on the
   * screen due to overflow.
   */
  private float scrollHeight;
  /**
   * The inner width of an element in pixels, including padding but excluding borders, margins, and
   * vertical scrollbars (if rendered).
   */
  private float clientWidth;
  /**
   * The inner height of an element in pixels, including padding but excluding borders, margins, and
   * horizontal scrollbars (if present).
   */
  private float clientHeight;

  /** Child nodes. */
  private final List<Node> childNodes = new LinkedList<>();

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Node firstChild;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Node lastChild;

  /**
   * Used to store all resolved styles by style engine (from defaults, stylesheets and element style
   * attribute).
   */
  @Setter(AccessLevel.NONE)
  private final ResolvedStyle resolvedStyle = new ResolvedStyle();

  /** Node attributes. */
  @Setter(AccessLevel.NONE)
  private final Map<String, String> attributes = new HashMap<>();

  /**
   * Map of listeners attached that should be attached for node and processed if any event
   * performed.
   */
  @SuppressWarnings("rawtypes")
  @Setter(AccessLevel.NONE)
  private final Map<Class<? extends Event>, List<? extends EventListener>> listeners =
      new HashMap<>();

  public Element(String nodeName) {
    super(nodeName);
  }

  public void setAttribute(String key, String value) {
    attributes.put(key, value);
  }

  public String getAttribute(String key) {
    return attributes.get(key);
  }

  public boolean hasAttribute(String attribute) {
    return attributes.containsKey(attribute);
  }

  @Override
  public void removeAttribute(String attribute) {
    attributes.remove(attribute);
  }

  public boolean hasAttributes() {
    return !attributes.isEmpty();
  }

  public <T extends Event> void addListener(Class<T> eventClass, EventListener<T> listener) {
    getOrCreateListener(eventClass).add(listener);
  }

  public <T extends Event> void removeListener(Class<T> eventClass, EventListener<T> listener) {
    getOrCreateListener(eventClass).remove(listener);
  }

  public <T extends Event> List<EventListener<T>> getListeners(Class<T> eventClass) {
    return getOrCreateListener(eventClass);
  }

  @SuppressWarnings("unchecked")
  private <T extends Event> List<EventListener<T>> getOrCreateListener(Class<T> eventClass) {
    return (List<EventListener<T>>)
        listeners.computeIfAbsent(eventClass, aClass -> new CopyOnWriteArrayList<>());
  }

  /**
   * Returns true if there is at least one event listener for specified event class.
   *
   * @param eventClass event class.
   * @return true if there is at least one event listener for specified event class.
   */
  public boolean hasListenersFor(Class<? extends Event> eventClass) {
    return listeners.containsKey(eventClass) && !listeners.get(eventClass).isEmpty();
  }

  @Override
  public void removeChild(@NonNull Node node) {
    unlinkSiblings(node);
    if (childNodes.remove(node)) {
      node.parent(null);
    }
  }

  private void unlinkSiblings(Node node) {
    var prev = node.previousSibling();
    var next = node.nextSibling();
    if (prev != null) {
      prev.nextSibling(next);
    }
    if (next != null) {
      next.previousSibling(prev);
    }
  }

  @Override
  public void addChild(@NonNull Node node) {
    if (node == this || containsReference(childNodes, node)) {
      return;
    }

    Element parent = node.parent();
    if (parent != null) {
      parent.removeChild(node);
    }

    childNodes.add(node);

    linkSiblings(node);

    node.parent(this);
  }

  private void linkSiblings(Node node) {
    if (firstChild == null) {
      firstChild = node;
    }

    if (lastChild != null) {
      lastChild.nextSibling(node);
      node.previousSibling(lastChild);
    }
    lastChild = node;
  }

  @Override
  public List<Node> childNodes() {
    return Collections.unmodifiableList(childNodes);
  }

  @Override
  public boolean hasChildNodes() {
    return !childNodes.isEmpty();
  }

  public Element nextElementSibling() {
    Node next = nextSibling();
    while (next != null && !(next instanceof Element)) {
      next = next.nextSibling();
    }
    return (Element) next;
  }

  public Element previousElementSibling() {
    Node previous = previousSibling();
    while (previous != null && !(previous instanceof Element)) {
      previous = previous.previousSibling();
    }
    return (Element) previous;
  }

  public String style() {
    return attributes().get("style");
  }

  public void style(String style) {
    attributes().put("style", style);
  }

  public String getClassAttribute() {
    return attributes.get("class");
  }

  public String getIdAttribute() {
    return attributes.get("id");
  }
}
