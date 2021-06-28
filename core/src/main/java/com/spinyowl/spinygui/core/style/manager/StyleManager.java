package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.node.Frame;

public interface StyleManager {

  /**
   * Adds frame to recalculation frame.
   *
   * @param frame frame to add.
   */
  void needRecalculate(Frame frame);

  /** Recalculates all queued frames. */
  void recalculate();
}
