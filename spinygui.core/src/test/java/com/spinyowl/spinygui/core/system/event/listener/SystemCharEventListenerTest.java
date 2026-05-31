package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.CharEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.TextUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemCharEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;

  private SystemEventListener<SystemCharEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemCharEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build();
  }

  @Test
  void process_generatesCharEvent() {
    // Arrange
    Frame frame = new Frame();
    Element element = new Element("input");
    frame.addChild(element);

    // make element focused so it will be used to generate char event.
    element.focused(true);
    double currentTime = 1;

    when(timeService.currentTime()).thenReturn(currentTime);

    SystemCharEvent source = createEvent(frame);

    CharEvent expected =
        CharEvent.builder()
            .source(frame)
            .target(element)
            .input(TextUtil.cpToStr(1))
            .timestamp(currentTime)
            .build();

    doNothing().when(eventProcessor).push(expected);

    // Act
    listener.process(source, frame);

    // Verify
    verify(timeService).currentTime();
    verify(eventProcessor).push(expected);
  }

  @Test
  void process_whenFocusedTextInputInsertsPrintableInputAndGeneratesCharEvent() {
    // Arrange
    Frame frame = new Frame();
    InputElement input = new InputElement();
    input.value("ac");
    input.caretIndex(1);
    input.focused(true);
    frame.addChild(input);

    double currentTime = 1;
    when(timeService.currentTime()).thenReturn(currentTime);

    SystemCharEvent source = SystemCharEvent.builder().frame(frame).codepoint('b').build();

    CharEvent expected =
        CharEvent.builder()
            .source(frame)
            .target(input)
            .input("b")
            .timestamp(currentTime)
            .build();

    doNothing().when(eventProcessor).push(expected);

    // Act
    listener.process(source, frame);

    // Verify
    Assertions.assertEquals("abc", input.value());
    Assertions.assertEquals(2, input.caretIndex());
    verify(eventProcessor).push(expected);
  }

  @Test
  void process_whenFocusedTextInputHasSelectionReplacesSelection() {
    Frame frame = new Frame();
    InputElement input = new InputElement();
    input.value("abcd");
    input.select(1, 3);
    input.focused(true);
    frame.addChild(input);
    when(timeService.currentTime()).thenReturn(1D);

    listener.process(SystemCharEvent.builder().frame(frame).codepoint('x').build(), frame);

    Assertions.assertEquals("axd", input.value());
    Assertions.assertEquals(2, input.caretIndex());
    Assertions.assertFalse(input.hasSelection());
  }

  @Test
  void process_whenFocusedTextInputReceivesControlCodeKeepsValueAndGeneratesCharEvent() {
    // Arrange
    Frame frame = new Frame();
    InputElement input = new InputElement();
    input.value("abc");
    input.caretIndex(1);
    input.focused(true);
    frame.addChild(input);

    double currentTime = 1;
    when(timeService.currentTime()).thenReturn(currentTime);

    SystemCharEvent source = SystemCharEvent.builder().frame(frame).codepoint('\n').build();

    CharEvent expected =
        CharEvent.builder()
            .source(frame)
            .target(input)
            .input("\n")
            .timestamp(currentTime)
            .build();

    doNothing().when(eventProcessor).push(expected);

    // Act
    listener.process(source, frame);

    // Verify
    Assertions.assertEquals("abc", input.value());
    Assertions.assertEquals(1, input.caretIndex());
    verify(eventProcessor).push(expected);
  }

  @Test
  void process_skipsGeneratingCharEvent() {
    // Arrange
    Frame frame = new Frame();
    Element focusedElement = new Element("input");
    frame.addChild(focusedElement);

    SystemCharEvent source = createEvent(frame);

    // Act
    listener.process(source, frame);

    // Verify
    verify(timeService, times(0)).currentTime();
    verify(eventProcessor, times(0)).push(any(CharEvent.class));
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    SystemCharEvent event = createEvent(frame());
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }

  private SystemCharEvent createEvent(Frame frame) {
    return SystemCharEvent.builder().frame(frame).codepoint(1).build();
  }
}
