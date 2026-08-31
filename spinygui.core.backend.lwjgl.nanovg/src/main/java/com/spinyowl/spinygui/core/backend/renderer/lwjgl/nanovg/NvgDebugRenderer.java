package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRectStroke;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgClipStack;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutService;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutSnapshot;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector2fc;

class NvgDebugRenderer implements NvgRenderer.DebugRenderer {

  private static final Color INLINE_FRAGMENT_FILL = new Color(255, 193, 7, 0.22f);
  private static final Color INLINE_FRAGMENT_STROKE = new Color(245, 124, 0, 0.95f);
  private static final Color CARET_COLOR = new Color(33, 33, 33, 0.95f);
  private static final float CARET_WIDTH = 1.5f;
  private static final float STROKE_WIDTH = 1f;

  private final HighlightSink highlightSink;
  private final CaretSink caretSink;
  private final StateSink stateSink;
  private TextMeasurer textMeasurer;
  private ControlTextLayoutService layoutService;

  NvgDebugRenderer() {
    this(DiagnosticSession.disabled());
  }

  NvgDebugRenderer(DiagnosticSession diagnostics) {
    this(
        new NanoVgHighlightSink(),
        new NanoVgCaretDrawingSink(),
        new NanoVgStateSink(diagnostics));
  }

  NvgDebugRenderer(HighlightSink highlightSink, CaretSink caretSink, StateSink stateSink) {
    this.highlightSink = highlightSink;
    this.caretSink = caretSink;
    this.stateSink = stateSink;
  }

  @Override
  public void textMeasurer(TextMeasurer textMeasurer) {
    this.textMeasurer = textMeasurer;
    this.layoutService = textMeasurer == null ? null : new ControlTextLayoutService(textMeasurer);
  }

  @Override
  public void layoutService(ControlTextLayoutService layoutService) {
    this.layoutService = layoutService;
    this.textMeasurer = layoutService == null ? null : layoutService.textMeasurer();
  }

  @Override
  public void render(Frame frame, long nanovgContext, Vector2fc mousePosition) {
    renderNode(frame, nanovgContext, mousePosition);
  }

  private void renderNode(Node node, long nanovgContext, Vector2fc mousePosition) {
    if (node instanceof Element element) {
      renderElement(element, nanovgContext, mousePosition);
      if (node.layoutChildNodes() != null) {
        node.layoutChildNodes().forEach(child -> renderNode(child, nanovgContext, mousePosition));
      }
    } else if (node instanceof Text text) {
      renderText(text, nanovgContext, mousePosition);
    }
  }

  private void renderElement(Element element, long nanovgContext, Vector2fc mousePosition) {
    if (element instanceof InputElement input) {
      renderInput(input, nanovgContext, mousePosition);
    }
    if (element.hovered() && !element.inlineFragments().isEmpty()) {
      renderFragments(
          nanovgContext,
          element,
          element.inlineFragments(),
          inlineFormattingOffset(element),
          mousePosition);
    }
  }

  private void renderInput(InputElement input, long nanovgContext, Vector2fc mousePosition) {
    if (!input.hovered() || !input.textInput() || layoutService == null) {
      return;
    }
    var fragment = inputTextFragment(input);
    renderFragments(nanovgContext, input, List.of(fragment), new Vector2f(), mousePosition);
  }

  private void renderText(Text text, long nanovgContext, Vector2fc mousePosition) {
    Element parent = text.parent();
    if (parent != null && parent.hovered() && !text.inlineFragments().isEmpty()) {
      renderFragments(
          nanovgContext, text, text.inlineFragments(), inlineFormattingOffset(text), mousePosition);
    }
  }

  private void renderFragments(
      long nanovgContext,
      Node clipNode,
      List<InlineFragment> fragments,
      Vector2f offset,
      Vector2fc mousePosition) {
    stateSink.begin(nanovgContext, clipNode);
    fragments.forEach(fragment -> renderFragment(nanovgContext, fragment, offset, mousePosition));
    stateSink.end(nanovgContext);
  }

  private void renderFragment(
      long nanovgContext, InlineFragment fragment, Vector2f offset, Vector2fc mousePosition) {
    if (fragment.width() <= 0 || fragment.height() <= 0) {
      return;
    }
    float x = offset.x + fragment.x();
    float y = offset.y + fragment.y();
    highlightSink.highlight(
        nanovgContext,
        x,
        y,
        fragment.width(),
        fragment.height());
    if (mousePosition != null
        && fragment.textFragment()
        && contains(fragment, x, y, mousePosition)) {
      drawCaret(nanovgContext, fragment, x, y, mousePosition.x());
    }
  }

  private void drawCaret(
      long nanovgContext, InlineFragment fragment, float x, float y, float mouseX) {
    if (textMeasurer == null || fragment.font() == null) {
      return;
    }
    if (fragment.node() instanceof InputElement input && layoutService != null) {
      float caretX = layoutService.query(input)
          .caretAt(Math.max(0, mouseX - x), 0, layoutService.diagnostics()).x();
      caretSink.drawCaret(nanovgContext, x + caretX, y, fragment.height());
      return;
    }
    List<Font> fonts =
        fragment.runs().stream().map(ResolvedTextRun::font).distinct().toList();
    TextCaretMetrics caret =
        textMeasurer.getTextCaretMetrics(
            fragment.text(),
            fonts.isEmpty() ? List.of(fragment.font()) : fonts,
            fragment.fontSize(),
            mouseX - x);
    caretSink.drawCaret(nanovgContext, x + caret.x(), y, fragment.height());
  }

