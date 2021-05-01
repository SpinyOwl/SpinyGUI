package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.DropEvent;
import com.spinyowl.spinygui.core.input.Mouse;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemDropEvent;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@SuperBuilder
public class SystemDropEventListener extends AbstractSystemEventListener<SystemDropEvent> {

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
    eventProcessor.push(
        DropEvent.builder().source(frame).target(targetElement).paths(event.paths()).build());
  }
}
