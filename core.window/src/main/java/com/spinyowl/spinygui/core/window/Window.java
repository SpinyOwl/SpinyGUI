package com.spinyowl.spinygui.core.window;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.window.service.Services;
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

  public Window() {
    frame = new Frame();
  }

  public static Window createWindow(String title, int width, int height) {
    return Services.getWindowService().createWindow(width, height, title);
  }

  public static Window createWindow(String title, int width, int height, Monitor monitor) {
    return Services.getWindowService().createWindow(width, height, title, monitor);
  }

  public abstract long getPointer();

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    if (title != null) {
      this.title = title;
      Services.getWindowService().setWindowTitle(this, title);
    }
  }

  public Vector2ic getPosition() {
    return Services.getWindowService().getWindowPosition(this);
  }

  public void position(Vector2i position) {
    Services.getWindowService().setWindowPosition(this, position);
  }

  public void setPosition(int x, int y) {
    position(new Vector2i(x, y));
  }

  public Vector2ic getSize() {
    return Services.getWindowService().getWindowSize(this);
  }

  public void setSize(Vector2i size) {
    Services.getWindowService().setWindowSize(this, size);
  }

  public void setSize(int width, int height) {
    this.setSize(new Vector2i(width, height));
  }

  public boolean isVisible() {
    return Services.getWindowService().isWindowVisible(this);
  }

  public void setVisible(boolean visible) {
    Services.getWindowService().setWindowVisible(this, visible);
  }

  public boolean isClosed() {
    return closed;
  }

  public void close() {
    closed = Services.getWindowService().closeWindow(this);
  }

  public abstract Monitor getMonitor();

  public abstract void setMonitor(Monitor monitor);

  public void addCloseEventListener(EventListener<WindowCloseEvent> listener) {
    Objects.requireNonNull(listener);
    frame.defaultLayer().addWindowCloseEventListener(listener);
  }

  public void removeCloseEventListener(EventListener<WindowCloseEvent> listener) {
    Objects.requireNonNull(listener);
    frame.defaultLayer().removeWindowCloseEventListener(listener);
  }

  public List<EventListener<WindowCloseEvent>> getCloseEventListeners() {
    return frame.defaultLayer().getWindowCloseEventListeners();
  }

  public abstract Node getFocusOwner();

  public Frame getFrame() {
    return frame;
  }
}