  private InlineFragment inputTextFragment(InputElement input) {
    ControlTextLayoutSnapshot snapshot = layoutService.query(input);
    ControlTextLayoutSnapshot.Key key = snapshot.key();
    ControlTextLayoutSnapshot.Line line = snapshot.lines().get(0);
    ResolvedStyle style = input.resolvedStyle();
    Font font = key.resolvedFonts().isEmpty() ? Font.DEFAULT : key.resolvedFonts().get(0);
    float fontSize = key.fontSize();
    Vector2f contentPosition = contentPosition(input);
    Vector2f contentSize = input.box().contentSize();
    float lineTop = contentPosition.y() + Math.max(0, (contentSize.y() - line.height()) / 2f);
    float visibleWidth =
        Math.max(0, Math.min(line.width() - input.textScrollLeft(), contentSize.x()));
    return InlineFragment.builder()
        .node(input)
        .text(input.value())
        .x(contentPosition.x() - input.textScrollLeft())
        .y(lineTop)
        .width(visibleWidth)
        .height(line.height())
        .baseline(lineTop + line.baseline())
        .font(font)
        .fontSize(fontSize)
        .color(style.color())
        .runs(line.runs())
        .build();
  }

  private Vector2f contentPosition(InputElement input) {
    Vector2f position = input.absolutePosition();
    position.add(
        input.box().border().left() + input.box().padding().left(),
        input.box().border().top() + input.box().padding().top());
    return position;
  }

  Font findFont(ResolvedStyle style) {
    if (style.fontFamilies() == null) {
      return Font.DEFAULT;
    }
    return Font.semanticOwner().resolver()
        .resolve(
            style.fontFamilies(), style.fontStyle(), style.fontWeight(), FontStretch.NORMAL)
        .stream()
        .findFirst()
        .orElse(Font.DEFAULT);
  }

  private boolean contains(InlineFragment fragment, float x, float y, Vector2fc mousePosition) {
    return mousePosition.x() >= x
        && mousePosition.x() <= x + fragment.width()
        && mousePosition.y() >= y
        && mousePosition.y() <= y + fragment.height();
  }

  private Vector2f inlineFormattingOffset(Element element) {
    Element parent = element.parent();
    while (parent != null && Display.INLINE.equals(parent.resolvedStyle().display())) {
      parent = parent.parent();
    }
    return parent == null
        ? new Vector2f()
        : parent.absolutePosition().sub(parent.scrollLeft(), parent.scrollTop());
  }

  private Vector2f inlineFormattingOffset(Text text) {
    Element parent = text.parent();
    while (parent != null && Display.INLINE.equals(parent.resolvedStyle().display())) {
      parent = parent.parent();
    }
    if (parent != null) {
      return parent.absolutePosition().sub(parent.scrollLeft(), parent.scrollTop());
    }
    return text.offsetParent() == null
        ? new Vector2f()
        : text.offsetParent()
            .absolutePosition()
            .sub(text.offsetParent().scrollLeft(), text.offsetParent().scrollTop());
  }

  interface HighlightSink {
    void highlight(long context, float x, float y, float width, float height);
  }

  interface CaretSink {
    void drawCaret(long context, float x, float y, float height);
  }

  interface StateSink {
    void begin(long context, Node clipNode);

    void end(long context);
  }

  interface NativeStateSink {
    void save(long context);

    void restore(long context);
  }

  private static final class NanoVgHighlightSink implements HighlightSink {
    @Override
    public void highlight(long context, float x, float y, float width, float height) {
      var position = new Vector2f(x, y);
      var size = new Vector2f(width, height);
      drawRect(context, position, size, INLINE_FRAGMENT_FILL);
      drawRectStroke(context, position, size, INLINE_FRAGMENT_STROKE, STROKE_WIDTH);
    }
  }

  private static final class NanoVgCaretDrawingSink implements CaretSink {
    @Override
    public void drawCaret(long context, float x, float y, float height) {
      drawRect(context, new Vector2f(x, y), new Vector2f(CARET_WIDTH, height), CARET_COLOR);
    }
  }

  static final class NanoVgStateSink implements StateSink {
    private final DiagnosticSession diagnostics;
    private final NvgClipStack clipStack;
    private final NativeStateSink nativeStateSink;

    private NanoVgStateSink(DiagnosticSession diagnostics) {
      this(diagnostics, new NvgClipStack.NanoVgClipSink(), new NanoVgNativeStateSink());
    }

    NanoVgStateSink(
        DiagnosticSession diagnostics,
        NvgClipStack.ClipSink clipSink,
        NativeStateSink nativeStateSink) {
      this.diagnostics = diagnostics;
      this.clipStack = new NvgClipStack(clipSink, diagnostics);
      this.nativeStateSink = nativeStateSink;
    }

    @Override
    public void begin(long context, Node clipNode) {
      clipStack.create(context, clipNode);
      diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS);
      nativeStateSink.save(context);
    }

    @Override
    public void end(long context) {
      diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS);
      nativeStateSink.restore(context);
      clipStack.reset(context);
    }
  }

  private static final class NanoVgNativeStateSink implements NativeStateSink {
    @Override
    public void save(long context) {
      nvgSave(context);
    }

    @Override
    public void restore(long context) {
      nvgRestore(context);
    }
  }
}
