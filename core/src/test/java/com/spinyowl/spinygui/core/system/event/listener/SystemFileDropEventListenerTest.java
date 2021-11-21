package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.FileDropEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemFileDropEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.joml.Vector2f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemFileDropEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;
  @Mock private MouseService mouseService;

  private SystemEventListener<SystemFileDropEvent> listener;
  private String[] paths;

  @BeforeEach
  void setUp() {
    listener =
        SystemFileDropEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .build();
    paths = new String[] {"first", "second"};
  }

  @Test
  void process_generatesDropEvent() {
    // Arrange
    Frame frame = new Frame();
    frame.dimensions().contentSize(100, 100);

    Vector2f current = new Vector2f(10, 10);
    Vector2f previous = new Vector2f(9, 9);
    CursorPositions cursorPositions = new CursorPositions(current, previous);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    SystemFileDropEvent systemFileDropEvent = createEvent(frame);

    double timestamp = 1D;
    when(timeService.currentTime()).thenReturn(timestamp);
    FileDropEvent expectedFileDropEvent =
        FileDropEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timestamp)
            .paths(paths)
            .build();
    doNothing().when(eventProcessor).push(expectedFileDropEvent);

    // Act
    listener.process(systemFileDropEvent, frame);

    // Verify
    verify(timeService).currentTime();
    verify(mouseService).getCursorPositions(frame);
    verify(eventProcessor).push(expectedFileDropEvent);
  }

  @Test
  void process_doNothingIfNoTarget() {
    // Arrange
    Frame frame = frame();
    frame.dimensions().contentSize(100, 100);
    Vector2f current = new Vector2f(110, 110);
    CursorPositions cursorPositions = new CursorPositions(current, current);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    SystemFileDropEvent systemFileDropEvent = createEvent(frame);

    // Act
    listener.process(systemFileDropEvent, frame);

    // Verify
    verifyNoInteractions(timeService);
    verifyNoInteractions(eventProcessor);
    verify(mouseService).getCursorPositions(frame);
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    SystemFileDropEvent event = createEvent(frame());
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }

  private SystemFileDropEvent createEvent(Frame frame) {
    return SystemFileDropEvent.builder().frame(frame).paths(paths).build();
  }
}
