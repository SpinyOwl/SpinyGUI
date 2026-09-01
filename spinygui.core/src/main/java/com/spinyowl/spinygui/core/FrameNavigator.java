package com.spinyowl.spinygui.core;

import com.spinyowl.spinygui.core.node.Frame;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Owns the current frame and a bounded browser-like navigation history.
 *
 * <p>The navigator retains frames only as navigation entries. Frames remain independent of host
 * services, renderers, and native window resources.
 */
public final class FrameNavigator {

  /** Back-history entries, ordered from oldest at the head to newest at the tail. */
  private final Deque<Frame> backHistory = new ArrayDeque<>();

  /** Forward-history entries, ordered from oldest at the head to next at the tail. */
  private final Deque<Frame> forwardHistory = new ArrayDeque<>();

  /** Maximum number of entries retained in each navigation direction for this navigator's life. */
  private final int historyCapacity;

  /** The non-null frame targeted by the host until the next effective navigation operation. */
  private Frame currentFrame;

  /**
   * Creates a navigator with caller-defined bounded history.
   *
   * @param initialFrame initial non-null current frame
   * @param historyCapacity positive maximum number of retained entries per direction
   * @throws NullPointerException when {@code initialFrame} is null
   * @throws IllegalArgumentException when {@code historyCapacity} is not positive
   */
  public FrameNavigator(Frame initialFrame, int historyCapacity) {
    this.currentFrame = Objects.requireNonNull(initialFrame, "initialFrame");
    if (historyCapacity <= 0) {
      throw new IllegalArgumentException("historyCapacity must be positive");
    }
    this.historyCapacity = historyCapacity;
  }

  /**
   * Returns the frame currently targeted by host work and newly captured input.
   *
   * @return current non-null frame
   */
  public synchronized Frame currentFrame() {
    return currentFrame;
  }

  /**
   * Makes {@code frame} current, records the previous current frame, and clears forward history.
   * Passing the current frame instance is a no-op.
   *
   * @param frame next non-null current frame
   * @throws NullPointerException when {@code frame} is null
   */
  public synchronized void navigate(Frame frame) {
    Frame nextFrame = Objects.requireNonNull(frame, "frame");
    if (nextFrame == currentFrame) {
      return;
    }
    retain(backHistory, currentFrame);
    currentFrame = nextFrame;
    forwardHistory.clear();
  }

  /**
   * Makes the newest back-history entry current when available.
   *
   * @return {@code true} when navigation changed the current frame
   */
  public synchronized boolean back() {
    if (backHistory.isEmpty()) {
      return false;
    }
    retain(forwardHistory, currentFrame);
    currentFrame = backHistory.removeLast();
    return true;
  }

  /**
   * Makes the next forward-history entry current when available.
   *
   * @return {@code true} when navigation changed the current frame
   */
  public synchronized boolean forward() {
    if (forwardHistory.isEmpty()) {
      return false;
    }
    retain(backHistory, currentFrame);
    currentFrame = forwardHistory.removeLast();
    return true;
  }

  /**
   * Reports whether {@link #back()} can change the current frame.
   *
   * @return {@code true} when back history is available
   */
  public synchronized boolean canGoBack() {
    return !backHistory.isEmpty();
  }

  /**
   * Reports whether {@link #forward()} can change the current frame.
   *
   * @return {@code true} when forward history is available
   */
  public synchronized boolean canGoForward() {
    return !forwardHistory.isEmpty();
  }

  /** Retains one entry and evicts the oldest entry when the configured bound is exceeded. */
  private void retain(Deque<Frame> history, Frame frame) {
    history.addLast(frame);
    if (history.size() > historyCapacity) {
      history.removeFirst();
    }
  }
}
