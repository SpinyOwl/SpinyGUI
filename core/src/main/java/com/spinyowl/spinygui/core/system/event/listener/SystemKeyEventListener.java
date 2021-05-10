package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.KeyboardEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class SystemKeyEventListener extends AbstractSystemEventListener<SystemKeyEvent> {

  @NonNull private final Keyboard keyboard;

  @Builder
  public SystemKeyEventListener(
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      @NonNull Keyboard keyboard) {
    super(eventProcessor, timeService);
    this.keyboard = keyboard;
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemKeyEvent event, @NonNull Frame frame) {
    var element = frame.getFocusedElement();
    if (element != null) {

      int keyCode = event.keyCode();
      var key = new KeyboardKey(keyboard.layout().keyCode(keyCode), keyCode, event.scancode());

      eventProcessor.push(
          KeyboardEvent.builder()
              .source(frame)
              .target(element)
              .key(key)
              .timestamp(timeService.getCurrentTime())
              .mods(event.mappedMods())
              .action(getAction(event))
              .build());
    }
  }

  private KeyAction getAction(SystemKeyEvent event) {
    return switch (event.action()) {
      case PRESS -> KeyAction.PRESS;
      case RELEASE -> KeyAction.RELEASE;
      case REPEAT -> KeyAction.REPEAT;
    };
  }
}
