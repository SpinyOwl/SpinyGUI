package com.spinyowl.spinygui.core.api;

import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.node.Element;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Layer class is root container for all nodes in layer. Contains root node of layer - layer
 * container, list of stylesheets which should be used to calculate node styles
 */
@Getter
@Setter
@ToString(exclude = "frame")
@EqualsAndHashCode(exclude = "frame")
public class Layer extends Element {

  /**
   * Parent frame.
   */
  @Setter(AccessLevel.NONE)
  private Frame frame;

  /**
   * Determines if  current layer allow to pass events to bottom layer if event wasn't handled by
   * components of this layer.
   */
  private boolean eventPassable = true;

  /**
   * Determines if current layer and all of it components can receive events.
   */
  private boolean eventReceivable = true;

  public Layer() {
    super("layer");
  }

  /**
   * Used to attach layer to frame.
   *
   * @param frame frame to attach.
   */
  protected void frame(Frame frame) {
    if (frame == this.frame) {
      return;
    }
    if (this.frame != null) {
      this.frame.removeLayer(this);
    }
    this.frame = frame;
    if (frame != null) {
      frame.addLayer(this);
    }
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
}