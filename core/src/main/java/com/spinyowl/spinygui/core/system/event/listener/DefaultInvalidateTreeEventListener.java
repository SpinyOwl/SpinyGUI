package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.style.manager.StyleManager;
import com.spinyowl.spinygui.core.system.event.InvalidateTreeEvent;
import lombok.Builder;
import lombok.NonNull;

@Builder
public final class DefaultInvalidateTreeEventListener
    implements SystemEventListener<InvalidateTreeEvent> {

  @NonNull private final StyleManager styleManager;

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull InvalidateTreeEvent event, @NonNull Frame frame) {
    styleManager.needRecalculate(frame);
  }
}
