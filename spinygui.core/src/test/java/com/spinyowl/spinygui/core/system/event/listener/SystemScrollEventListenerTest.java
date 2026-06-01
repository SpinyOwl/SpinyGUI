package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.style.types.Overflow;
import org.joml.Vector2f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemScrollEventListenerTest {

  public static final int OFFSET_X = 1;
  public static final int OFFSET_Y = -1;
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
  void process_whenInnerCanScrollVertically_scrollsInnerOnly() {
    Frame frame = frame();
    Element outer = scrollContainer(0, 0, 200, 200, 200, 500);
    Element inner = scrollContainer(0, 0, 100, 100, 100, 300);
    frame.addChild(outer);
    outer.addChild(inner);
    frame.box().contentSize(500, 500);

    double timestamp = 1D;
    cursorAt(frame, 10, 10);
    when(timeService.currentTime()).thenReturn(timestamp);

    SystemScrollEvent event = createEvent(frame);

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(inner)
            .timestamp(timestamp)
            .offsetX(OFFSET_X)
            .offsetY(OFFSET_Y)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    assertEquals(50, inner.scrollTop());
    assertEquals(0, outer.scrollTop());
    verify(eventProcessor).push(expectedEvent);
    verifyNoMoreInteractions(eventProcessor);
  }

  @Test
  void process_whenInnerAtScrollLimit_chainsVerticalScrollToOuter() {
    Frame frame = frame();
    Element outer = scrollContainer(0, 0, 200, 200, 200, 500);
    Element inner = scrollContainer(0, 0, 100, 100, 100, 300);
    inner.scrollTop(200);
    frame.addChild(outer);
    outer.addChild(inner);
    frame.box().contentSize(500, 500);

    double timestamp = 1D;
    cursorAt(frame, 10, 10);
    when(timeService.currentTime()).thenReturn(timestamp);

    SystemScrollEvent event = createEvent(frame);

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(outer)
            .timestamp(timestamp)
            .offsetX(OFFSET_X)
            .offsetY(OFFSET_Y)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    assertEquals(200, inner.scrollTop());
    assertEquals(50, outer.scrollTop());
    verify(eventProcessor).push(expectedEvent);
    verifyNoMoreInteractions(eventProcessor);
  }

  @Test
  void process_whenInnerOverflowHidden_doesNotConsumeAndAncestorScrolls() {
    Frame frame = frame();
    Element outer = scrollContainer(0, 0, 200, 200, 200, 500);
    Element inner = scrollContainer(0, 0, 100, 100, 100, 300);
    inner.resolvedStyle().overflowY(Overflow.HIDDEN);
    frame.addChild(outer);
    outer.addChild(inner);
    frame.box().contentSize(500, 500);

    double timestamp = 1D;
    cursorAt(frame, 10, 10);
    when(timeService.currentTime()).thenReturn(timestamp);

    SystemScrollEvent event = createEvent(frame);

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(outer)
            .timestamp(timestamp)
            .offsetX(OFFSET_X)
            .offsetY(OFFSET_Y)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    assertEquals(0, inner.scrollTop());
    assertEquals(50, outer.scrollTop());
    verify(eventProcessor).push(expectedEvent);
    verifyNoMoreInteractions(eventProcessor);
  }

  @Test
  void process_whenHorizontalAxisCanScroll_scrollsHorizontally() {
    Frame frame = frame();
    Element target = scrollContainer(0, 0, 100, 100, 300, 100);
    frame.addChild(target);
    frame.box().contentSize(500, 500);

    double timestamp = 1D;
    cursorAt(frame, 10, 10);
    when(timeService.currentTime()).thenReturn(timestamp);

    SystemScrollEvent event =
        SystemScrollEvent.builder().frame(frame).offsetX(-1).offsetY(0).build();

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(target)
            .timestamp(timestamp)
            .offsetX(-1)
            .offsetY(0)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    assertEquals(0, target.scrollTop());
    assertEquals(50, target.scrollLeft());
    verify(eventProcessor).push(expectedEvent);
    verifyNoMoreInteractions(eventProcessor);
  }

  @Test
  void process_whenHorizontalReverseScrollCanMove_scrollsHorizontally() {
    Frame frame = frame();
    Element target = scrollContainer(0, 0, 100, 100, 300, 100);
    target.scrollLeft(50);
    frame.addChild(target);
    frame.box().contentSize(500, 500);

    double timestamp = 1D;
    cursorAt(frame, 10, 10);
    when(timeService.currentTime()).thenReturn(timestamp);

    SystemScrollEvent event =
        SystemScrollEvent.builder().frame(frame).offsetX(1).offsetY(0).build();

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(target)
            .timestamp(timestamp)
            .offsetX(1)
            .offsetY(0)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    assertEquals(0, target.scrollLeft());
    verify(eventProcessor).push(expectedEvent);
    verifyNoMoreInteractions(eventProcessor);
  }

  @Test
  void process_whenNoHoveredElementCanConsumeScroll_usesFocusedElementFallback() {
    Frame frame = frame();
    Element hovered = element(0, 0, 100, 100);
    Element focused = scrollContainer(200, 0, 100, 100, 100, 300);
    focused.focused(true);
    frame.addChild(hovered);
    frame.addChild(focused);
    frame.box().contentSize(100, 100);

    double timestamp = 1D;
    cursorAt(frame, 10, 10);
    when(timeService.currentTime()).thenReturn(timestamp);

    SystemScrollEvent event = createEvent(frame);

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(focused)
            .timestamp(timestamp)
            .offsetX(OFFSET_X)
            .offsetY(OFFSET_Y)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    assertEquals(50, focused.scrollTop());
    verify(eventProcessor).push(expectedEvent);
    verifyNoMoreInteractions(eventProcessor);
  }

  @Test
  void process_whenHoveredTextareaContentOverflows_scrollsTextareaTextViewport() {
    listener =
        SystemScrollEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .textMeasurer(new FixedTextMeasurer())
            .build();
    Frame frame = frame();
    TextareaElement textarea = textarea(0, 0, 100, 20, "one\ntwo\nthree\nfour");
    frame.addChild(textarea);
    frame.box().contentSize(200, 200);

    double timestamp = 1D;
    cursorAt(frame, 10, 10);
    when(timeService.currentTime()).thenReturn(timestamp);

    SystemScrollEvent event = createEvent(frame);

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(textarea)
            .timestamp(timestamp)
            .offsetX(OFFSET_X)
            .offsetY(OFFSET_Y)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    assertEquals(20, textarea.textScrollTop());
    verify(eventProcessor).push(expectedEvent);
    verifyNoMoreInteractions(eventProcessor);
  }

  @Test
  void process_whenHoveredTextareaAtScrollLimit_chainsVerticalScrollToAncestor() {
    listener =
        SystemScrollEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .textMeasurer(new FixedTextMeasurer())
            .build();
    Frame frame = frame();
    Element outer = scrollContainer(0, 0, 200, 200, 200, 500);
    TextareaElement textarea = textarea(0, 0, 100, 20, "one\ntwo\nthree\nfour");
    textarea.textScrollTop(20);
    frame.addChild(outer);
    outer.addChild(textarea);
    frame.box().contentSize(500, 500);

    double timestamp = 1D;
    cursorAt(frame, 10, 10);
    when(timeService.currentTime()).thenReturn(timestamp);

    SystemScrollEvent event = createEvent(frame);

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(outer)
            .timestamp(timestamp)
            .offsetX(OFFSET_X)
            .offsetY(OFFSET_Y)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    assertEquals(20, textarea.textScrollTop());
    assertEquals(50, outer.scrollTop());
    verify(eventProcessor).push(expectedEvent);
    verifyNoMoreInteractions(eventProcessor);
  }

  @Test
  void process_whenCursorOutsideFrame_doNotGenerateScrollEvent() {
    Frame frame = frame();
    frame.box().contentSize(100, 100);

    cursorAt(frame, -10, -10);

    listener.process(createEvent(frame), frame);

    verifyNoInteractions(eventProcessor);
    verifyNoInteractions(timeService);
  }

  @Test
  void process_whenCursorOutsideFrameAndFocusedElementCanScroll_usesFocusedElementFallback() {
    Frame frame = frame();
    Element focused = scrollContainer(200, 0, 100, 100, 100, 300);
    focused.focused(true);
    frame.addChild(focused);
    frame.box().contentSize(100, 100);

    double timestamp = 1D;
    cursorAt(frame, -10, -10);
    when(timeService.currentTime()).thenReturn(timestamp);

    SystemScrollEvent event = createEvent(frame);

    ScrollEvent expectedEvent =
        ScrollEvent.builder()
            .source(frame)
            .target(focused)
            .timestamp(timestamp)
            .offsetX(OFFSET_X)
            .offsetY(OFFSET_Y)
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    assertEquals(50, focused.scrollTop());
    verify(eventProcessor).push(expectedEvent);
    verifyNoMoreInteractions(eventProcessor);
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    SystemScrollEvent event = createEvent(frame());
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }

  private SystemScrollEvent createEvent(Frame frame) {
    return SystemScrollEvent.builder().frame(frame).offsetX(OFFSET_X).offsetY(OFFSET_Y).build();
  }

  private void cursorAt(Frame frame, float x, float y) {
    Vector2f current = new Vector2f(x, y);
    when(mouseService.getCursorPositions(frame)).thenReturn(new CursorPositions(current, current));
  }

  private Element scrollContainer(
      float x, float y, float width, float height, float scrollWidth, float scrollHeight) {
    Element element = element(x, y, width, height);
    element.clientWidth(width);
    element.clientHeight(height);
    element.scrollWidth(scrollWidth);
    element.scrollHeight(scrollHeight);
    element.resolvedStyle().overflowX(Overflow.AUTO);
    element.resolvedStyle().overflowY(Overflow.AUTO);
    return element;
  }

  private Element element(float x, float y, float width, float height) {
    Element element = NodeBuilder.div();
    element.box().contentPosition(x, y);
    element.box().contentSize(width, height);
    return element;
  }

  private TextareaElement textarea(float x, float y, float width, float height, String value) {
    TextareaElement textarea = NodeBuilder.textarea(value);
    textarea.box().contentPosition(x, y);
    textarea.box().contentSize(width, height);
    return textarea;
  }

  private static class FixedTextMeasurer implements TextMeasurer {
    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      TextLineMetrics line = getTextLineMetrics(text, font, fontSize, lineHeight);
      return TextMetrics.builder()
          .line(line)
          .width(line.width())
          .height(line.height())
          .lineHeight(line.height())
          .fontMetrics(line.fontMetrics())
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
      return measureText(text, offsetX, font, fontSize, lineHeight, maxWidth, wordWrap);
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      FontMetrics fontMetrics = new FontMetrics(8, 2, 0, 10, 8);
      return TextLineMetrics.builder()
          .characters(text)
          .startIndex(0)
          .endIndex(text.length())
          .charCount(text.length())
          .width(text.length() * 10f)
          .height(10)
          .baseline(8)
          .fontMetrics(fontMetrics)
          .build();
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      int index = Math.max(0, Math.min(text.length(), Math.round(offsetX / 10f)));
      return new TextCaretMetrics(index, index * 10f);
    }
  }
}
