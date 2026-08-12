package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgClipStack;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class NvgDebugRendererTest {

  @Test
  void render_whenParentElementHovered_highlightsTextInlineFragments() {
    RecordingHighlightSink sink = new RecordingHighlightSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgDebugRenderer renderer = new NvgDebugRenderer(sink, caretSink, new NoOpStateSink());
    Frame frame = new Frame();
    Element parent = new Element("div");
    parent.hovered(true);
    Text text = new Text("abc");
    text.inlineFragments(List.of(fragment("abc", 3, 5, 20, 10)));
    frame.addChild(parent);
    parent.addChild(text);
    frame.layoutChildNodes(List.of(parent));
    parent.layoutChildNodes(List.of(text));

    renderer.render(frame, 7, null);

    assertEquals(List.of("highlight(7,3.0,5.0,20.0,10.0)"), sink.calls());
    assertEquals(List.of(), caretSink.calls());
  }

  @Test
  void render_whenElementNotHovered_skipsTextInlineFragments() {
    RecordingHighlightSink sink = new RecordingHighlightSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgDebugRenderer renderer = new NvgDebugRenderer(sink, caretSink, new NoOpStateSink());
    Frame frame = new Frame();
    Element parent = new Element("div");
    Text text = new Text("abc");
    text.inlineFragments(List.of(fragment("abc", 3, 5, 20, 10)));
    frame.addChild(parent);
    parent.addChild(text);
    frame.layoutChildNodes(List.of(parent));
    parent.layoutChildNodes(List.of(text));

    renderer.render(frame, 7, new Vector2f(5, 6));

    assertEquals(List.of(), sink.calls());
    assertEquals(List.of(), caretSink.calls());
  }

  @Test
  void render_whenInlineElementHovered_highlightsElementFragments() {
    RecordingHighlightSink sink = new RecordingHighlightSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgDebugRenderer renderer = new NvgDebugRenderer(sink, caretSink, new NoOpStateSink());
    Frame frame = new Frame();
    Element inline = new Element("span");
    inline.hovered(true);
    inline.inlineFragments(List.of(fragment(null, 10, 8, 30, 12)));
    frame.addChild(inline);
    frame.layoutChildNodes(List.of(inline));

    renderer.render(frame, 2, new Vector2f(12, 9));

    assertEquals(List.of("highlight(2,10.0,8.0,30.0,12.0)"), sink.calls());
    assertEquals(List.of(), caretSink.calls());
  }

  @Test
  void render_whenMouseInsideHoveredTextFragment_drawsCaret() {
    RecordingHighlightSink sink = new RecordingHighlightSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgDebugRenderer renderer = new NvgDebugRenderer(sink, caretSink, new NoOpStateSink());
    renderer.textMeasurer(new FixedCaretTextMeasurer());
    Frame frame = new Frame();
    Element parent = new Element("div");
    parent.hovered(true);
    Text text = new Text("abc");
    text.inlineFragments(List.of(fragment("abc", 3, 5, 20, 10)));
    frame.addChild(parent);
    parent.addChild(text);
    frame.layoutChildNodes(List.of(parent));
    parent.layoutChildNodes(List.of(text));

    renderer.render(frame, 7, new Vector2f(9, 8));

    assertEquals(List.of("caret(7,7.0,5.0,10.0)"), caretSink.calls());
  }

  @Test
  void render_whenMouseOutsideHoveredTextFragment_skipsCaret() {
    RecordingHighlightSink sink = new RecordingHighlightSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgDebugRenderer renderer = new NvgDebugRenderer(sink, caretSink, new NoOpStateSink());
    Frame frame = new Frame();
    Element parent = new Element("div");
    parent.hovered(true);
    Text text = new Text("abc");
    text.inlineFragments(List.of(fragment("abc", 3, 5, 20, 10)));
    frame.addChild(parent);
    parent.addChild(text);
    frame.layoutChildNodes(List.of(parent));
    parent.layoutChildNodes(List.of(text));

    renderer.render(frame, 7, new Vector2f(30, 8));

    assertEquals(List.of(), caretSink.calls());
  }

  @Test
  void render_whenTextInputHovered_highlightsInputValueFragment() {
    RecordingHighlightSink sink = new RecordingHighlightSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgDebugRenderer renderer = new NvgDebugRenderer(sink, caretSink, new NoOpStateSink());
    renderer.textMeasurer(new FixedCaretTextMeasurer());
    Frame frame = new Frame();
    InputElement input = input("abcd");
    input.hovered(true);
    frame.addChild(input);
    frame.layoutChildNodes(List.of(input));

    renderer.render(frame, 3, null);

    assertEquals(List.of("highlight(3,20.0,32.0,40.0,16.0)"), sink.calls());
    assertEquals(List.of(), caretSink.calls());
  }

  @Test
  void render_whenMouseInsideHoveredTextInputValue_drawsCaret() {
    RecordingHighlightSink sink = new RecordingHighlightSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgDebugRenderer renderer = new NvgDebugRenderer(sink, caretSink, new NoOpStateSink());
    renderer.textMeasurer(new FixedCaretTextMeasurer());
    Frame frame = new Frame();
    InputElement input = input("abcd");
    input.hovered(true);
    frame.addChild(input);
    frame.layoutChildNodes(List.of(input));

    renderer.render(frame, 3, new Vector2f(28, 35));

    assertEquals(List.of("caret(3,24.0,32.0,16.0)"), caretSink.calls());
  }

  @Test
  void enabledDebugStateCountsCommandsWithoutChangingDisabledCommandOrder() {
    Frame frame = clippedHoveredTextFrame();
    List<String> disabledCalls = new ArrayList<>();
    NvgDebugRenderer disabled = recordingDebugRenderer(DiagnosticSession.disabled(), disabledCalls);
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    List<String> enabledCalls = new ArrayList<>();
    NvgDebugRenderer enabled = recordingDebugRenderer(diagnostics, enabledCalls);

    disabled.render(frame, 17, null);
    enabled.render(frame, 17, null);

    assertEquals(List.of("scissor", "save", "highlight", "restore", "reset"), disabledCalls);
    assertEquals(disabledCalls, enabledCalls);
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.SCISSOR_CALLS));
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.SAVE_CALLS));
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.RESTORE_CALLS));
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.RESET_SCISSOR_CALLS));
  }

  private Frame clippedHoveredTextFrame() {
    Frame frame = new Frame();
    Element parent = new Element("div");
    parent.hovered(true);
    parent.resolvedStyle().overflowX(Overflow.HIDDEN);
    parent.resolvedStyle().overflowY(Overflow.HIDDEN);
    parent.box().contentPosition(10, 20);
    parent.box().contentSize(100, 80);
    Text text = new Text("abc");
    text.inlineFragments(List.of(fragment("abc", 3, 5, 20, 10)));
    frame.addChild(parent);
    parent.addChild(text);
    frame.layoutChildNodes(List.of(parent));
    parent.layoutChildNodes(List.of(text));
    text.offsetParent(parent);
    return frame;
  }

  private NvgDebugRenderer recordingDebugRenderer(
      DiagnosticSession diagnostics, List<String> calls) {
    NvgClipStack.ClipSink clipSink =
        new NvgClipStack.ClipSink() {
          @Override
          public void scissor(long context, float x, float y, float width, float height) {
            calls.add("scissor");
          }

          @Override
          public void intersectScissor(
              long context, float x, float y, float width, float height) {
            calls.add("intersect");
          }

          @Override
          public void reset(long context) {
            calls.add("reset");
          }
        };
    NvgDebugRenderer.NativeStateSink stateSink =
        new NvgDebugRenderer.NativeStateSink() {
          @Override
          public void save(long context) {
            calls.add("save");
          }

          @Override
          public void restore(long context) {
            calls.add("restore");
          }
        };
    return new NvgDebugRenderer(
        (context, x, y, width, height) -> calls.add("highlight"),
        (context, x, y, height) -> calls.add("caret"),
        new NvgDebugRenderer.NanoVgStateSink(diagnostics, clipSink, stateSink));
  }

  private InlineFragment fragment(String text, float x, float y, float width, float height) {
    return InlineFragment.builder()
        .text(text)
        .x(x)
        .y(y)
        .width(width)
        .height(height)
        .font(Font.DEFAULT)
        .fontSize(16)
        .build();
  }

  private InputElement input(String value) {
    InputElement input = new InputElement();
    input.value(value);
    input.box().contentPosition(20, 30);
    input.box().contentSize(60, 20);
    input.box().border().left(2);
    input.box().border().top(2);
    input.resolvedStyle().fontFamilies(List.of(Font.DEFAULT.fontFamily()));
    input.resolvedStyle().fontSize(Length.pixel(16));
    input.resolvedStyle().lineHeight(1f);
    return input;
  }

  private static final class RecordingCaretSink implements NvgDebugRenderer.CaretSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void drawCaret(long context, float x, float y, float height) {
      calls.add("caret(%d,%.1f,%.1f,%.1f)".formatted(context, x, y, height));
    }

    List<String> calls() {
      return calls;
    }
  }

  private static final class RecordingHighlightSink implements NvgDebugRenderer.HighlightSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void highlight(long context, float x, float y, float width, float height) {
      calls.add("highlight(%d,%.1f,%.1f,%.1f,%.1f)".formatted(context, x, y, width, height));
    }

    List<String> calls() {
      return calls;
    }
  }

  private static final class NoOpStateSink implements NvgDebugRenderer.StateSink {
    @Override
    public void begin(long context, Node clipNode) {}

    @Override
    public void end(long context) {}
  }

  private static final class FixedCaretTextMeasurer implements TextMeasurer {
    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      throw new UnsupportedOperationException();
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
      throw new UnsupportedOperationException();
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
      throw new UnsupportedOperationException();
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      return TextLineMetrics.builder()
          .characters(text)
          .width(text.length() * 10f)
          .height(16)
          .baseline(12)
          .fontMetrics(new FontMetrics(12, 4, 0, 16, 12))
          .build();
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, List<Font> fonts, float fontSize, float lineHeight) {
      return getTextLineMetrics(text, fonts.get(0), fontSize, lineHeight);
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      return new TextCaretMetrics(1, 4);
    }
  }
}
