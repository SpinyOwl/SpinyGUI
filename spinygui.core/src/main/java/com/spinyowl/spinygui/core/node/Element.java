package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.util.Reference.containsReference;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.diagnostic.FrameDiagnosticCounter;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.ScrollbarPart;
import com.spinyowl.spinygui.core.util.ScrollbarGeometry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
@ToString(exclude = {"childNodes", "resolvedStyle", "listenerMap", "inlineFragments"})
public class Element extends Node implements EventTarget {

  /** The number of pixels that an element's content is scrolled vertically. */
  @Setter(AccessLevel.NONE)
  private float scrollTop;
  /** The number of pixels that an element's content is scrolled from its left edge. */
  @Setter(AccessLevel.NONE)
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

  private final List<InlineFragment> inlineFragments = new ArrayList<>();

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
  private final ResolvedStyle resolvedStyle = new ResolvedStyle();

  /** Values used for rendering and input in the current frame, separate from computed CSS state. */
  private final PresentationState presentationState = new PresentationState();

  /** Typed paint-facing view of the current presentation values. */
  private final PresentedStyle presentedStyle = new PresentedStyle(resolvedStyle, presentationState);

  private final Map<ScrollbarPart, ResolvedStyle> scrollbarStyles =
      new EnumMap<>(ScrollbarPart.class);

  /** Scrollbar metrics calculated during layout and reused by render/input paths. */
  private ScrollbarGeometry.Metrics scrollbarMetrics;

  /** Node attributes. */
  @Setter(AccessLevel.NONE)
  private final Map<String, String> attributes = new HashMap<>();

  /**
   * Map of listeners attached that should be attached for node and processed if any event
   * performed.
   */
  @SuppressWarnings("rawtypes")
  @Setter(AccessLevel.NONE)
  private final Map<Class<? extends Event>, List<? extends EventListener>> listenerMap =
      new HashMap<>();

  public Element(String nodeName) {
    super(nodeName);
  }

  public void setAttribute(String key, String value) {
    String previous = attributes.put(key, value);
    if (!java.util.Objects.equals(previous, value)) invalidateStyleSource();
  }

  public String getAttribute(String key) {
    return attributes.get(key);
  }

  public boolean hasAttribute(String attribute) {
    return attributes.containsKey(attribute);
  }

  /** Returns whether this element is a control disabled by its {@code disabled} attribute. */
  public boolean disabled() {
    return false;
  }

  @Override
  public void removeAttribute(String attribute) {
    if (attributes.remove(attribute) != null) invalidateStyleSource();
  }

  public boolean hasAttributes() {
    return !attributes.isEmpty();
  }

  @Override
  public Map<String, String> attributes() {
    return Collections.unmodifiableMap(attributes);
  }

  public void setAttributes(Map<String, String> values) {
    values.forEach(this::setAttribute);
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
        listenerMap.computeIfAbsent(eventClass, aClass -> new CopyOnWriteArrayList<>());
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

  @Override
  public void removeChild(@NonNull Node node) {
    if (childNodes.remove(node)) {
      Frame owner = frame();
      unlinkSiblings(node);
      frameDiagnostics().increment(FrameDiagnosticCounter.MUTATION_DETACHMENTS);
      resetPresentationState(node);
      node.assignParent(null);
      node.previousSibling(null);
      node.nextSibling(null);
      if (owner != null) owner.invalidateStyle();
    }
  }

  private void resetPresentationState(Node node) {
    if (node instanceof Element element) {
      element.presentationState().reset();
      element.children().forEach(this::resetPresentationState);
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
    if (firstChild == node) firstChild = next;
    if (lastChild == node) lastChild = prev;
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

    frameDiagnostics().increment(FrameDiagnosticCounter.MUTATION_ATTACHMENTS);

    linkSiblings(node);

    node.assignParent(this);
    Frame owner = frame();
    if (owner != null) owner.invalidateStyle();
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
    frameDiagnostics().increment(FrameDiagnosticCounter.CHILD_NODE_VIEW_READS);
    return Collections.unmodifiableList(childNodes);
  }

  @Override
  public boolean hasChildNodes() {
    return !childNodes.isEmpty();
  }

  public List<InlineFragment> inlineFragments() {
    return Collections.unmodifiableList(inlineFragments);
  }

  public void inlineFragments(List<InlineFragment> fragments) {
    inlineFragments.clear();
    inlineFragments.addAll(fragments);
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
    setAttribute("style", style);
  }

  public void scrollTop(float scrollTop) {
    if (Float.compare(this.scrollTop, scrollTop) == 0) return;
    this.scrollTop = scrollTop;
    invalidateTransformSource();
  }

  public void scrollLeft(float scrollLeft) {
    if (Float.compare(this.scrollLeft, scrollLeft) == 0) return;
    this.scrollLeft = scrollLeft;
    invalidateTransformSource();
  }

  /** Applies layout-owned scroll clamping without creating a new source revision. */
  public void resolveScrollOffsets(float scrollLeft, float scrollTop) {
    this.scrollLeft = scrollLeft;
    this.scrollTop = scrollTop;
  }

  public Map<ScrollbarPart, ResolvedStyle> scrollbarStyles() {
    return Collections.unmodifiableMap(scrollbarStyles);
  }

  public ResolvedStyle scrollbarStyle(ScrollbarPart part) {
    return scrollbarStyles.get(part);
  }

  public ResolvedStyle getOrCreateScrollbarStyle(ScrollbarPart part) {
    return scrollbarStyles.computeIfAbsent(part, ignored -> new ResolvedStyle());
  }

  public void clearScrollbarStyles() {
    scrollbarStyles.clear();
  }

  public String getClassAttribute() {
    return attributes.get("class");
  }

  public String getIdAttribute() {
    return attributes.get("id");
  }
}
