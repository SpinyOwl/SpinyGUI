package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.WindowPosEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowPosEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemWindowPosEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;
  private SystemEventListener<SystemWindowPosEvent> listener;
  private int posX;
  private int posY;

  @BeforeEach
  void setUp() {
    listener =
        SystemWindowPosEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build();
    posX = 10;
    posY = 40;
  }

  @Test
  void process_generatesEvent() {
    // Arrange
    var frame = frame();
    var timestamp = 1D;

    when(timeService.getCurrentTime()).thenReturn(timestamp);

    var sourceEvent = createEvent(frame);
    var expectedEvent =
        WindowPosEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timestamp)
            .posX(posX)
            .posY(posY)
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
    SystemWindowPosEvent event = createEvent(frame());
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }

  private SystemWindowPosEvent createEvent(Frame frame) {
    return SystemWindowPosEvent.builder().frame(frame).posX(posX).posY(posY).build();
  }
}
