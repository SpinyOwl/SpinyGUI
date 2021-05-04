package com.spinyowl.spinygui.core.system.event.listener;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.joml.Vector2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemScrollEventListenerTest {
  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;
  @Mock private MouseService mouseService;

  private SystemEventListener<SystemScrollEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemScrollEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .build();
  }

  @Test
  void process_generatesScrollEvent() {
    Frame frame = new Frame();
    frame.size(100, 100);

    Vector2f current = new Vector2f(10, 10);
    CursorPositions cursorPositions = new CursorPositions(current, current);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    double timestamp = 1D;
    when(timeService.getCurrentTime()).thenReturn(timestamp);

    SystemScrollEvent event = SystemScrollEvent.builder().window(1).offsetX(1).offsetY(-1).build();

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timestamp)
            .offsetX(1)
            .offsetY(-1)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).getCursorPositions(frame);
    verify(timeService).getCurrentTime();
    verify(eventProcessor).push(expectedEvent);
  }
}
