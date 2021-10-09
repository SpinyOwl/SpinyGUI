package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.FocusEvent.FocusInEvent;
import com.spinyowl.spinygui.core.event.FocusEvent.FocusOutEvent;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.MouseButton;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemMouseButton;
import com.spinyowl.spinygui.core.time.TimeService;
import org.joml.Vector2f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemMouseClickEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;
  @Mock private MouseService mouseService;

  private SystemEventListener<SystemMouseClickEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemMouseClickEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .build();
  }

  @Test
  void process_pressOutCurrentFrame_generatesReleaseEventForFocusedElement() {
    // Arrange
    Element element = div();
    element.focused(true);
    element.dimensions().contentSize(20, 20);
    element.dimensions().contentPosition(20, 20);

    Frame frame = frame(element);
    frame.dimensions().contentSize(100, 100);

    SystemMouseClickEvent event =
        SystemMouseClickEvent.builder()
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .frame(frame)
            .button(SystemMouseButton.LEFT)
            .build();

    doNothing().when(mouseService).pressed(event.button().mouseButton(), true);

    Vector2f current = new Vector2f(-25, -25); // click out of frame (for example in other frame)
    CursorPositions cursorPositions = new CursorPositions(current, current);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    double timestamp = 1;
    when(timeService.getCurrentTime()).thenReturn(timestamp);

    MouseClickEvent expectedReleaseEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(element)
            .action(KeyAction.RELEASE)
            .mouseButton(MouseButton.LEFT)
            .position(new Vector2f(element.dimensions().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(ImmutableSet.of())
            .timestamp(timestamp)
            .build();
    doNothing().when(eventProcessor).push(expectedReleaseEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).pressed(event.button().mouseButton(), true);
    verify(mouseService).getCursorPositions(frame);

    verify(timeService).getCurrentTime();

    assertFalse(element.focused());
  }

  @Test
  void process_pressInCurrentFrame_generatesReleaseEventForFocusedElement() {
    // Arrange
    Element newFocusedElement = div(); // will gain focus
    newFocusedElement.dimensions().contentSize(20, 20);
    newFocusedElement.dimensions().contentPosition(20, 20);

    Element oldFocusedElement = div(); // will lose focus
    oldFocusedElement.focused(true);
    oldFocusedElement.dimensions().contentSize(20, 20);
    oldFocusedElement.dimensions().contentPosition(50, 20);

    Frame frame = frame(oldFocusedElement, newFocusedElement);
    frame.dimensions().contentSize(100, 100);

    SystemMouseClickEvent event =
        SystemMouseClickEvent.builder()
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .frame(frame)
            .button(SystemMouseButton.LEFT)
            .build();

    doNothing().when(mouseService).pressed(event.button().mouseButton(), true);

    Vector2f current = new Vector2f(25, 25); // click in frame
    CursorPositions cursorPositions = new CursorPositions(current, current);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    double timestamp = 1;
    when(timeService.getCurrentTime()).thenReturn(timestamp);

    FocusOutEvent expectedFocusLostEvent =
        new FocusOutEvent(frame, oldFocusedElement, timestamp, newFocusedElement);
    doNothing().when(eventProcessor).push(expectedFocusLostEvent);

    MouseClickEvent expectedPressEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(newFocusedElement)
            .action(KeyAction.PRESS)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(
                new Vector2f(newFocusedElement.dimensions().contentPosition())
                    .sub(current)
                    .negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();
    doNothing().when(eventProcessor).push(expectedPressEvent);

    FocusInEvent expectedFocusGainedEvent =
        new FocusInEvent(frame, newFocusedElement, timestamp, oldFocusedElement);
    doNothing().when(eventProcessor).push(expectedFocusGainedEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).pressed(event.button().mouseButton(), true);
    verify(mouseService).getCursorPositions(frame);

    verify(timeService, times(3)).getCurrentTime();

    verify(eventProcessor).push(expectedFocusLostEvent);
    verify(eventProcessor).push(expectedPressEvent);
    verify(eventProcessor).push(expectedFocusGainedEvent);

    assertFalse(oldFocusedElement.focused());
    assertFalse(oldFocusedElement.pressed());

    assertTrue(newFocusedElement.focused());
    assertTrue(newFocusedElement.pressed());
  }

  @Test
  void process_releaseInCurrentFrame_generatesReleaseEventForFocusedElement() {
    // Arrange
    Element otherElement = div();
    otherElement.dimensions().contentSize(20, 20);
    otherElement.dimensions().contentPosition(20, 20);

    Element focusedElement = div(); // will lose focus
    focusedElement.focused(true);
    focusedElement.dimensions().contentSize(20, 20);
    focusedElement.dimensions().contentPosition(50, 20);

    Frame frame = frame(focusedElement, otherElement);
    frame.dimensions().contentSize(100, 100);

    SystemMouseClickEvent event =
        SystemMouseClickEvent.builder()
            .action(SystemKeyAction.RELEASE)
            .mods(ImmutableSet.of())
            .frame(frame)
            .button(SystemMouseButton.LEFT)
            .build();

    doNothing().when(mouseService).pressed(event.button().mouseButton(), false);

    Vector2f current = new Vector2f(25, 25); // click in frame
    CursorPositions cursorPositions = new CursorPositions(current, current);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    double timestamp = 1;
    when(timeService.getCurrentTime()).thenReturn(timestamp);

    MouseClickEvent expectedReleaseEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(focusedElement)
            .action(KeyAction.RELEASE)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(
                new Vector2f(focusedElement.dimensions().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();
    doNothing().when(eventProcessor).push(expectedReleaseEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).pressed(event.button().mouseButton(), false);
    verify(mouseService).getCursorPositions(frame);

    verify(timeService, times(1)).getCurrentTime();

    verify(eventProcessor).push(expectedReleaseEvent);

    assertTrue(focusedElement.focused());
    assertFalse(focusedElement.pressed());

    assertFalse(otherElement.focused());
    assertFalse(otherElement.pressed());
  }

  @Test
  void process_releaseInCurrentFrame_generatesClickAndReleaseEventForFocusedElement() {
    // Arrange
    Element focusedElement = div(); // will lose focus
    focusedElement.focused(true);
    focusedElement.dimensions().contentSize(20, 20);
    focusedElement.dimensions().contentPosition(50, 20);

    Frame frame = frame(focusedElement);
    frame.dimensions().contentSize(100, 100);

    SystemMouseClickEvent event =
        SystemMouseClickEvent.builder()
            .action(SystemKeyAction.RELEASE)
            .mods(ImmutableSet.of())
            .frame(frame)
            .button(SystemMouseButton.LEFT)
            .build();

    doNothing().when(mouseService).pressed(event.button().mouseButton(), false);

    Vector2f current = new Vector2f(55, 25); // click in frame
    CursorPositions cursorPositions = new CursorPositions(current, current);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    double timestamp = 1;
    when(timeService.getCurrentTime()).thenReturn(timestamp);

    MouseClickEvent expectedClickEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(focusedElement)
            .action(KeyAction.CLICK)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(
                new Vector2f(focusedElement.dimensions().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();
    doNothing().when(eventProcessor).push(expectedClickEvent);

    MouseClickEvent expectedReleaseEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(focusedElement)
            .action(KeyAction.RELEASE)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(
                new Vector2f(focusedElement.dimensions().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();
    doNothing().when(eventProcessor).push(expectedReleaseEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).pressed(event.button().mouseButton(), false);
    verify(mouseService).getCursorPositions(frame);

    verify(timeService, times(2)).getCurrentTime();

    verify(eventProcessor).push(expectedClickEvent);
    verify(eventProcessor).push(expectedReleaseEvent);

    assertTrue(focusedElement.focused());
    assertFalse(focusedElement.pressed());
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    SystemMouseClickEvent event =
        SystemMouseClickEvent.builder()
            .action(SystemKeyAction.RELEASE)
            .mods(ImmutableSet.of())
            .frame(frame())
            .button(SystemMouseButton.LEFT)
            .build();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }
}
