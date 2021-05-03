package com.spinyowl.spinygui.core.system.event.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.CharEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.TextUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemCharEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;

  private SystemCharEventListener systemCharEventListener;

  @BeforeEach
  public void setUp() {
    systemCharEventListener =
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

    when(timeService.getCurrentTime()).thenReturn(currentTime);

    SystemCharEvent source = SystemCharEvent.builder().window(1).codepoint(1).build();

    CharEvent expected =
        CharEvent.builder()
            .source(frame)
            .target(element)
            .input(TextUtil.cpToStr(1))
            .timestamp(currentTime)
            .build();

    doNothing().when(eventProcessor).push(expected);

    // Act
    systemCharEventListener.process(source, frame);

    // Verify
    verify(eventProcessor).push(expected);
  }

  @Test
  void process_skipsGeneratingCharEvent() {
    // Arrange
    Frame frame = new Frame();
    Element focusedElement = new Element("input");
    frame.addChild(focusedElement);

    SystemCharEvent source = SystemCharEvent.builder().window(1).codepoint(1).build();

    // Act
    systemCharEventListener.process(source, frame);

    // Verify
    verify(timeService, times(0)).getCurrentTime();
    verify(eventProcessor, times(0)).push(any(CharEvent.class));
  }
}
