package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowCloseEvent;
import lombok.NonNull;

/** Generates {@link WindowCloseEvent} for frame and pushes it to {@link EventProcessor}. */
public class SystemWindowsCloseEventListener
    extends AbstractSystemEventListener<SystemWindowCloseEvent> {

  public SystemWindowsCloseEventListener(@NonNull EventProcessor eventProcessor) {
    super(eventProcessor);
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(SystemWindowCloseEvent event, Frame frame) {
    eventProcessor.push(WindowCloseEvent.builder().source(frame).target(frame).build());
  }
}
