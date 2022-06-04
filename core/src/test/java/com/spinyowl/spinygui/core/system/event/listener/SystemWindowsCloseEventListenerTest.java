package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowCloseEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemWindowsCloseEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;

  private SystemEventListener<SystemWindowCloseEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemWindowsCloseEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build();
  }

  @Test
  void process_generatesWindowCloseEvent() {

    // Arrange
    Frame frame = new Frame();
    frame.box().contentSize(100, 100);

    SystemWindowCloseEvent event = createEvent(frame);

    double timestamp = 1D;
    when(timeService.currentTime()).thenReturn(timestamp);

    WindowCloseEvent expectedEvent =
        WindowCloseEvent.builder().source(frame).target(frame).timestamp(timestamp).build();
    doNothing().when(eventProcessor).push(expectedEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(timeService).currentTime();
    verify(eventProcessor).push(expectedEvent);
  }

  private SystemWindowCloseEvent createEvent(Frame frame) {
    return SystemWindowCloseEvent.builder().frame(frame).build();
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    SystemWindowCloseEvent event = createEvent(frame());
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }
}
