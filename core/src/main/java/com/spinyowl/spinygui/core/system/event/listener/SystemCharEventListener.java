package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.CharEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.util.TextUtil;
import lombok.NonNull;

public class SystemCharEventListener extends AbstractSystemEventListener<SystemCharEvent> {

  public SystemCharEventListener(@NonNull EventProcessor eventProcessor) {
    super(eventProcessor);
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(SystemCharEvent event, Frame frame) {
    Element focusedElement = frame.getFocusedElement();
    if (focusedElement == null) {
      return;
    }

    eventProcessor.push(
        CharEvent.builder()
            .source(frame)
            .target(focusedElement)
            .input(TextUtil.cpToStr(event.codepoint()))
            .build());
  }
}
