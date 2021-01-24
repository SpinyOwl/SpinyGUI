package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.util.Reference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"firstChild", "lastChild"})
public class Element extends Node implements EventTarget {

  /**
   * Child nodes.
   */
  private final List<Node> childNodes = new LinkedList<>();

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Node firstChild;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Node lastChild;

  /**
   * Used to overload styles from stylesheet.
   */
  @Setter(AccessLevel.NONE)
  private final NodeStyle style = new NodeStyle();
  /**
   * Styles from stylesheet. Updated by style manager every frame state changes.
   */
  @Setter(AccessLevel.NONE)
  private final NodeStyle calculatedStyle = new NodeStyle();
  /**
   * Node attributes.
   */
  @Setter(AccessLevel.NONE)
  private final Map<String, String> attributes = new ConcurrentHashMap<>();
  /**
   * Map of listeners attached that should be attached for node and processed if any event
   * performed.
   */
  @Setter(AccessLevel.NONE)
  private final Map<Class<? extends Event>, List<? extends EventListener>> listenerMap = new ConcurrentHashMap<>();

  public Element(String nodeName) {
    super(nodeName);
  }

  /**
   * Shorthand to set attribute.
   *
   * @param key   attribute name.
   * @param value attribute value.
   */
  public void setAttribute(String key, String value) {
    attributes.put(key, value);
  }

  /**
   * Shorthand to get attribute.
   *
   * @param key attribute name.
   * @return attribute value.
   */
  public String getAttribute(String key) {
    return attributes.get(key);
  }

  public boolean hasAttribute(String attribute) {
    return attributes.containsKey(attribute);
  }

  public boolean hasAttributes() {
    return !attributes.isEmpty();
  }

  /**
   * Used to add event listener for specified event class.
   *
   * @param eventClass event class.
   * @param listener   event listener to add.
   * @param <T>        type of event for which adding event listener.
   */
  public <T extends Event> void addListener(Class<T> eventClass, EventListener<T> listener) {
    getOrCreate(eventClass).add(listener);
  }

  public <T extends Event> void removeListener(Class<T> eventClass,
      EventListener<T> listener) {
    getOrCreate(eventClass).remove(listener);
  }

  /**
   * Returns listener list for specified event class.
   *
   * @param eventClass event class.
   * @param <T>        type of event.
   * @return list of event listeners for specified event class.
   */
  public <T extends Event> List<EventListener<T>> getListeners(Class<T> eventClass) {
    return getOrCreate(eventClass);
  }

  private <T extends Event> List<EventListener<T>> getOrCreate(Class<T> eventClass) {
    return (List<EventListener<T>>) listenerMap
        .computeIfAbsent(eventClass, aClass -> new CopyOnWriteArrayList<>());
  }

  /**
   * Returns true if there is at least one event listener for specified event class.
   *
   * @param eventClass event class.
   * @return true if there is at least one event listener for specified event class.
   */
  public boolean hasListenersFor(Class<? extends Event> eventClass) {
    return listenerMap.containsKey(eventClass) && !listenerMap.get(eventClass).isEmpty();
  }

  /**
   * Used to remove child node.
   *
   * @param node node to remove.
   */
  @Override
  public void removeChild(Node node) {
    if (node != null) {
      unlinkSiblings(node);
      if (childNodes.remove(node)) {
        node.parent(null);
      }
    }
  }

  private void unlinkSiblings(Node node) {
    Node prev = node.previousSibling();
    Node next = node.nextSibling();
    if (prev != null) {
      prev.nextSibling(next);
    }
    if (next != null) {
      next.previousSibling(prev);
    }
  }

  /**
   * Used to add child node.
   *
   * @param node node to add.
   */
  @Override
  public void addChild(Node node) {
    if (node == null || node == this || Reference.contains(childNodes, node)) {
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

  /**
   * Used to get child nodes.
   *
   * @return list of child nodes.
   */
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
}
