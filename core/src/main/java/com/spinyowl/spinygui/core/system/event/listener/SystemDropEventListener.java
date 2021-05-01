package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.DropEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.Mouse;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemDropEvent;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.NonNull;
import org.joml.Vector2fc;

public class SystemDropEventListener extends AbstractSystemEventListener<SystemDropEvent> {

  public SystemDropEventListener(@NonNull EventProcessor eventProcessor) {
    super(eventProcessor);
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(SystemDropEvent event, Frame frame) {
    Vector2fc currentMousePosition = Mouse.getCursorPositions(frame).current();
    var targetElement = NodeUtilities.getTargetElement(frame, currentMousePosition);
    eventProcessor.pushEvent(
        DropEvent.builder().target(targetElement).source(frame).paths(event.paths()).build());
  }
}
