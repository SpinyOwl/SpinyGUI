package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.WindowFocusEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowFocusEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemWindowFocusEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;

  private SystemEventListener<SystemWindowFocusEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemWindowFocusEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build();
  }

  @Test
  void process_generatesWindowFocusEvent() {

    // Arrange
    Frame frame = new Frame();
    frame.box().contentSize(100, 100);

    SystemWindowFocusEvent event = createEvent(frame);

    double timestamp = 1D;
    when(timeService.currentTime()).thenReturn(timestamp);

    WindowFocusEvent expectedEvent =
        WindowFocusEvent.builder().source(frame).target(frame).timestamp(timestamp).build();
    doNothing().when(eventProcessor).push(expectedEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(timeService).currentTime();
    verify(eventProcessor).push(expectedEvent);
  }

  private SystemWindowFocusEvent createEvent(Frame frame) {
    return SystemWindowFocusEvent.builder().frame(frame).build();
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    SystemWindowFocusEvent event = createEvent(frame());
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }
}
