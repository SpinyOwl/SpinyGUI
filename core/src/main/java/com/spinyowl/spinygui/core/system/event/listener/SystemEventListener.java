package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemEvent;

/**
 * Used to listen, process and translate system event to gui event.
 *
 * @param <E> type of system event.
 */
public interface SystemEventListener<E extends SystemEvent> {

  /**
   * Used to listen, process and translate system event to gui event.
   * @param event system event to process
   * @param frame target frame for system event.
   */
  void process(E event, Frame frame);
}
