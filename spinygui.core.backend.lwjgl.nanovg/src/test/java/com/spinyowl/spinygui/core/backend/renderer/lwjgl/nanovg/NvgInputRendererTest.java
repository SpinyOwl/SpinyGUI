package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class NvgInputRendererTest {

  @Test
  void render_whenTextInputIsFocused_drawsValueAndCaretClippedToContentBox() {
    RecordingStateSink stateSink = new RecordingStateSink();
    RecordingSelectionSink selectionSink = new RecordingSelectionSink();
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(stateSink, selectionSink, textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());

    InputElement input = input("abcd");
    input.caretIndex(2);
    input.focused(true);

    renderer.render(input, 9);

    assertEquals(List.of("begin(9,20.0,30.0,60.0,20.0)", "end(9)"), stateSink.calls());
    assertEquals(List.of(), selectionSink.calls());
    assertEquals(List.of("text(9,abcd,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of("caret(9,40.0,32.0,16.0)"), caretSink.calls());
  }

  @Test
  void render_whenTextInputIsNotFocused_skipsCaret() {
    RecordingStateSink stateSink = new RecordingStateSink();
    RecordingSelectionSink selectionSink = new RecordingSelectionSink();
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(stateSink, selectionSink, textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());

    renderer.render(input("abc"), 5);

    assertEquals(List.of("text(5,abc,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of(), selectionSink.calls());
    assertEquals(List.of(), caretSink.calls());
  }

  @Test
  void render_whenFocusedTextInputIsEmpty_drawsCaretAtContentStart() {
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(), new RecordingSelectionSink(), textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());

    InputElement input = input("");
    input.focused(true);

    renderer.render(input, 3);

    assertEquals(List.of("text(3,,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of("caret(3,20.0,32.0,16.0)"), caretSink.calls());
  }

  @Test
  void render_whenInputHasHorizontalScroll_offsetsTextAndCaret() {
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(), new RecordingSelectionSink(), textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());
    InputElement input = input("abcdef");
    input.focused(true);
    input.caretIndex(4);
    input.textScrollLeft(15);

    renderer.render(input, 3);

    assertEquals(List.of("text(3,abcdef,5.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of("caret(3,45.0,32.0,16.0)"), caretSink.calls());
  }

  @Test
  void render_whenInputHasSelection_drawsSelectionBeforeText() {
    RecordingSelectionSink selectionSink = new RecordingSelectionSink();
    RecordingTextSink textSink = new RecordingTextSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(), selectionSink, textSink, new RecordingCaretSink());
    renderer.textMeasurer(new FixedTextMeasurer());
    InputElement input = input("abcdef");
    input.select(1, 4);

    renderer.render(input, 8);

    assertEquals(List.of("selection(8,30.0,32.0,30.0,16.0)"), selectionSink.calls());
    assertEquals(List.of("text(8,abcdef,20.0,44.0,16.0)"), textSink.calls());
  }

  @Test
  void render_whenTextMeasurerIsMissing_skipsInputText() {
    RecordingTextSink textSink = new RecordingTextSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(),
            new RecordingSelectionSink(),
            textSink,
            new RecordingCaretSink());

    renderer.render(input("abc"), 1);

    assertEquals(List.of(), textSink.calls());
  }

  private InputElement input(String value) {
    InputElement input = new InputElement();
    input.value(value);
    input.box().contentPosition(20, 30);
    input.box().contentSize(60, 20);
    input.resolvedStyle().fontFamilies(Set.of(Font.DEFAULT.fontFamily()));
    input.resolvedStyle().fontSize(Length.pixel(16));
    input.resolvedStyle().lineHeight(1f);
    input.resolvedStyle().color(Color.BLACK);
    return input;
  }

  private static final class RecordingStateSink implements NvgInputRenderer.InputStateSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void begin(
        long context, InputElement input, Vector2f contentPosition, Vector2f contentSize) {
      calls.add(
          "begin(%d,%.1f,%.1f,%.1f,%.1f)"
              .formatted(
                  context,
                  contentPosition.x(),
                  contentPosition.y(),
                  contentSize.x(),
                  contentSize.y()));
    }

    @Override
    public void end(long context) {
      calls.add("end(%d)".formatted(context));
    }

    List<String> calls() {
      return calls;
    }
  }

  private static final class RecordingTextSink implements NvgInputRenderer.InputTextSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void drawText(
        long context,
        String text,
        Font font,
        float fontSize,
        Color color,
        float x,
        float baseline) {
      calls.add("text(%d,%s,%.1f,%.1f,%.1f)".formatted(context, text, x, baseline, fontSize));
    }

    List<String> calls() {
      return calls;
    }
  }

  private static final class RecordingSelectionSink implements NvgInputRenderer.InputSelectionSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void drawSelection(long context, float x, float y, float width, float height) {
      calls.add(
          "selection(%d,%.1f,%.1f,%.1f,%.1f)".formatted(context, x, y, width, height));
    }

    List<String> calls() {
      return calls;
    }
  }

  private static final class RecordingCaretSink implements NvgInputRenderer.InputCaretSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void drawCaret(long context, float x, float y, float height) {
      calls.add("caret(%d,%.1f,%.1f,%.1f)".formatted(context, x, y, height));
    }

    List<String> calls() {
      return calls;
    }
  }

  private static final class FixedTextMeasurer implements TextMeasurer {
    private static final float CHAR_WIDTH = 10;

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
          .width(text.length() * CHAR_WIDTH)
          .height(16)
          .baseline(12)
          .fontMetrics(new FontMetrics(12, 4, 0, 16, 12))
          .build();
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      throw new UnsupportedOperationException();
    }
  }
}
