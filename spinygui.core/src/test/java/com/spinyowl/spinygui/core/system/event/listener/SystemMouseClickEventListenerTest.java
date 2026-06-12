package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.FocusInEvent;
import com.spinyowl.spinygui.core.event.FocusOutEvent;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.MouseButton;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import com.spinyowl.spinygui.core.system.input.SystemMouseButton;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Set;
import org.joml.Vector2f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    element.box().contentSize(20, 20);
    element.box().contentPosition(20, 20);

    Frame frame = frame(element);
    frame.box().contentSize(100, 100);

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
    when(timeService.currentTime()).thenReturn(timestamp);

    MouseClickEvent expectedReleaseEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(element)
            .action(KeyAction.RELEASE)
            .mouseButton(MouseButton.LEFT)
            .position(new Vector2f(element.box().contentPosition()).sub(current).negate())
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

    verify(timeService).currentTime();

    assertFalse(element.focused());
  }

  @Test
  void process_pressInCurrentFrame_generatesReleaseEventForFocusedElement() {
    // Arrange
    Element newFocusedElement = div(); // will gain focus
    newFocusedElement.box().contentSize(20, 20);
    newFocusedElement.box().contentPosition(20, 20);

    Element oldFocusedElement = div(); // will lose focus
    oldFocusedElement.focused(true);
    oldFocusedElement.box().contentSize(20, 20);
    oldFocusedElement.box().contentPosition(50, 20);

    Frame frame = frame(oldFocusedElement, newFocusedElement);
    frame.box().contentSize(100, 100);

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
    when(timeService.currentTime()).thenReturn(timestamp);

    FocusOutEvent expectedFocusLostEvent =
        FocusOutEvent.builder()
            .source(frame)
            .target(oldFocusedElement)
            .timestamp(timestamp)
            .nextFocus(newFocusedElement)
            .build();
    doNothing().when(eventProcessor).push(expectedFocusLostEvent);

    MouseClickEvent expectedPressEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(newFocusedElement)
            .action(KeyAction.PRESS)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(new Vector2f(newFocusedElement.box().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();
    doNothing().when(eventProcessor).push(expectedPressEvent);

    FocusInEvent expectedFocusGainedEvent =
        FocusInEvent.builder()
            .source(frame)
            .target(newFocusedElement)
            .timestamp(timestamp)
            .prevFocus(oldFocusedElement)
            .build();
    doNothing().when(eventProcessor).push(expectedFocusGainedEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).pressed(event.button().mouseButton(), true);
    verify(mouseService).getCursorPositions(frame);

    verify(timeService, times(3)).currentTime();

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
    otherElement.box().contentSize(20, 20);
    otherElement.box().contentPosition(20, 20);

    Element focusedElement = div(); // will lose focus
    focusedElement.focused(true);
    focusedElement.box().contentSize(20, 20);
    focusedElement.box().contentPosition(50, 20);

    Frame frame = frame(focusedElement, otherElement);
    frame.box().contentSize(100, 100);

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
    when(timeService.currentTime()).thenReturn(timestamp);

    MouseClickEvent expectedReleaseEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(focusedElement)
            .action(KeyAction.RELEASE)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(new Vector2f(focusedElement.box().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();
    doNothing().when(eventProcessor).push(expectedReleaseEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).pressed(event.button().mouseButton(), false);
    verify(mouseService).getCursorPositions(frame);

    verify(timeService, times(1)).currentTime();

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
    focusedElement.box().contentSize(20, 20);
    focusedElement.box().contentPosition(50, 20);

    Frame frame = frame(focusedElement);
    frame.box().contentSize(100, 100);

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
    when(timeService.currentTime()).thenReturn(timestamp);

    MouseClickEvent expectedClickEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(focusedElement)
            .action(KeyAction.CLICK)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(new Vector2f(focusedElement.box().contentPosition()).sub(current).negate())
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
            .position(new Vector2f(focusedElement.box().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();
    doNothing().when(eventProcessor).push(expectedReleaseEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).pressed(event.button().mouseButton(), false);
    verify(mouseService).getCursorPositions(frame);

    verify(timeService, times(2)).currentTime();

    verify(eventProcessor).push(expectedClickEvent);
    verify(eventProcessor).push(expectedReleaseEvent);

    assertTrue(focusedElement.focused());
    assertFalse(focusedElement.pressed());
  }

  @Test
  void process_releaseFocusedButtonInCurrentFrame_generatesActionEventWithClickAndRelease() {
    ButtonElement button = new ButtonElement();
    button.focused(true);
    button.pressed(true);
    button.box().contentSize(20, 20);
    button.box().contentPosition(50, 20);
    Frame frame = frame(button);
    frame.box().contentSize(100, 100);

    SystemMouseClickEvent event = mouseRelease(frame);
    Vector2f current = new Vector2f(55, 25);
    when(mouseService.getCursorPositions(frame))
        .thenReturn(new CursorPositions(current, current));
    double timestamp = 1;
    when(timeService.currentTime()).thenReturn(timestamp);

    MouseClickEvent expectedClickEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(button)
            .action(KeyAction.CLICK)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(new Vector2f(button.box().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();
    ActionEvent expectedActionEvent =
        ActionEvent.builder().source(frame).target(button).timestamp(timestamp).build();
    MouseClickEvent expectedReleaseEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(button)
            .action(KeyAction.RELEASE)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(new Vector2f(button.box().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();

    listener.process(event, frame);

    verify(eventProcessor).push(expectedClickEvent);
    verify(eventProcessor).push(expectedActionEvent);
    verify(eventProcessor).push(expectedReleaseEvent);
    assertTrue(button.focused());
    assertFalse(button.pressed());
  }

  @Test
  void process_releaseFocusedButtonInputInCurrentFrame_generatesActionEventWithClickAndRelease() {
    InputElement input = new InputElement();
    input.type(TYPE_BUTTON);
    input.value("Save");
    input.focused(true);
    input.pressed(true);
    input.box().contentSize(20, 20);
    input.box().contentPosition(50, 20);
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);

    SystemMouseClickEvent event = mouseRelease(frame);
    Vector2f current = new Vector2f(55, 25);
    when(mouseService.getCursorPositions(frame))
        .thenReturn(new CursorPositions(current, current));
    double timestamp = 1;
    when(timeService.currentTime()).thenReturn(timestamp);

    MouseClickEvent expectedClickEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(input)
            .action(KeyAction.CLICK)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(new Vector2f(input.box().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();
    ActionEvent expectedActionEvent =
        ActionEvent.builder().source(frame).target(input).timestamp(timestamp).build();
    MouseClickEvent expectedReleaseEvent =
        MouseClickEvent.builder()
            .source(frame)
            .target(input)
            .action(KeyAction.RELEASE)
            .timestamp(timestamp)
            .mouseButton(MouseButton.LEFT)
            .position(new Vector2f(input.box().contentPosition()).sub(current).negate())
            .absolutePosition(current)
            .mods(event.mappedMods())
            .build();

    listener.process(event, frame);

    verify(eventProcessor).push(expectedClickEvent);
    verify(eventProcessor).push(expectedActionEvent);
    verify(eventProcessor).push(expectedReleaseEvent);
    assertTrue(input.focused());
    assertFalse(input.pressed());
    assertEquals("Save", input.value());
  }

  @Test
  void process_pressAndReleaseButtonInputInCurrentFrame_generatesActionEvent() {
    InputElement input = new InputElement();
    input.type(TYPE_BUTTON);
    input.value("Save");
    input.box().contentSize(20, 20);
    input.box().contentPosition(50, 20);
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);

    Vector2f current = new Vector2f(55, 25);
    when(mouseService.getCursorPositions(frame))
        .thenReturn(new CursorPositions(current, current));
    double timestamp = 1;
    when(timeService.currentTime()).thenReturn(timestamp);

    listener.process(mousePress(frame), frame);
    listener.process(mouseRelease(frame), frame);

    ActionEvent expectedActionEvent =
        ActionEvent.builder().source(frame).target(input).timestamp(timestamp).build();
    verify(eventProcessor).push(expectedActionEvent);
    assertTrue(input.focused());
    assertFalse(input.pressed());
    assertEquals("Save", input.value());
  }

  @Test
  void process_pressButtonInputWithTextMeasurer_doesNotPlaceCaretOrExtendSelection() {
    InputElement input = new InputElement();
    input.type(TYPE_BUTTON);
    input.value("Save");
    input.caretIndex(2);
    input.box().contentPosition(20, 20);
    input.box().contentSize(40, 20);
    input.box().padding().left(5);
    input.box().padding().right(5);
    input.box().border().left(2);
    input.box().border().right(2);
    input.resolvedStyle().fontFamilies(Set.of(Font.DEFAULT.fontFamily()));
    input.resolvedStyle().fontStyle(FontStyle.NORMAL);
    input.resolvedStyle().fontWeight(FontWeight.REGULAR);
    input.resolvedStyle().fontSize(Length.pixel(16));
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);
    listener = listenerWithTextMeasurer();

    processInputPress(frame, new Vector2f(55, 25), ImmutableSet.of(SystemKeyMod.SHIFT));

    Assertions.assertEquals(2, input.caretIndex());
    Assertions.assertEquals(2, input.selectionAnchor());
    assertTrue(input.focused());
    assertTrue(input.pressed());
  }

  @Test
  void process_clickButtonNestedElement_generatesActionEventForButton() {
    ButtonElement button = new ButtonElement();
    button.box().contentPosition(50, 20);
    button.box().contentSize(80, 40);
    Element nested = div();
    nested.offsetParent(button);
    nested.box().contentPosition(5, 5);
    nested.box().contentSize(50, 20);
    button.addChild(nested);
    Frame frame = frame(button);
    frame.box().contentSize(160, 100);

    Vector2f current = new Vector2f(60, 30);
    when(mouseService.getCursorPositions(frame))
        .thenReturn(new CursorPositions(current, current));
    double timestamp = 1;
    when(timeService.currentTime()).thenReturn(timestamp);

    listener.process(mousePress(frame), frame);
    listener.process(mouseRelease(frame), frame);

    ActionEvent expectedActionEvent =
        ActionEvent.builder().source(frame).target(button).timestamp(timestamp).build();
    verify(eventProcessor).push(expectedActionEvent);
    assertTrue(button.focused());
    assertFalse(button.pressed());
    assertFalse(nested.focused());
    assertFalse(nested.pressed());
  }

  @Test
  void process_pressTextInputAtStart_setsCaretToStartAndKeepsMouseEvents() {
    InputElement input = textInput();
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);
    listener = listenerWithTextMeasurer();

    processInputPress(frame, new Vector2f(15, 25));

    Assertions.assertEquals(0, input.caretIndex());
    assertTrue(input.focused());
    assertTrue(input.pressed());
    verify(eventProcessor, times(2)).push(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void process_pressTextInputAtMiddle_setsCaretUnderPointer() {
    InputElement input = textInput();
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);
    listener = listenerWithTextMeasurer();

    processInputPress(frame, new Vector2f(35, 25));

    Assertions.assertEquals(2, input.caretIndex());
  }

  @Test
  void process_pressTextInputAtEnd_setsCaretToEnd() {
    InputElement input = textInput();
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);
    listener = listenerWithTextMeasurer();

    processInputPress(frame, new Vector2f(65, 25));

    Assertions.assertEquals(4, input.caretIndex());
  }

  @Test
  void process_pressTextInputInRightPadding_clampsCaretOffsetToContentEnd() {
    InputElement input = textInput();
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);
    listener = listenerWithTextMeasurer();

    processInputPress(frame, new Vector2f(66, 25));

    Assertions.assertEquals(4, input.caretIndex());
  }

  @Test
  void process_pressTextInputWithHorizontalScroll_placesCaretInScrolledText() {
    InputElement input = textInput();
    input.value("abcdef");
    input.textScrollLeft(20);
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);
    listener = listenerWithTextMeasurer();

    processInputPress(frame, new Vector2f(35, 25));

    Assertions.assertEquals(4, input.caretIndex());
  }

  @Test
  void process_shiftPressTextInput_extendsSelectionFromAnchor() {
    InputElement input = textInput();
    input.caretIndex(1);
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);
    listener = listenerWithTextMeasurer();

    processInputPress(frame, new Vector2f(55, 25), ImmutableSet.of(SystemKeyMod.SHIFT));

    Assertions.assertEquals(1, input.selectionStart());
    Assertions.assertEquals(4, input.selectionEnd());
    Assertions.assertEquals(4, input.caretIndex());
  }

  @Test
  void process_shiftPressTextarea_extendsSelectionFromAnchor() {
    TextareaElement textarea = textarea();
    textarea.caretIndex(1);
    Frame frame = frame(textarea);
    frame.box().contentSize(100, 100);
    listener = listenerWithTextMeasurer();

    processInputPress(frame, new Vector2f(55, 45), ImmutableSet.of(SystemKeyMod.SHIFT));

    Assertions.assertEquals(1, textarea.selectionStart());
    Assertions.assertEquals(5, textarea.selectionEnd());
    Assertions.assertEquals(5, textarea.caretIndex());
  }

  @Test
  void process_pressNestedTextInput_placesCaretFromAbsoluteContentPosition() {
    Element panel = div();
    panel.box().contentPosition(100, 20);
    panel.box().contentSize(200, 100);
    InputElement input = textInput();
    input.offsetParent(panel);
    panel.addChild(input);
    Frame frame = frame(panel);
    frame.box().contentSize(400, 200);
    frame.layoutChildNodes(java.util.List.of(panel));
    panel.layoutChildNodes(java.util.List.of(input));
    listener = listenerWithTextMeasurer();

    processInputPress(frame, new Vector2f(135, 45));

    Assertions.assertEquals(2, input.caretIndex());
    assertTrue(input.focused());
  }

  @Test
  void process_pressVerticalScrollbarTrack_scrollsByClientPageAndEmitsScrollEvent() {
    Element element = scrollableElement(100, 100, 100, 300);
    element.resolvedStyle().overflowX(Overflow.HIDDEN);
    Frame frame = frame(element);
    frame.box().contentSize(200, 200);
    Vector2f current = new Vector2f(99, 80);

    when(mouseService.getCursorPositions(frame)).thenReturn(new CursorPositions(current, current));
    when(timeService.currentTime()).thenReturn(1D);

    listener.process(mousePress(frame), frame);

    assertEquals(100, element.scrollTop(), 0.0001f);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(eventProcessor).push(eventCaptor.capture());
    ScrollEvent event = assertInstanceOf(ScrollEvent.class, eventCaptor.getValue());
    assertEquals(element, event.target());
    assertEquals(0, event.offsetX(), 0.0001f);
    assertEquals(100, event.offsetY(), 0.0001f);
  }

  @Test
  void process_pressHorizontalScrollbarTrack_scrollsByClientPageAndEmitsScrollEvent() {
    Element element = scrollableElement(100, 100, 300, 100);
    element.resolvedStyle().overflowY(Overflow.HIDDEN);
    Frame frame = frame(element);
    frame.box().contentSize(200, 200);
    Vector2f current = new Vector2f(80, 99);

    when(mouseService.getCursorPositions(frame)).thenReturn(new CursorPositions(current, current));
    when(timeService.currentTime()).thenReturn(1D);

    listener.process(mousePress(frame), frame);

    assertEquals(100, element.scrollLeft(), 0.0001f);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(eventProcessor).push(eventCaptor.capture());
    ScrollEvent event = assertInstanceOf(ScrollEvent.class, eventCaptor.getValue());
    assertEquals(element, event.target());
    assertEquals(100, event.offsetX(), 0.0001f);
    assertEquals(0, event.offsetY(), 0.0001f);
  }

  @Test
  void process_pressInsideScrollableContent_keepsNormalContentClickTargeting() {
    Element element = scrollableElement(100, 100, 100, 300);
    element.resolvedStyle().overflowX(Overflow.HIDDEN);
    Frame frame = frame(element);
    frame.box().contentSize(200, 200);
    Vector2f current = new Vector2f(20, 20);

    when(mouseService.getCursorPositions(frame)).thenReturn(new CursorPositions(current, current));
    when(timeService.currentTime()).thenReturn(1D);

    listener.process(mousePress(frame), frame);

    assertTrue(element.focused());
    assertTrue(element.pressed());

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(eventProcessor, times(2)).push(eventCaptor.capture());
    assertTrue(
        eventCaptor.getAllValues().stream()
            .filter(MouseClickEvent.class::isInstance)
            .map(MouseClickEvent.class::cast)
            .anyMatch(
                event ->
                    event.target() == element
                        && KeyAction.PRESS.equals(event.action())
                        && event.absolutePosition().equals(current)));
  }

  @Test
  void process_releaseAfterScrollbarThumbPress_endsActiveDrag() {
    ScrollbarInteraction scrollbarInteraction = new ScrollbarInteraction();
    listener =
        SystemMouseClickEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .scrollbarInteraction(scrollbarInteraction)
            .build();
    Element element = scrollableElement(100, 100, 100, 300);
    element.resolvedStyle().overflowX(Overflow.HIDDEN);
    Frame frame = frame(element);
    frame.box().contentSize(200, 200);
    Vector2f current = new Vector2f(99, 10);

    when(mouseService.getCursorPositions(frame)).thenReturn(new CursorPositions(current, current));

    listener.process(mousePress(frame), frame);
    assertTrue(scrollbarInteraction.dragging());

    listener.process(mouseRelease(frame), frame);

    assertFalse(scrollbarInteraction.dragging());
    verify(eventProcessor, times(0)).push(any());
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

  private SystemEventListener<SystemMouseClickEvent> listenerWithTextMeasurer() {
    return SystemMouseClickEventListener.builder()
        .eventProcessor(eventProcessor)
        .timeService(timeService)
        .mouseService(mouseService)
        .textMeasurer(new FixedWidthTextMeasurer())
        .build();
  }

  private InputElement textInput() {
    InputElement input = new InputElement();
    input.value("abcd");
    applyTextControlGeometry(input);
    return input;
  }

  private TextareaElement textarea() {
    TextareaElement textarea = new TextareaElement("ab\ncd");
    applyTextControlGeometry(textarea);
    textarea.resolvedStyle().lineHeight(1f);
    return textarea;
  }

  private void applyTextControlGeometry(Element element) {
    element.box().contentPosition(20, 20);
    element.box().contentSize(40, 40);
    element.box().padding().left(5);
    element.box().padding().right(5);
    element.box().padding().top(5);
    element.box().border().left(2);
    element.box().border().right(2);
    element.box().border().top(2);
    element.resolvedStyle().fontFamilies(Set.of(Font.DEFAULT.fontFamily()));
    element.resolvedStyle().fontStyle(FontStyle.NORMAL);
    element.resolvedStyle().fontWeight(FontWeight.REGULAR);
    element.resolvedStyle().fontSize(Length.pixel(16));
  }

  private void processInputPress(Frame frame, Vector2f cursorPosition) {
    processInputPress(frame, cursorPosition, ImmutableSet.of());
  }

  private void processInputPress(
      Frame frame, Vector2f cursorPosition, ImmutableSet<SystemKeyMod> mods) {
    SystemMouseClickEvent event =
        SystemMouseClickEvent.builder()
            .action(SystemKeyAction.PRESS)
            .mods(mods)
            .frame(frame)
            .button(SystemMouseButton.LEFT)
            .build();

    when(mouseService.getCursorPositions(frame))
        .thenReturn(new CursorPositions(cursorPosition, cursorPosition));

    listener.process(event, frame);
  }

  private SystemMouseClickEvent mousePress(Frame frame) {
    return mouseEvent(frame, SystemKeyAction.PRESS);
  }

  private SystemMouseClickEvent mouseRelease(Frame frame) {
    return mouseEvent(frame, SystemKeyAction.RELEASE);
  }

  private SystemMouseClickEvent mouseEvent(Frame frame, SystemKeyAction action) {
    return SystemMouseClickEvent.builder()
        .action(action)
        .mods(ImmutableSet.of())
        .frame(frame)
        .button(SystemMouseButton.LEFT)
        .build();
  }

  private Element scrollableElement(
      float clientWidth, float clientHeight, float scrollWidth, float scrollHeight) {
    Element element = div();
    element.box().contentSize(clientWidth, clientHeight);
    element.clientWidth(clientWidth);
    element.clientHeight(clientHeight);
    element.scrollWidth(scrollWidth);
    element.scrollHeight(scrollHeight);
    element.resolvedStyle().overflowX(Overflow.AUTO);
    element.resolvedStyle().overflowY(Overflow.AUTO);
    return element;
  }

  private static class FixedWidthTextMeasurer implements TextMeasurer {
    private static final float CHAR_WIDTH = 10;
    private static final FontMetrics FONT_METRICS = new FontMetrics(12, 4, 0, 16, 12);

    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      TextLineMetrics line = lineMetrics(text);
      return TextMetrics.builder()
          .line(line)
          .width(line.width())
          .height(line.height())
          .lineHeight(line.height())
          .fontMetrics(FONT_METRICS)
          .build();
    }

    @Override
    public TextMetrics measureText(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      return measureText(text, font, fontSize, lineHeight);
    }

    @Override
    public TextMetrics getTextMetrics(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      return measureText(text, font, fontSize, lineHeight);
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      return lineMetrics(text);
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      int caretIndex = Math.round(offsetX / CHAR_WIDTH);
      caretIndex = Math.max(0, Math.min(caretIndex, text.length()));
      return new TextCaretMetrics(caretIndex, caretIndex * CHAR_WIDTH);
    }

    private static TextLineMetrics lineMetrics(String text) {
      return TextLineMetrics.builder()
          .characters(text)
          .startIndex(0)
          .endIndex(text.length())
          .charCount(text.length())
          .width(text.length() * CHAR_WIDTH)
          .height(16)
          .baseline(12)
          .fontMetrics(FONT_METRICS)
          .build();
    }
  }
}
