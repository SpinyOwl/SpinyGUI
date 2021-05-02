package com.spinyowl.spinygui.core.system.event.listener;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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
  void process_pushesCharEvent_generatedFrom_SystemCharEvent() {
    Frame frame = mock(Frame.class);
    Element focusedElement = mock(Element.class);

    double currentTime = 1;

    when(timeService.getCurrentTime()).thenReturn(currentTime);
    when(frame.getFocusedElement()).thenReturn(focusedElement);

    SystemCharEvent source = SystemCharEvent.builder().window(1).codepoint(1).build();

    CharEvent expected =
        CharEvent.builder()
            .source(frame)
            .target(focusedElement)
            .input(TextUtil.cpToStr(1))
            .timestamp(currentTime)
            .build();

    doNothing().when(eventProcessor).push(expected);

    systemCharEventListener.process(source, frame);

    verify(eventProcessor).push(expected);
  }
}
