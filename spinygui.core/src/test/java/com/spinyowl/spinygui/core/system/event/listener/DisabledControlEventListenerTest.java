package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.attrs;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.disabled;
import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static com.spinyowl.spinygui.core.node.NodeBuilder.textarea;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemMouseButton;
import com.spinyowl.spinygui.core.time.TimeService;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DisabledControlEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;
  @Mock private MouseService mouseService;
  @Mock private Keyboard keyboard;

  @Test
  void mousePressOnDisabledButtonDoesNotFocusPressOrDispatchEvents() {
    ButtonElement button = button(attrs(disabled()));
    button.box().contentSize(20, 20);
    button.box().contentPosition(20, 20);
    Frame frame = frame(button);
    frame.box().contentSize(100, 100);

    Vector2f cursor = new Vector2f(25, 25);
    when(mouseService.getCursorPositions(frame)).thenReturn(new CursorPositions(cursor, cursor));

    SystemMouseClickEvent event =
        SystemMouseClickEvent.builder()
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .frame(frame)
            .button(SystemMouseButton.LEFT)
            .build();

    var listener =
        SystemMouseClickEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .build();

    listener.process(event, frame);

    verify(mouseService).pressed(event.button().mouseButton(), true);
    verify(mouseService).getCursorPositions(frame);
    verifyNoInteractions(eventProcessor, timeService);
    assertFalse(button.focused());
    assertFalse(button.pressed());
  }

  @Test
  void mouseReleaseOverDisabledButtonClearsPreviouslyPressedControl() {
    ButtonElement disabledButton = button(attrs(disabled()));
    disabledButton.box().contentSize(20, 20);
    disabledButton.box().contentPosition(20, 20);

    ButtonElement focusedButton = button();
    focusedButton.focused(true);
    focusedButton.pressed(true);
    focusedButton.box().contentSize(20, 20);
    focusedButton.box().contentPosition(50, 20);

    Frame frame = frame(disabledButton, focusedButton);
    frame.box().contentSize(100, 100);

    Vector2f cursor = new Vector2f(25, 25);
    when(mouseService.getCursorPositions(frame)).thenReturn(new CursorPositions(cursor, cursor));
    when(timeService.currentTime()).thenReturn(1D);

    SystemMouseClickEvent event =
        SystemMouseClickEvent.builder()
            .action(SystemKeyAction.RELEASE)
            .mods(ImmutableSet.of())
            .frame(frame)
            .button(SystemMouseButton.LEFT)
            .build();

    var listener =
        SystemMouseClickEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .build();

    listener.process(event, frame);

    assertFalse(disabledButton.pressed());
    assertFalse(focusedButton.pressed());
    verify(eventProcessor).push(any(MouseClickEvent.class));
  }

  @Test
  void keyPressOnFocusedDisabledButtonDoesNotActivateOrDispatchKeyboardEvent() {
    ButtonElement button = button(attrs(disabled()));
    button.focused(true);
    button.pressed(true);
    Frame frame = frame(button);

    SystemKeyEvent event =
        SystemKeyEvent.builder()
            .keyCode(13)
            .scancode(13)
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .frame(frame)
            .build();

    var listener =
        SystemKeyEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .keyboard(keyboard)
            .build();

    listener.process(event, frame);

    verifyNoInteractions(eventProcessor, timeService, keyboard);
    assertFalse(button.pressed());
  }

  @Test
  void charInputOnFocusedDisabledTextareaDoesNotEditOrDispatchCharEvent() {
    TextareaElement textarea = textarea(attrs(disabled()), "ac");
    textarea.caretIndex(1);
    textarea.focused(true);
    Frame frame = frame(textarea);

    SystemCharEvent event = SystemCharEvent.builder().frame(frame).codepoint('b').build();

    var listener =
        SystemCharEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build();

    listener.process(event, frame);

    assertEquals("ac", textarea.value());
    assertEquals(1, textarea.caretIndex());
    verifyNoInteractions(eventProcessor, timeService);
  }
}
