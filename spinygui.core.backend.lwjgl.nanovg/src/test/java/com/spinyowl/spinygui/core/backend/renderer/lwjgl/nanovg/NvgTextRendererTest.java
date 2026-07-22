package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.animation.TransitionCoordinator;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.time.TimeService;
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

  @Test
  void renderFragments_drawsButtonTextContentThroughGenericTextPath() {
    RecordingTextSink sink = new RecordingTextSink();
    NvgTextRenderer renderer = new NvgTextRenderer(sink);
    ButtonElement button = new ButtonElement();
    button.box().contentPosition(20, 30);
    button.box().contentSize(90, 28);
    Text text = new Text("Save");
    button.addChild(text);
    text.inlineFragments(List.of(fragment("Save", 8, 14)));

    renderer.renderFragments(text, 4, renderer.inlineFormattingOffset(text));

    assertEquals(List.of("draw(4,Save,28.0,44.0)"), sink.calls());
  }

  @Test
  void renderFragments_usesTheOwningElementsPresentedTextColorAndOpacity() {
    RecordingColorTextSink sink = new RecordingColorTextSink();
    NvgTextRenderer renderer = new NvgTextRenderer(sink);
    ButtonElement button = new ButtonElement();
    button.resolvedStyle().color(Color.RED);
    button.resolvedStyle().opacity(1f);
    button.presentationState().setValue("color", Color.BLUE);
    button.presentationState().setValue("opacity", 0.5f);
    Text text = new Text("Save");
    button.addChild(text);
    text.inlineFragments(List.of(fragment("Save", 8, 14)));

    renderer.renderFragments(text, 4, new Vector2f());

    assertEquals(Color.BLUE.withA(0.5f), sink.colors());
  }

  @Test
  void renderFragments_usesFakeClockTransitionValuesAtMidpoint() {
    FakeClock clock = new FakeClock();
    TransitionCoordinator coordinator = new TransitionCoordinator(clock);
    var store = new DefaultPropertyStoreProvider().createPropertyStore();
    var manager = new StyleManagerImpl(store, StyleSheetParserFactory.createParser(store), coordinator);
    Frame frame = new Frame();
    Element element = new Element("div");
    Text text = new Text("Save");
    text.inlineFragments(List.of(fragment("Save", 0, 14)));
    element.addChild(text);
    frame.addChild(element);
    element.style("color: #000000; opacity: 1; transition: color 1s linear, opacity 1s linear");
    manager.recalculate(frame);
    coordinator.tick();
    element.style("color: #ffffff; opacity: 0; transition: color 1s linear, opacity 1s linear");
    manager.recalculate(frame);
    clock.time(0.5);
    coordinator.tick();

    RecordingColorTextSink sink = new RecordingColorTextSink();
    new NvgTextRenderer(sink).renderFragments(text, 1, new Vector2f());

    assertEquals(new Color(0.5f, 0.5f, 0.5f, 0.5f), sink.colors());
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

  private static final class RecordingColorTextSink implements NvgTextRenderer.TextSink {
    private Color color;

    @Override
    public void drawText(long context, InlineFragment fragment, float x, float baseline) {
      color = fragment.color();
    }

    Color colors() {
      return color;
    }
  }

  private static final class FakeClock implements TimeService {
    private double currentTime;

    @Override
    public double currentTime() {
      return currentTime;
    }

    private void time(double currentTime) {
      this.currentTime = currentTime;
    }
  }
}
