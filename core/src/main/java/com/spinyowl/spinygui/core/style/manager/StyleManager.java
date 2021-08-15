package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.node.Frame;

public interface StyleManager {

  /** Recalculates all queued frames. */
  void recalculate(Frame frame);
}
