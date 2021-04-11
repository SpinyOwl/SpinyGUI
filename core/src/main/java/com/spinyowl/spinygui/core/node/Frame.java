package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The root element that holds all stylesheets and other nodes.
 */
@ToString
@EqualsAndHashCode
public class Frame extends Element {

  public static final String FRAME_TAG_NAME = "frame";

  /**
   * List of stylesheets attached to frame.
   */
  protected final List<StyleSheet> styleSheets = new CopyOnWriteArrayList<>();

  /**
   * Default constructor which provide {@code "frame"} string to super constructor.
   */
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

  @Override
  public Frame frame() {
    return this;
  }
}
