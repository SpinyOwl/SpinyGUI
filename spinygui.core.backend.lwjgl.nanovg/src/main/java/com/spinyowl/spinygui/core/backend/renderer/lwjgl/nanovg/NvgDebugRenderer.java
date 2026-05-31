package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRectStroke;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextGlyphPositions;

import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.nanovg.NVGGlyphPosition;
import org.lwjgl.system.MemoryStack;

class NvgDebugRenderer {

  private static final Color INLINE_FRAGMENT_FILL = new Color(255, 193, 7, 0.22f);
  private static final Color INLINE_FRAGMENT_STROKE = new Color(245, 124, 0, 0.95f);
  private static final Color CARET_COLOR = new Color(33, 33, 33, 0.95f);
  private static final float CARET_WIDTH = 1.5f;
  private static final float STROKE_WIDTH = 1f;

  private final HighlightSink highlightSink;
  private final CaretSink caretSink;
  private final StateSink stateSink;

  NvgDebugRenderer() {
    this(new NanoVgHighlightSink(), new NanoVgCaretSink(new NvgFontRegistry()), new NanoVgStateSink());
  }

  NvgDebugRenderer(NvgFontRegistry fontRegistry) {
    this(new NanoVgHighlightSink(), new NanoVgCaretSink(fontRegistry), new NanoVgStateSink());
  }

  NvgDebugRenderer(HighlightSink highlightSink, CaretSink caretSink, StateSink stateSink) {
    this.highlightSink = highlightSink;
    this.caretSink = caretSink;
    this.stateSink = stateSink;
  }

  void render(Frame frame, long nanovgContext, Vector2fc mousePosition) {
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
    if (element.hovered() && !element.inlineFragments().isEmpty()) {
      renderFragments(
          nanovgContext, element, element.inlineFragments(), inlineFormattingOffset(element), mousePosition);
    }
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
    if (mousePosition != null && fragment.textFragment() && contains(fragment, x, y, mousePosition)) {
      caretSink.drawCaret(nanovgContext, fragment, x, y, mousePosition.x(), mousePosition.y());
    }
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
    void drawCaret(
        long context, InlineFragment fragment, float x, float y, float mouseX, float mouseY);
  }

  interface StateSink {
    void begin(long context, Node clipNode);

    void end(long context);
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

  private static final class NanoVgCaretSink implements CaretSink {
    private final NvgFontRegistry fontRegistry;

    private NanoVgCaretSink(NvgFontRegistry fontRegistry) {
      this.fontRegistry = fontRegistry;
    }

    @Override
    public void drawCaret(
        long context, InlineFragment fragment, float x, float y, float mouseX, float mouseY) {
      float caretX = caretX(context, fragment, x, mouseX);
      drawRect(context, new Vector2f(caretX, y), new Vector2f(CARET_WIDTH, fragment.height()), CARET_COLOR);
    }

    private float caretX(long context, InlineFragment fragment, float x, float mouseX) {
      String fontFace = fontRegistry.fontFace(fragment.font(), context);
      if (fontFace == null) {
        return approximateCaretX(fragment, x, mouseX);
      }
      nvgFontFace(context, fontFace);
      nvgFontSize(context, fragment.fontSize());
      nvgTextAlign(context, NVG_ALIGN_LEFT | NVG_ALIGN_BASELINE);

      try (MemoryStack stack = MemoryStack.stackPush()) {
        NVGGlyphPosition.Buffer positions = NVGGlyphPosition.malloc(fragment.text().length(), stack);
        int count = nvgTextGlyphPositions(context, x, fragment.baseline(), fragment.text(), positions);
        if (count <= 0) {
          return approximateCaretX(fragment, x, mouseX);
        }
        for (int i = 0; i < count; i++) {
          float currentX = positions.get(i).x();
          float nextX = i + 1 < count ? positions.get(i + 1).x() : x + fragment.width();
          if (mouseX < (currentX + nextX) / 2f) {
            return currentX;
          }
        }
      }
      return x + fragment.width();
    }

    private float approximateCaretX(InlineFragment fragment, float x, float mouseX) {
      int charCount = Math.max(1, fragment.text().length());
      float charWidth = fragment.width() / charCount;
      int caretIndex = Math.round((mouseX - x) / charWidth);
      caretIndex = Math.max(0, Math.min(charCount, caretIndex));
      return x + caretIndex * charWidth;
    }
  }

  private static final class NanoVgStateSink implements StateSink {
    @Override
    public void begin(long context, Node clipNode) {
      createScissor(context, clipNode);
      nvgSave(context);
    }

    @Override
    public void end(long context) {
      nvgRestore(context);
      resetScissor(context);
    }
  }
}
