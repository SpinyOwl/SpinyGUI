package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.MouseButton.LEFT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.CursorExitEvent;
import com.spinyowl.spinygui.core.event.MouseDragEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.joml.Vector2f;
import org.junit.jupiter.api.Assertions;
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

  private SystemEventListener<SystemCursorPosEvent> listener;

  @BeforeEach
  void setUp() {
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

    Frame frame = new Frame();
    frame.size(100, 100);

    SystemCursorPosEvent event =
        SystemCursorPosEvent.builder().posX(posX).posY(posY).frame(frame).build();

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
            .intersection(currentSecond)
            .cursorPosition(currentSecond)
            .build();

    doNothing().when(eventProcessor).push(expectedEnterEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).getCursorPositions(frame);
    verify(mouseService).setCursorPositions(frame, newCursorPosition);

    verify(eventProcessor).push(expectedEnterEvent);
    verify(eventProcessor, times(0)).push(any(CursorExitEvent.class));
    verify(eventProcessor, times(0)).push(any(MouseDragEvent.class));
  }

  @Test
  void process_generatesExitEvent() {
    // Arrange

    int posX = -1;
    int posY = -1;
    Frame frame = new Frame();
    frame.size(100, 100);

    SystemCursorPosEvent event =
        SystemCursorPosEvent.builder().posX(posX).posY(posY).frame(frame).build();

    Vector2f currentFirst = new Vector2f(1, 1);
    Vector2f previousFirst = new Vector2f(2, 2);

    CursorPositions cursorPositions = new CursorPositions(currentFirst, previousFirst);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);

    Vector2f currentSecond = new Vector2f(posX, posY);
    CursorPositions newCursorPosition = new CursorPositions(currentSecond, currentFirst);

    doNothing().when(mouseService).setCursorPositions(frame, newCursorPosition);

    CursorExitEvent expectedExitEvent =
        CursorExitEvent.builder()
            .source(frame)
            .target(frame)
            .intersection(currentSecond)
            .cursorPosition(currentSecond)
            .build();

    doNothing().when(eventProcessor).push(expectedExitEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).getCursorPositions(frame);
    verify(mouseService).setCursorPositions(frame, newCursorPosition);

    verify(eventProcessor).push(expectedExitEvent);
    verify(eventProcessor, times(0)).push(any(CursorEnterEvent.class));
    verify(eventProcessor, times(0)).push(any(MouseDragEvent.class));
  }

  @Test
  void process_generatesDragEvent() {
    // Arrange

    int posX = 13;
    int posY = 13;

    Frame frame = new Frame();
    frame.size(100, 100);

    SystemCursorPosEvent event =
        SystemCursorPosEvent.builder().posX(posX).posY(posY).frame(frame).build();

    Element element = new Element("div");
    element.size(10, 10);
    element.position(10, 10);
    element.focused(true);
    frame.addChild(element);

    // by these positions we achieve that current mouse target and previous mouse target are the
    // same elements -> no enter/exit events are generated.
    Vector2f currentFirst = new Vector2f(12, 12);
    Vector2f previousFirst = new Vector2f(11, 11);

    CursorPositions cursorPositions = new CursorPositions(currentFirst, previousFirst);
    when(mouseService.getCursorPositions(frame)).thenReturn(cursorPositions);
    when(mouseService.pressed(LEFT)).thenReturn(true);

    Vector2f currentSecond = new Vector2f(posX, posY);
    CursorPositions newCursorPosition = new CursorPositions(currentSecond, currentFirst);

    doNothing().when(mouseService).setCursorPositions(frame, newCursorPosition);

    Vector2f delta = currentSecond.sub(currentFirst, new Vector2f());
    MouseDragEvent expectedDragEvent =
        MouseDragEvent.builder().source(frame).target(element).delta(delta).build();
    doNothing().when(eventProcessor).push(expectedDragEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(mouseService).pressed(LEFT);
    verify(mouseService).getCursorPositions(frame);
    verify(mouseService).setCursorPositions(frame, newCursorPosition);

    verify(eventProcessor, times(1)).push(expectedDragEvent);
    verify(eventProcessor, times(0)).push(any(CursorExitEvent.class));
    verify(eventProcessor, times(0)).push(any(CursorEnterEvent.class));
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    SystemCursorPosEvent event =
        SystemCursorPosEvent.builder().posX(1).posY(1).frame(frame()).build();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }
}
