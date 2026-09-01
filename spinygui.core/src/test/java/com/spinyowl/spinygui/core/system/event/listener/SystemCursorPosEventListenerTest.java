package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.MouseButton.LEFT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.CursorExitEvent;
import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.MouseDragEvent;
import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.FontTestOwner;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.List;
import org.joml.Vector2f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    FontTestOwner.install();
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
    frame.box().contentSize(100, 100);

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
    frame.box().contentSize(100, 100);

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
    frame.box().contentSize(100, 100);

    SystemCursorPosEvent event =
        SystemCursorPosEvent.builder().posX(posX).posY(posY).frame(frame).build();

    Element element = new Element("div");
    element.box().contentSize(10, 10);
    element.box().contentPosition(10, 10);
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
  void process_leftDragFocusedPressedTextInput_extendsSelectionToCursorCaret() {
    listener = listenerWithTextMeasurer();
    InputElement input = textInput();
    input.focused(true);
    input.pressed(true);
    input.caretIndex(1);
    Frame frame = frame(input);
    frame.box().contentSize(100, 100);
    Vector2f previous = new Vector2f(35, 25);
    Vector2f current = new Vector2f(55, 25);
    when(mouseService.getCursorPositions(frame)).thenReturn(new CursorPositions(previous, previous));
    when(mouseService.pressed(LEFT)).thenReturn(true);

    listener.process(cursorEvent(frame, current.x(), current.y()), frame);

    assertEquals(1, input.selectionStart());
    assertEquals(4, input.selectionEnd());
    assertEquals(4, input.caretIndex());
  }

  @Test
  void process_leftDragFocusedPressedTextarea_extendsSelectionToCursorCaret() {
    listener = listenerWithTextMeasurer();
    TextareaElement textarea = textarea();
    textarea.focused(true);
    textarea.pressed(true);
    textarea.caretIndex(1);
    Frame frame = frame(textarea);
    frame.box().contentSize(100, 100);
    Vector2f previous = new Vector2f(35, 25);
    Vector2f current = new Vector2f(55, 45);
    when(mouseService.getCursorPositions(frame)).thenReturn(new CursorPositions(previous, previous));
    when(mouseService.pressed(LEFT)).thenReturn(true);

    listener.process(cursorEvent(frame, current.x(), current.y()), frame);

    assertEquals(1, textarea.selectionStart());
    assertEquals(5, textarea.selectionEnd());
    assertEquals(5, textarea.caretIndex());
  }

  @Test
  void process_dragVerticalScrollbarThumb_updatesScrollTopAndClampsAtBothEnds() {
    ScrollbarInteraction scrollbarInteraction = new ScrollbarInteraction();
    listener =
        SystemCursorPosEventListener.builder()
            .eventProcessor(eventProcessor)
            .mouseService(mouseService)
            .timeService(timeService)
            .scrollbarInteraction(scrollbarInteraction)
            .build();
    Element element = scrollableElement(100, 100, 100, 300);
    element.resolvedStyle().overflowX(Overflow.HIDDEN);
    Frame frame = frame(element);
    frame.box().contentSize(200, 200);

    beginDrag(scrollbarInteraction, element, new Vector2f(99, 10));
    when(mouseService.getCursorPositions(frame))
        .thenReturn(new CursorPositions(new Vector2f(99, 10), new Vector2f(99, 10)))
        .thenReturn(new CursorPositions(new Vector2f(99, 90), new Vector2f(99, 10)));
    when(mouseService.pressed(LEFT)).thenReturn(true);
    when(timeService.currentTime()).thenReturn(1D, 2D);

    listener.process(cursorEvent(frame, 99, 90), frame);

    assertEquals(200, element.scrollTop(), 0.0001f);

    scrollbarInteraction.endDrag();
    beginDrag(scrollbarInteraction, element, new Vector2f(99, 90));

    listener.process(cursorEvent(frame, 99, 0), frame);

    assertEquals(0, element.scrollTop(), 0.0001f);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(eventProcessor, times(2)).push(eventCaptor.capture());
    ScrollEvent first = assertInstanceOf(ScrollEvent.class, eventCaptor.getAllValues().get(0));
    ScrollEvent second = assertInstanceOf(ScrollEvent.class, eventCaptor.getAllValues().get(1));
    assertEquals(200, first.offsetY(), 0.0001f);
    assertEquals(-200, second.offsetY(), 0.0001f);
  }

  @Test
  void process_dragHorizontalScrollbarThumb_updatesScrollLeft() {
    ScrollbarInteraction scrollbarInteraction = new ScrollbarInteraction();
    listener =
        SystemCursorPosEventListener.builder()
            .eventProcessor(eventProcessor)
            .mouseService(mouseService)
            .timeService(timeService)
            .scrollbarInteraction(scrollbarInteraction)
            .build();
    Element element = scrollableElement(100, 100, 300, 100);
    element.resolvedStyle().overflowY(Overflow.HIDDEN);
    Frame frame = frame(element);
    frame.box().contentSize(200, 200);
    beginDrag(scrollbarInteraction, element, new Vector2f(10, 99));

    when(mouseService.getCursorPositions(frame))
        .thenReturn(new CursorPositions(new Vector2f(10, 99), new Vector2f(10, 99)));
    when(mouseService.pressed(LEFT)).thenReturn(true);
    when(timeService.currentTime()).thenReturn(1D);

    listener.process(cursorEvent(frame, 60, 99), frame);

    assertEquals(150, element.scrollLeft(), 0.0001f);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(eventProcessor).push(eventCaptor.capture());
    ScrollEvent event = assertInstanceOf(ScrollEvent.class, eventCaptor.getValue());
    assertEquals(element, event.target());
    assertEquals(150, event.offsetX(), 0.0001f);
    assertEquals(0, event.offsetY(), 0.0001f);
  }

  @Test
  void process_modalOpenCancelsCapturedBackgroundScrollbarDrag() {
    ScrollbarInteraction scrollbarInteraction = new ScrollbarInteraction();
    listener =
        SystemCursorPosEventListener.builder()
            .eventProcessor(eventProcessor)
            .mouseService(mouseService)
            .timeService(timeService)
            .scrollbarInteraction(scrollbarInteraction)
            .build();
    Element background = scrollableElement(100, 100, 100, 300);
    background.resolvedStyle().overflowX(Overflow.HIDDEN);
    Element modal = div();
    modal.box().contentSize(50, 50);
    Frame frame = frame(background, modal);
    frame.box().contentSize(200, 200);
    beginDrag(scrollbarInteraction, background, new Vector2f(99, 10));
    frame.topLayer().showModal(modal);
    when(mouseService.getCursorPositions(frame))
        .thenReturn(new CursorPositions(new Vector2f(99, 10), new Vector2f(99, 10)));
    when(mouseService.pressed(LEFT)).thenReturn(true);

    listener.process(cursorEvent(frame, 99, 90), frame);

    assertFalse(scrollbarInteraction.dragging());
    assertEquals(0, background.scrollTop(), 0.0001f);
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

  private SystemCursorPosEvent cursorEvent(Frame frame, float x, float y) {
    return SystemCursorPosEvent.builder().posX(x).posY(y).frame(frame).build();
  }

  private SystemEventListener<SystemCursorPosEvent> listenerWithTextMeasurer() {
    return SystemCursorPosEventListener.builder()
        .eventProcessor(eventProcessor)
        .mouseService(mouseService)
        .timeService(timeService)
        .textMeasurer(new FixedWidthTextMeasurer())
        .build();
  }

  private InputElement textInput() {
    InputElement input = new InputElement();
    input.value("abcd");
    applyTextControlGeometry(input);
    return input;
  }

  private TextareaElement textarea() {
    TextareaElement textarea = new TextareaElement("ab\ncd");
    applyTextControlGeometry(textarea);
    textarea.resolvedStyle().lineHeight(1f);
    return textarea;
  }

  private void applyTextControlGeometry(Element element) {
    element.box().contentPosition(20, 20);
    element.box().contentSize(40, 40);
    element.box().padding().left(5);
    element.box().padding().top(5);
    element.box().border().left(2);
    element.box().border().top(2);
    element.resolvedStyle().fontFamilies(List.of(Font.DEFAULT.fontFamily()));
    element.resolvedStyle().fontStyle(FontStyle.NORMAL);
    element.resolvedStyle().fontWeight(FontWeight.REGULAR);
    element.resolvedStyle().fontSize(Length.pixel(16));
  }

  private void beginDrag(
      ScrollbarInteraction scrollbarInteraction, Element element, Vector2f point) {
    scrollbarInteraction.beginDrag(scrollbarInteraction.hit(List.of(element), point), point);
  }

  private Element scrollableElement(
      float clientWidth, float clientHeight, float scrollWidth, float scrollHeight) {
    Element element = div();
    element.box().contentSize(clientWidth, clientHeight);
    element.clientWidth(clientWidth);
    element.clientHeight(clientHeight);
    element.scrollWidth(scrollWidth);
    element.scrollHeight(scrollHeight);
    element.resolvedStyle().overflowX(Overflow.AUTO);
    element.resolvedStyle().overflowY(Overflow.AUTO);
    return element;
  }

  private static class FixedWidthTextMeasurer implements TextMeasurer {
    private static final float CHAR_WIDTH = 10;
    private static final FontMetrics FONT_METRICS = new FontMetrics(12, 4, 0, 16, 12);

    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      TextLineMetrics line = lineMetrics(text);
      return TextMetrics.builder()
          .line(line)
          .width(line.width())
          .height(line.height())
          .lineHeight(line.height())
          .fontMetrics(FONT_METRICS)
          .build();
    }

    @Override
    public TextMetrics measureText(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      return measureText(text, font, fontSize, lineHeight);
    }

    @Override
    public TextMetrics getTextMetrics(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      return measureText(text, font, fontSize, lineHeight);
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      return lineMetrics(text);
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      int caretIndex = Math.round(offsetX / CHAR_WIDTH);
      caretIndex = Math.max(0, Math.min(caretIndex, text.length()));
      return new TextCaretMetrics(caretIndex, caretIndex * CHAR_WIDTH);
    }

    private static TextLineMetrics lineMetrics(String text) {
      return TextLineMetrics.builder()
          .characters(text)
          .startIndex(0)
          .endIndex(text.length())
          .charCount(text.length())
          .width(text.length() * CHAR_WIDTH)
          .height(16)
          .baseline(12)
          .fontMetrics(FONT_METRICS)
          .build();
    }
  }
}
