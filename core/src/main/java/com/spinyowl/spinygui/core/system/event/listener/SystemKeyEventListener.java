package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.KeyboardEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import lombok.NonNull;

public class SystemKeyEventListener extends AbstractSystemEventListener<SystemKeyEvent> {

  private final Keyboard keyboard;

  public SystemKeyEventListener(
      @NonNull EventProcessor eventProcessor, @NonNull Keyboard keyboard) {
    super(eventProcessor);
    this.keyboard = keyboard;
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(SystemKeyEvent event, Frame frame) {
    int keyCode = event.keyCode();
    var key = new KeyboardKey(keyboard.layout().keyCode(keyCode), keyCode, event.scancode());

    var element = frame.getFocusedElement();
    if (element != null) {
      eventProcessor.push(
          KeyboardEvent.builder()
              .source(frame)
              .target(element)
              .key(key)
              .mods(event.mods())
              .action(event.action())
              .build());
    }
  }
}
