package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgColorUtil.create;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Display;
import java.nio.ByteBuffer;
import org.joml.Vector2f;

public class NvgTextRenderer {
  private final TextSink textSink;

  public NvgTextRenderer() {
    this(new NvgFontRegistry());
  }

  NvgTextRenderer(NvgFontRegistry fontRegistry) {
    this(new NanoVgTextSink(fontRegistry));
  }

  NvgTextRenderer(TextSink textSink) {
    this.textSink = textSink;
  }

  public void render(Node node, long nanovg) {
    Text text = node.asText();
    if (text.inlineFragments().isEmpty()) {
      return;
    }

    createScissor(nanovg, node);

    nvgSave(nanovg);
    nvgTextAlign(nanovg, NVG_ALIGN_LEFT | NVG_ALIGN_BASELINE);
    renderFragments(text, nanovg, inlineFormattingOffset(text));
    nvgRestore(nanovg);

    resetScissor(nanovg);
  }

  Vector2f inlineFormattingOffset(Text text) {
    Element parent = text.parent();
    while (parent != null && Display.INLINE.equals(parent.resolvedStyle().display())) {
      parent = parent.parent();
    }
    if (parent != null) {
      return parent.layoutAbsolutePosition().sub(parent.scrollLeft(), parent.scrollTop());
    }
    return text.offsetParent() == null
        ? new Vector2f()
        : text
            .offsetParent()
            .layoutAbsolutePosition()
            .sub(text.offsetParent().scrollLeft(), text.offsetParent().scrollTop());
  }

  void renderFragments(Text text, long nanovg, Vector2f offset) {
    for (InlineFragment fragment : text.inlineFragments()) {
      renderFragment(fragment, nanovg, offset);
    }
  }

  private void renderFragment(InlineFragment fragment, long nanovg, Vector2f offset) {
    if (!fragment.textFragment()) {
      return;
    }
    textSink.drawText(nanovg, fragment, offset.x + fragment.x(), offset.y + fragment.baseline());
  }

  interface TextSink {
    void drawText(long context, InlineFragment fragment, float x, float baseline);
  }

  private static final class NanoVgTextSink implements TextSink {
    private final NvgFontRegistry fontRegistry;

    private NanoVgTextSink(NvgFontRegistry fontRegistry) {
      this.fontRegistry = fontRegistry;
    }

    @Override
    public void drawText(long context, InlineFragment fragment, float x, float baseline) {
      String fontFace = fontRegistry.fontFace(fragment.font(), context);
      if (fontFace == null) {
        return;
      }
      nvgFontFace(context, fontFace);
      nvgFontSize(context, fragment.fontSize());
      try (var color = create(fragment.color())) {
        nvgFillColor(context, color);
        ByteBuffer textBuffer = memUTF8(fragment.text(), false);
        try {
          nvgText(context, x, baseline, textBuffer);
        } finally {
          memFree(textBuffer);
        }
      }
    }
  }
}
