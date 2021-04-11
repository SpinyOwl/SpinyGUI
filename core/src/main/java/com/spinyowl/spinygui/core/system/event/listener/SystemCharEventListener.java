package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.InputEvent;
import com.spinyowl.spinygui.core.event.InputEvent.InputType;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.util.TextUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SystemCharEventListener implements SystemEventListener<SystemCharEvent> {

  /** Event processor to pass generated events to. */
  @NonNull private final EventProcessor eventProcessor;

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

    eventProcessor.pushEvent(
        InputEvent.builder()
            .inputType(InputType.INSERT_TEXT)
            .data(TextUtil.cpToStr(event.codepoint()))
            .target(focusedElement)
            .source(frame)
            .build());
  }
}
