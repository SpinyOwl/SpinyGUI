package com.spinyowl.spinygui.core.system.event.listener;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.joml.Vector2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemCursorPosEventListenerTest {

  @Mock private MouseService mouseService;
  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;

  private SystemCursorPosEventListener listener;

  @BeforeEach
  public void setUp() {
    listener =
        SystemCursorPosEventListener.builder()
            .eventProcessor(eventProcessor)
            .mouseService(mouseService)
            .timeService(timeService)
            .build();
  }

  @Test
  void process_generatesEnterEvent() {
    // Arrange

    int posX = 1;
    int posY = 1;
    SystemCursorPosEvent event =
        SystemCursorPosEvent.builder().posX(posX).posY(posY).window(1).build();

    Frame frame = new Frame();
    frame.size(100, 100);

    Vector2f currentFirst = new Vector2f(-1, -1);
    Vector2f previousFirst = new Vector2f(-2, -2);

    CursorPositions cursorPositions = new CursorPositions(currentFirst, previousFirst);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    Vector2f currentSecond = new Vector2f(posX, posY);
    CursorPositions newCursorPosition = new CursorPositions(currentSecond, currentFirst);

    doNothing().when(mouseService).setCursorPositions(frame, newCursorPosition);

    CursorEnterEvent expectedEnterEvent =
        CursorEnterEvent.builder()
            .source(frame)
            .target(frame)
            .entered(true)
            .intersection(currentSecond)
            .cursorPosition(currentSecond)
            .build();

    doNothing().when(eventProcessor).push(expectedEnterEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(eventProcessor).push(expectedEnterEvent);
  }
}
