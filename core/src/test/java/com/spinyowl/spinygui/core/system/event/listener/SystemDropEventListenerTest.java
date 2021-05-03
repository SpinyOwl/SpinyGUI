package com.spinyowl.spinygui.core.system.event.listener;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.common.collect.ImmutableList;
import com.spinyowl.spinygui.core.event.DropEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemDropEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.joml.Vector2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemDropEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;
  @Mock private MouseService mouseService;

  private SystemEventListener<SystemDropEvent> systemDropEventListener;

  @BeforeEach
  void setUp() {
    systemDropEventListener =
        SystemDropEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .build();
  }

  @Test
  void process_generatesDropEvent() {
    // Arrange
    Frame frame = new Frame();
    frame.size(100, 100);

    Vector2f current = new Vector2f(10, 10);
    Vector2f previous = new Vector2f(9, 9);
    CursorPositions cursorPositions = new CursorPositions(current, previous);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    ImmutableList<String> paths = ImmutableList.of("first", "second");
    SystemDropEvent systemDropEvent = SystemDropEvent.builder().paths(paths).build();

    double timestamp = 1D;
    when(timeService.getCurrentTime()).thenReturn(timestamp);
    DropEvent expectedDropEvent =
        DropEvent.builder().source(frame).target(frame).timestamp(timestamp).paths(paths).build();
    doNothing().when(eventProcessor).push(expectedDropEvent);

    // Act
    systemDropEventListener.process(systemDropEvent, frame);

    // Verify
    verify(timeService).getCurrentTime();
    verify(mouseService).getCursorPositions(frame);
    verify(eventProcessor).push(expectedDropEvent);
  }
}
