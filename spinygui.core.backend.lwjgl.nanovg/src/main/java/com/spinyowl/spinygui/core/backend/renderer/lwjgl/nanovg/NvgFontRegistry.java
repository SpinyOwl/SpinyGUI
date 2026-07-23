package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.stb.STBTruetype.stbtt_FindGlyphIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.stb.STBTTFontinfo;

class NvgFontRegistry {
  private static final int REPLACEMENT_CODE_POINT = 0xFFFD;

  private final Map<String, FontFace> loadedFontFaces = new HashMap<>();
  private final Map<String, ByteBuffer> fontBuffers = new HashMap<>();
  private final Map<String, STBTTFontinfo> fontInfos = new HashMap<>();

  String fontFace(Font font, long nanovg) {
    String key = fontKey(font);
    FontFace face = loadedFontFaces.get(key);
    if (face == null) {
      ByteBuffer fontBuffer = fontBuffers.computeIfAbsent(font.path(), IOUtil::resourceAsByteBuffer);
      if (fontBuffer == null) {
        return null;
      }

      String fontFace = font.fontFamily() + "-" + Integer.toUnsignedString(key.hashCode());
      int id = nvgCreateFontMem(nanovg, fontFace, fontBuffer.duplicate(), false);
      if (id == -1) {
        return null;
      }

      face = new FontFace(fontFace, id);
      loadedFontFaces.put(key, face);
    }
    return face.name();
  }

  String displayText(Font primaryFont, String text) {
    StringBuilder displayText = new StringBuilder(text.length());
    text.codePoints()
        .forEach(
            codePoint -> {
              if (Character.isISOControl(codePoint) || hasGlyph(primaryFont, codePoint)) {
                displayText.appendCodePoint(codePoint);
              } else {
                displayText.appendCodePoint(REPLACEMENT_CODE_POINT);
              }
            });
    return displayText.toString();
  }

  private boolean hasGlyph(Font primaryFont, int codePoint) {
    return glyphIndex(primaryFont, codePoint) != 0;
  }

  private int glyphIndex(Font font, int codePoint) {
    return stbtt_FindGlyphIndex(
        fontInfos.computeIfAbsent(font.path(), this::loadFontInfo), codePoint);
  }

  private STBTTFontinfo loadFontInfo(String path) {
    ByteBuffer fontBuffer = fontBuffers.computeIfAbsent(path, IOUtil::resourceAsByteBuffer);
    STBTTFontinfo fontInfo = STBTTFontinfo.create();
    if (fontBuffer == null || !stbtt_InitFont(fontInfo, fontBuffer.duplicate())) {
      throw new IllegalStateException("Failed to load font from '%s'".formatted(path));
    }
    return fontInfo;
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

  private record FontFace(String name, int id) {}
}
