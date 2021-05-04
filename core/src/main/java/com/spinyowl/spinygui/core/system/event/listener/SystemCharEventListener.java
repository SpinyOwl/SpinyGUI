package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.CharEvent;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.util.TextUtil;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
public class SystemCharEventListener extends AbstractSystemEventListener<SystemCharEvent> {

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemCharEvent event, @NonNull Frame frame) {
    var focusedElement = frame.getFocusedElement();
    if (focusedElement == null) {
      return;
    }

    eventProcessor.push(
        CharEvent.builder()
            .source(frame)
            .target(focusedElement)
            .timestamp(timeService.getCurrentTime())
            .input(TextUtil.cpToStr(event.codepoint()))
            .build());
  }
}
