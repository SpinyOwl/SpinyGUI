package com.spinyowl.spinygui.core.system;

import com.spinyowl.spinygui.core.node.Frame;
import java.util.Set;
import lombok.NonNull;

/** Used to store and provide window-frame associations. */
public interface FrameWindowService {

  void put(@NonNull Long window, @NonNull Frame frame);

  Frame getFrame(@NonNull Long window);

  Set<Long> getWindows(@NonNull Frame frame);

  void remove(@NonNull Long window);
}
