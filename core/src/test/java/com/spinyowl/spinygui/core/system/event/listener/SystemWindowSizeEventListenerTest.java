package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.WindowSizeEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.system.event.SystemWindowSizeEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemWindowSizeEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;
  private SystemEventListener<SystemWindowSizeEvent> listener;
  private int width;
  private int height;

  @BeforeEach
  void setUp() {
    listener =
        SystemWindowSizeEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build();
    width = 10;
    height = 40;
  }

  @Test
  void process_generatesEvent() {
    // Arrange
    var frame = frame();
    var timestamp = 1D;

    when(timeService.getCurrentTime()).thenReturn(timestamp);

    var sourceEvent = createEvent();
    var expectedEvent =
        WindowSizeEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timestamp)
            .width(10)
            .height(40)
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
    var event = createEvent();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    var frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }

  private SystemWindowSizeEvent createEvent() {
    return SystemWindowSizeEvent.builder().window(1).width(width).height(height).build();
  }
}
