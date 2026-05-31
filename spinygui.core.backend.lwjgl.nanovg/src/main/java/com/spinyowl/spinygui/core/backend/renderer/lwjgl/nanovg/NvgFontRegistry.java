package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

class NvgFontRegistry {
  private final Map<String, String> loadedFontFaces = new HashMap<>();
  private final Map<String, ByteBuffer> fontBuffers = new HashMap<>();

  String fontFace(Font font, long nanovg) {
    String key = fontKey(font);
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

  private String fontKey(Font font) {
    return font.fontFamily()
        + "|"
        + font.style()
        + "|"
        + font.weight()
        + "|"
        + font.stretch()
        + "|"
        + font.path();
  }
}
