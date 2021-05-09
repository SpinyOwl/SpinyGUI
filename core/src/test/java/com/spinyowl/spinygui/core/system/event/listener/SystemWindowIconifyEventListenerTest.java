package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.WindowIconifyEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowIconifyEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemWindowIconifyEventListenerTest {
  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;

  private SystemEventListener<SystemWindowIconifyEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemWindowIconifyEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build();
  }

  @Test
  void process_generatesEvent() {
    // Arrange
    var frame = frame();
    var timestamp = 1D;

    when(timeService.getCurrentTime()).thenReturn(timestamp);

    var sourceEvent = createEvent();
    var expectedEvent =
        WindowIconifyEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timestamp)
            .iconified(true)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    // Act
    listener.process(sourceEvent, frame);

    // Verify
    verify(timeService).getCurrentTime();
    verify(eventProcessor).push(expectedEvent);
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    SystemWindowIconifyEvent event = createEvent();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }

  private SystemWindowIconifyEvent createEvent() {
    return SystemWindowIconifyEvent.builder().window(1).iconified(true).build();
  }
}
