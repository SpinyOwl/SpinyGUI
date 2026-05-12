package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgColorUtil.create;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
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
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;

public class NvgTextRenderer {
  private final Map<String, String> loadedFontFaces = new HashMap<>();
  private final Map<String, ByteBuffer> fontBuffers = new HashMap<>();

  public void render(Node node, long nanovg) {
    Text text = node.asText();
    if (text.inlineFragments().isEmpty()) {
      return;
    }

    createScissor(nanovg, node);

    nvgSave(nanovg);
    nvgTextAlign(nanovg, NVG_ALIGN_LEFT | NVG_ALIGN_BASELINE);
    Vector2f offset =
        text.offsetParent() == null ? new Vector2f() : text.offsetParent().absolutePosition();
    for (InlineFragment fragment : text.inlineFragments()) {
      renderFragment(fragment, nanovg, offset);
    }
    nvgRestore(nanovg);

    resetScissor(nanovg);
  }

  private void renderFragment(InlineFragment fragment, long nanovg, Vector2f offset) {
    if (!fragment.textFragment()) {
      return;
    }
    String fontFace = fontFace(fragment.font(), nanovg);
    if (fontFace == null) {
      return;
    }
    nvgFontFace(nanovg, fontFace);
    nvgFontSize(nanovg, fragment.fontSize());
    try (var color = create(fragment.color())) {
      nvgFillColor(nanovg, color);
      ByteBuffer textBuffer = memUTF8(fragment.text(), false);
      try {
        nvgText(nanovg, offset.x + fragment.x(), offset.y + fragment.baseline(), textBuffer);
      } finally {
        memFree(textBuffer);
      }
    }
  }

  private String fontFace(Font font, long nanovg) {
    String key =
        font.fontFamily()
            + "|"
            + font.style()
            + "|"
            + font.weight()
            + "|"
            + font.stretch()
            + "|"
            + font.path();
    if (loadedFontFaces.containsKey(key)) {
      return loadedFontFaces.get(key);
    }

    ByteBuffer fontBuffer = fontBuffers.computeIfAbsent(font.path(), IOUtil::resourceAsByteBuffer);
    if (fontBuffer == null) {
      return null;
    }

    String fontFace = font.fontFamily() + "-" + Integer.toUnsignedString(key.hashCode());
    int id = nvgCreateFontMem(nanovg, fontFace, fontBuffer.duplicate(), false);
    if (id == -1) {
      return null;
    }

    loadedFontFaces.put(key, fontFace);
    return fontFace;
  }
}
