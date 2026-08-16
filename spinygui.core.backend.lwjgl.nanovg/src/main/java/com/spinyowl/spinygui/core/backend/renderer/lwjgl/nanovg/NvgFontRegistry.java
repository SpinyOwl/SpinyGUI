package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.stb.STBTruetype.stbtt_FindGlyphIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.lwjgl.stb.STBTTFontinfo;

class NvgFontRegistry {
  private static final int REPLACEMENT_CODE_POINT = 0xFFFD;

  private final Map<String, FontFace> loadedFontFaces = new HashMap<>();
  private final Map<String, ByteBuffer> fontBuffers = new HashMap<>();
  private final Map<String, STBTTFontinfo> fontInfos = new HashMap<>();
  private final Set<SemanticFontOwner.FaceKey> activeSemanticFaces = new HashSet<>();
  private final NvgRenderer renderer;
  private final FaceCreator faceCreator;

  NvgFontRegistry() {
    this(null, FaceCreator.NATIVE);
  }

  NvgFontRegistry(NvgRenderer renderer, FaceCreator faceCreator) {
    this.renderer = renderer;
    this.faceCreator = Objects.requireNonNull(faceCreator, "faceCreator");
  }

  String fontFace(Font font, long nanovg) {
    if (renderer == null) {
      Font.semanticOwner().verifyUse();
    } else {
      renderer.requireFontFaceUse(nanovg);
    }
    String key = fontKey(font);
    FontFace face = loadedFontFaces.get(key);
    if (face == null) {
      ByteBuffer fontBuffer = fontBuffers.computeIfAbsent(font.path(), IOUtil::resourceAsByteBuffer);
      if (fontBuffer == null) {
        return null;
      }

      String fontFace = font.fontFamily() + "-" + Integer.toUnsignedString(key.hashCode());
      int id = faceCreator.create(nanovg, fontFace, fontBuffer.duplicate());
      if (id == -1) {
        return null;
      }

      face = new FontFace(fontFace, id);
      loadedFontFaces.put(key, face);
      activeSemanticFaces.add(faceKey(font));
    }
    return face.name();
  }

  void beforeReplacement(
      SemanticFontOwner.Identity previous, SemanticFontOwner.Identity replacement) {
    if (!previous.key().equals(replacement.key())) {
      throw new IllegalArgumentException("Semantic replacement preflight requires one face key");
    }
    if (activeSemanticFaces.contains(previous.key())) {
      throw new IllegalStateException(
          "Destroy the initialized NanoVG renderer before replacing an active font face");
    }
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

  private SemanticFontOwner.FaceKey faceKey(Font font) {
    return new SemanticFontOwner.FaceKey(
        font.fontFamily(),
        font.style().name(),
        font.weight().name(),
        font.stretch().name());
  }

  @FunctionalInterface
  interface FaceCreator {
    FaceCreator NATIVE =
        (context, name, bytes) -> nvgCreateFontMem(context, name, bytes, false);

    int create(long context, String name, ByteBuffer bytes);
  }

  private record FontFace(String name, int id) {}
}
