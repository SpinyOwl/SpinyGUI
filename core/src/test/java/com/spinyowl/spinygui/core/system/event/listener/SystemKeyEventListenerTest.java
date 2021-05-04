package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.KeyAction.PRESS;
import static com.spinyowl.spinygui.core.input.KeyAction.RELEASE;
import static com.spinyowl.spinygui.core.input.KeyAction.REPEAT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.KeyboardEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import com.spinyowl.spinygui.core.input.KeyboardLayout;
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

  private SystemEventListener<SystemKeyEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemKeyEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .keyboard(keyboard)
            .build();
  }

  @Test
  void process_pressGeneratesKeyboardEvent() {
    test(SystemKeyAction.PRESS, PRESS);
  }

  @Test
  void process_repeatGeneratesKeyboardEvent() {
    test(SystemKeyAction.REPEAT, REPEAT);
  }

  @Test
  void process_releaseGeneratesKeyboardEvent() {
    test(SystemKeyAction.RELEASE, RELEASE);
  }

  @Test
  void process_doNothingIfNoFocusedElement() {
    // Arrange
    var frame = frame(div());
    SystemKeyEvent systemEvent =
        SystemKeyEvent.builder()
            .keyCode(7)
            .scancode(7)
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .window(1)
            .build();

    // Act
    listener.process(systemEvent, frame);

    // Verify
    verifyNoInteractions(eventProcessor);
    verifyNoInteractions(timeService);
    verifyNoInteractions(keyboard);
  }

  private void test(SystemKeyAction systemAction, KeyAction action) {
    // Arrange
    double timestamp = 1D;
    when(timeService.getCurrentTime()).thenReturn(timestamp);
    KeyboardLayout keyboardLayout = mock(KeyboardLayout.class);
    when(keyboard.layout()).thenReturn(keyboardLayout);

    int keyCode = 7;
    KeyCode keyCodeObject = KeyCode.KEY_7;
    int scancode = 7;

    SystemKeyEvent event =
        SystemKeyEvent.builder()
            .keyCode(keyCode)
            .scancode(scancode)
            .action(systemAction)
            .mods(ImmutableSet.of())
            .window(1)
            .build();

    when(keyboardLayout.keyCode(keyCode)).thenReturn(keyCodeObject);

    var element = div();
    var frame = frame(element);

    element.focused(true);

    KeyboardEvent expectedEvent =
        KeyboardEvent.builder()
            .source(frame)
            .target(element)
            .action(action)
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
