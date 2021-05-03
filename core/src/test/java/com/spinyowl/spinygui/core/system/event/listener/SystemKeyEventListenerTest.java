package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.KeyAction.PRESS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.KeyboardEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import com.spinyowl.spinygui.core.input.KeyboardLayout;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemKeyEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;
  @Mock private Keyboard keyboard;
  @Mock private KeyboardLayout keyboardLayout;

  private SystemEventListener<SystemKeyEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemKeyEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .keyboard(keyboard)
            .build();

    when(keyboard.layout()).thenReturn(keyboardLayout);
  }

  @Test
  void process_generatesKeyboardEvent() {
    // Arrange
    double timestamp = 1D;
    when(timeService.getCurrentTime()).thenReturn(timestamp);

    int keyCode = 7;
    KeyCode keyCodeObject = KeyCode.KEY_7;
    int scancode = 7;

    SystemKeyEvent event =
        SystemKeyEvent.builder()
            .keyCode(keyCode)
            .scancode(scancode)
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .window(1)
            .build();

    when(keyboardLayout.keyCode(keyCode)).thenReturn(keyCodeObject);

    Frame frame = new Frame();
    Element element = new Element("div");
    frame.addChild(element);

    element.focused(true);

    KeyboardEvent expectedEvent =
        KeyboardEvent.builder()
            .source(frame)
            .target(element)
            .action(PRESS)
            .timestamp(timestamp)
            .mods(ImmutableSet.of())
            .key(new KeyboardKey(keyCodeObject, keyCode, scancode))
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(keyboard).layout();
    verify(keyboardLayout).keyCode(keyCode);
    verify(timeService).getCurrentTime();
    verify(eventProcessor).push(expectedEvent);
  }
}
