package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.WindowRefreshEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.system.event.SystemWindowRefreshEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemWindowRefreshEventListenerTest {
  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;

  private SystemEventListener<SystemWindowRefreshEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemWindowRefreshEventListener.builder()
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
        WindowRefreshEvent.builder().source(frame).target(frame).timestamp(timestamp).build();
    doNothing().when(eventProcessor).push(expectedEvent);

    // Act
    listener.process(sourceEvent, frame);

    // Verify
    verify(timeService).getCurrentTime();
    verify(eventProcessor).push(expectedEvent);
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    var event = createEvent();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    var frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }

  private SystemWindowRefreshEvent createEvent() {
    return SystemWindowRefreshEvent.builder().window(1).build();
  }
}
