package com.spinyowl.spinygui.core.window;

import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.node.Node;
import java.util.List;
import java.util.Objects;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Window class. Represents OS window.
 * <br>
 * <br>
 * <b>If you need to add custom functionality to winodw class - you need to create proxy for
 * instance created by static method!</b>
 */
public abstract class Window {

  public static final EventListener<WindowCloseEvent> EXIT_ON_CLOSE = event -> System.exit(0);
  private final Frame frame;
  /**
   * Root panel.
   */
  private volatile boolean closed = false;
  private String title;

  protected Window() {
    frame = new Frame();
  }

  protected Window(Frame frame) {
    this.frame = Objects.requireNonNull(frame);
  }

  public abstract long pointer();

  public String title() {
    return title;
  }

  public void title(String title) {
    this.title = Objects.requireNonNull(title);
  }

  public abstract Vector2ic position();
  public abstract void position(Vector2i position);
  public abstract void position(int x, int y);

  public abstract Vector2ic size();
  public abstract void size(Vector2i size);
  public abstract void size(int width, int height);

  public abstract boolean visible();
  public abstract void setVisible(boolean visible);

  public abstract boolean closed();
  public abstract void close();

  public abstract Monitor monitor();
  public abstract void monitor(Monitor monitor);

  public void addCloseEventListener(EventListener<WindowCloseEvent> listener) {
    frame.addWindowCloseEventListener(Objects.requireNonNull(listener));
  }

  public void removeCloseEventListener(EventListener<WindowCloseEvent> listener) {
    frame.removeWindowCloseEventListener(Objects.requireNonNull(listener));
  }

  public List<EventListener<WindowCloseEvent>> getCloseEventListeners() {
    return frame.getWindowCloseEventListeners();
  }

  public abstract Node focusOwner();

  public Frame frame() {
    return frame;
  }
}
