package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class NvgTextRendererTest {

  @Test
  void renderFragments_drawsTextFragmentsAtComputedOffsetPositions() {
    RecordingTextSink sink = new RecordingTextSink();
    NvgTextRenderer renderer = new NvgTextRenderer(sink);
    Text text = new Text("Horizontal auto");
    text.inlineFragments(
        List.of(
            fragment("Horizontal", 0, 8),
            fragment(" ", 100, 8),
            fragment("auto", 110, 8)));

    renderer.renderFragments(text, 9, new Vector2f(5, 7));

    assertEquals(
        List.of(
            "draw(9,Horizontal,5.0,15.0)",
            "draw(9, ,105.0,15.0)",
            "draw(9,auto,115.0,15.0)"),
        sink.calls());
  }

  @Test
  void renderFragments_skipsNonTextFragmentsWithoutChangingFollowingPositions() {
    RecordingTextSink sink = new RecordingTextSink();
    NvgTextRenderer renderer = new NvgTextRenderer(sink);
    Text text = new Text("a b");
    text.inlineFragments(
        List.of(
            fragment("a", 0, 8),
            fragment(null, 10, 8),
            fragment("", 20, 8),
            fragment(" ", 10, 8),
            fragment("b", 20, 8)));

    renderer.renderFragments(text, 3, new Vector2f(2, 4));

    assertEquals(
        List.of("draw(3,a,2.0,12.0)", "draw(3, ,12.0,12.0)", "draw(3,b,22.0,12.0)"),
        sink.calls());
  }

  private InlineFragment fragment(String text, float x, float baseline) {
    return InlineFragment.builder()
        .text(text)
        .x(x)
        .baseline(baseline)
        .font(Font.DEFAULT)
        .fontSize(16)
        .color(Color.BLACK)
        .build();
  }

  private static final class RecordingTextSink implements NvgTextRenderer.TextSink {

    private final List<String> calls = new ArrayList<>();

    @Override
    public void drawText(long context, InlineFragment fragment, float x, float baseline) {
      calls.add("draw(%d,%s,%.1f,%.1f)".formatted(context, fragment.text(), x, baseline));
    }

    List<String> calls() {
      return calls;
    }
  }
}
