package com.spinyowl.spinygui.core.system.event.listener;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.KeyboardLayout;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.impl.KeyboardLayoutImpl;
import com.spinyowl.spinygui.core.input.impl.MouseServiceImpl;
import com.spinyowl.spinygui.core.input.impl.ShortcutRegistryImpl;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops;
import com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerCapability;
import com.spinyowl.spinygui.core.system.font.internal.ResolvedMeasurement;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutService;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SystemControlTextLayoutServiceCompositionTest {

  @Test
  void allTextAwareListenersRetainTheExactInjectedSharedService() {
    DefaultEventProcessor events = new DefaultEventProcessor();
    TimeService time = () -> 1D;
    MouseServiceImpl mouse = new MouseServiceImpl();
    ScrollbarInteraction scrollbar = new ScrollbarInteraction();
    ControlTextLayoutService service =
        new ControlTextLayoutService(mock(TextMeasurer.class));

    SystemCharEventListener chars =
        SystemCharEventListener.builder()
            .eventProcessor(events)
            .timeService(time)
            .controlTextLayoutService(service)
            .build();
    SystemKeyEventListener keys =
        SystemKeyEventListener.builder()
            .eventProcessor(events)
            .timeService(time)
            .keyboard(new Keyboard(new KeyboardLayoutImpl(Map.of()), new ShortcutRegistryImpl()))
            .controlTextLayoutService(service)
            .build();
    SystemCursorPosEventListener cursor =
        SystemCursorPosEventListener.builder()
            .eventProcessor(events)
            .timeService(time)
            .mouseService(mouse)
            .scrollbarInteraction(scrollbar)
            .controlTextLayoutService(service)
            .build();
    SystemMouseClickEventListener clicks =
        SystemMouseClickEventListener.builder()
            .eventProcessor(events)
            .timeService(time)
            .mouseService(mouse)
            .scrollbarInteraction(scrollbar)
            .controlTextLayoutService(service)
            .build();
    SystemScrollEventListener scroll =
        SystemScrollEventListener.builder()
            .eventProcessor(events)
            .timeService(time)
            .mouseService(mouse)
            .controlTextLayoutService(service)
            .build();

    assertSame(service, chars.controlTextLayoutService());
    assertSame(service, keys.controlTextLayoutService());
    assertSame(service, cursor.controlTextLayoutService());
    assertSame(service, clicks.controlTextLayoutService());
    assertSame(service, scroll.controlTextLayoutService());
  }

  @Test
  void charAndKeyInvalidationsRebuildExactlyOnceThenReturnToWarmReuse() {
    DefaultEventProcessor events = new DefaultEventProcessor();
    TimeService time = () -> 1D;
    ListenerMeasurer measurer = new ListenerMeasurer();
    ControlTextLayoutService service = new ControlTextLayoutService(measurer);
    Keyboard keyboard = mock(Keyboard.class);
    KeyboardLayout layout = mock(KeyboardLayout.class);
    when(keyboard.layout()).thenReturn(layout);
    when(layout.keyCode(7)).thenReturn(KeyCode.BACKSPACE);
    SystemCharEventListener chars =
        SystemCharEventListener.builder()
            .eventProcessor(events)
            .timeService(time)
            .controlTextLayoutService(service)
            .build();
    SystemKeyEventListener keys =
        SystemKeyEventListener.builder()
            .eventProcessor(events)
            .timeService(time)
            .keyboard(keyboard)
            .controlTextLayoutService(service)
            .build();
    Frame frame = new Frame();
    InputElement input = new InputElement();
    input.value("abc");
    input.caretIndex(3);
    input.focused(true);
    input.box().contentSize(100, 20);
    frame.addChild(input);
    service.query(input);

    chars.process(SystemCharEvent.builder().frame(frame).codepoint('d').build(), frame);
    assertSame(input.currentTextLayoutSnapshot(), service.query(input));
    org.junit.jupiter.api.Assertions.assertEquals(2, measurer.builds);

    keys.process(
        SystemKeyEvent.builder()
            .frame(frame)
            .keyCode(7)
            .scancode(7)
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .build(),
        frame);
    assertSame(input.currentTextLayoutSnapshot(), service.query(input));
    org.junit.jupiter.api.Assertions.assertEquals(3, measurer.builds);
  }

  private static final class ListenerMeasurer
      implements TextMeasurer, RangeTextMeasurerCapability {
    private int builds;

    @Override
    public ResolvedMeasurement measureRange(
        String source,
        int start,
        int end,
        float offsetX,
        java.util.List<Font> fonts,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      builds++;
      int count = source.codePointCount(start, end);
      int[] boundaries = new int[count + 1];
      float[] advances = new float[count + 1];
      int boundary = start;
      boundaries[0] = start;
      for (int index = 1; index <= count; index++) {
        boundary = source.offsetByCodePoints(boundary, 1);
        boundaries[index] = boundary;
        advances[index] = index * 10f;
      }
      FontMetrics fontMetrics = new FontMetrics(12, 4, 0, 16, 12);
      TextLineMetrics line =
          new TextLineMetrics(
              source.substring(start, end),
              start,
              end,
              end - start,
              count * 10f,
              16,
              12,
              fontMetrics,
              java.util.List.of());
      return new ResolvedMeasurement(
          new TextMetrics(java.util.List.of(line), line.width(), 16, 16, fontMetrics),
          java.util.List.of(new FinalLineCaretStops(boundaries, advances)));
    }

    @Override public TextMetrics measureText(String text, Font font, float size, float height) {
      throw new AssertionError("unexpected TextMeasurer entry point");
    }
    @Override public TextMetrics measureText(String text, float offset, Font font, float size,
        float height, float width, boolean wrap) {
      throw new AssertionError("unexpected TextMeasurer entry point");
    }
    @Override public TextMetrics getTextMetrics(String text, float offset, Font font, float size,
        float height, float width, boolean wrap) {
      throw new AssertionError("unexpected TextMeasurer entry point");
    }
    @Override public TextLineMetrics getTextLineMetrics(
        String text, Font font, float size, float height) {
      throw new AssertionError("unexpected TextMeasurer entry point");
    }
    @Override public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float size, float offset) {
      throw new AssertionError("unexpected TextMeasurer entry point");
    }
  }
}
