package com.spinyowl.spinygui.core.system;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.NonNull;

/** Used to store and provide window-frame associations. */
public interface FrameWindowService {

  /**
   * Adds window-frame mapping.
   *
   * @param window window.
   * @param frame frame.
   */
  void put(@NonNull Long window, @NonNull Frame frame);

  /**
   * Used to obtain frame by window.
   *
   * @param window window.
   * @return frame if found otherwise null.
   */
  Frame getFrame(@NonNull Long window);

  /**
   * Used to get window by frame.
   *
   * @param frame frame to find window.
   * @return window reference if found otherwise null.
   */
  Long getWindow(@NonNull Frame frame);

  /**
   * Used to remove window-frame mapping.
   *
   * @param window window reference.
   */
  void remove(@NonNull Long window);
}
