package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
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

    assertEquals(List.of("caret(7,abc,3.0,5.0,9.0,8.0)"), caretSink.calls());
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

  private InlineFragment fragment(String text, float x, float y, float width, float height) {
    return InlineFragment.builder().text(text).x(x).y(y).width(width).height(height).build();
  }

  private static final class RecordingCaretSink implements NvgDebugRenderer.CaretSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void drawCaret(
        long context, InlineFragment fragment, float x, float y, float mouseX, float mouseY) {
      calls.add(
          "caret(%d,%s,%.1f,%.1f,%.1f,%.1f)"
              .formatted(context, fragment.text(), x, y, mouseX, mouseY));
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
}
