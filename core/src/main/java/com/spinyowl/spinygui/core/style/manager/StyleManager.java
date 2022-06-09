package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.node.Frame;

public interface StyleManager {

  /** Recalculates all styles for provided frame and its children. */
  void recalculate(Frame frame);
}
