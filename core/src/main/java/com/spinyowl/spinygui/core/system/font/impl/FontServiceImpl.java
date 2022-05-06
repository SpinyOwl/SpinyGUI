package com.spinyowl.spinygui.core.system.font.impl;

import static org.lwjgl.stb.STBTruetype.STBTT_MS_EID_UNICODE_BMP;
import static org.lwjgl.stb.STBTruetype.STBTT_MS_LANG_ENGLISH;
import static org.lwjgl.stb.STBTruetype.STBTT_PLATFORM_ID_MICROSOFT;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontNameString;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForMappingEmToPixels;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.slf4j.LoggerFactory.getLogger;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

public class FontServiceImpl implements FontService {
  private static final Logger LOG = getLogger(FontServiceImpl.class);

  private static final String SUBINDEX_SPLIT_REGEX = "\\s+";
  private static final String SUBFEATURE_SPLIT_REGEX = "(?=\\p{Upper})";
  private static final int FONT_FAMILY_INDEX = 1;
  private static final int FONT_SUBFAMILY_INDEX = 2;
  private static final int TYPOGRAPHIC_FONT_FAMILY_INDEX = 16;
  private static final int TYPOGRAPHIC_FONT_SUBFAMILY_INDEX = 17;

  private final Map<String, STBTTFontinfo> fontInfoMap = new ConcurrentHashMap<>();
  private final Map<String, ByteBuffer> dataMap = new ConcurrentHashMap<>();

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("squid:S3776")
  public Font loadFont(String path) {
    STBTTFontinfo fontInfo = getFontInfo(path);
    String fontFamily = getFontFamily(fontInfo);
    String subfamily = getSubfamily(fontInfo);

    // split subfamily by capital letter and trim spaces
    String[] fontFeatures = subfamily.split(SUBINDEX_SPLIT_REGEX);

    FontStyle fontStyle = FontStyle.NORMAL;
    FontWeight fontWeight = FontWeight.NORMAL;
    FontStretch fontStretch = FontStretch.NORMAL;
    for (String f : fontFeatures) {
      String fontFeature = f.trim();
      if (FontStyle.contains(fontFeature)) {
        fontStyle = FontStyle.find(fontFeature);
      } else if (FontStretch.contains(fontFeature)) {
        fontStretch = FontStretch.find(fontFeature);
      } else if (FontWeight.contains(fontFeature)) {
        fontWeight = FontWeight.find(fontFeature);
      } else {
        String[] subFeatures = fontFeature.split(SUBFEATURE_SPLIT_REGEX);
        for (String sf : subFeatures) {
          String sff = sf.trim();
          if (FontStyle.contains(sff)) {
            fontStyle = FontStyle.find(sff);
          } else if (FontStretch.contains(sff)) {
            fontStretch = FontStretch.find(sff);
          } else if (FontWeight.contains(sff)) {
            fontWeight = FontWeight.find(sff);
          }
        }
      }
    }

    if (LOG.isInfoEnabled()) {
      LOG.info(
          "Font [ {} | {} ] loaded successfully from '{}'",
          fontFamily,
          Arrays.toString(fontFeatures),
          path);
    }
    return new Font(fontFamily, fontStyle, fontStretch, fontWeight, path);
  }

  @Override
  public boolean isFontAvailable(@NonNull Font font) {
    return fontInfoMap.containsKey(font.path());
  }

  private String getSubfamily(STBTTFontinfo fontInfo) {
    String typographicSubfamily = getInfo(fontInfo, TYPOGRAPHIC_FONT_SUBFAMILY_INDEX);
    return typographicSubfamily.isBlank()
        ? getInfo(fontInfo, FONT_SUBFAMILY_INDEX)
        : typographicSubfamily;
  }

  private String getFontFamily(STBTTFontinfo fontInfo) {
    String typographicFontFamily = getInfo(fontInfo, TYPOGRAPHIC_FONT_FAMILY_INDEX);
    return typographicFontFamily.isBlank()
        ? getInfo(fontInfo, FONT_FAMILY_INDEX)
        : typographicFontFamily;
  }

  public TextMetrics getTextMetrics(
      @NonNull String text,
      float offsetX,
      @NonNull Font font,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    if (maxWidth < 0.1) {
      TextMetrics.builder().height(0).fullLineHeight(0).build();
    }

    STBTTFontinfo fontInfo = getFontInfo(font.path());
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pCodePoint = stack.mallocInt(1);
      IntBuffer pAdvance = stack.mallocInt(1);
      IntBuffer pLeftSideBearing = stack.mallocInt(1);

      float scaleFactor = stbtt_ScaleForMappingEmToPixels(fontInfo, fontSize);

      int textLength = text.length();
      float lineWidth = offsetX;

      float fullLineHeight = lineHeight * fontSize;
      TextLineMetrics.TextLineMetricsBuilder textLineMetrics =
          TextLineMetrics.builder().height(fullLineHeight);

      int lastSpace = -1;
      float lastSpaceWidth = 0;

      int lineStart = 0;
      int lineEnd = 0;

      int i = 0;
      int lineCount = 1;
      TextMetrics.TextMetricsBuilder textMetrics = TextMetrics.builder();
      while (i < textLength) {
        int newLineStart = i + 1;
        // get codepoint
        int codePointSize = getCodePointSize(text, textLength, i, pCodePoint);
        int codePoint = pCodePoint.get(0);
        if (Character.isSpaceChar(codePoint)) {
          lastSpace = i;
          lastSpaceWidth = lineWidth;
        }
        i += codePointSize;

        // get char width
        stbtt_GetCodepointHMetrics(fontInfo, codePoint, pAdvance, pLeftSideBearing);
        float charWidth = pAdvance.get(0) * scaleFactor;

        if (lineWidth + charWidth > maxWidth) {
          lineEnd = wordWrap && lastSpace >= lineStart ? lastSpace + 1 : newLineStart;
          textMetrics.line(
              textLineMetrics
                  .characters(text.subSequence(lineStart, lineEnd))
                  .height(fullLineHeight)
                  .width(lineWidth)
                  .build());

          lineCount++;
          textLineMetrics =
              TextLineMetrics.builder().height(fullLineHeight).width(lineWidth - lastSpaceWidth);

          lineStart = lineEnd;
          lineWidth = lineWidth - lastSpaceWidth;
          lastSpaceWidth = 0;
        }

        lineWidth += charWidth;
      }
      textMetrics.line(
          textLineMetrics
              .characters(text.subSequence(lineStart, lineEnd))
              .height(fullLineHeight)
              .width(lineWidth)
              .build());
      textMetrics.height(lineCount * fullLineHeight).fullLineHeight(fullLineHeight);
      return textMetrics.build();
    }
  }

  @Override
  public TextLineMetrics getTextLineMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
    STBTTFontinfo fontInfo = getFontInfo(font.path());
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pCodePoint = stack.mallocInt(1);
      IntBuffer pAdvance = stack.mallocInt(1);
      IntBuffer pLeftSideBearing = stack.mallocInt(1);

      float scaleFactor = stbtt_ScaleForPixelHeight(fontInfo, fontSize);

      int textLength = text.length();

      float lineWidth = 0;
      int i = 0;
      while (i < textLength) {
        i += getCodePointSize(text, textLength, i, pCodePoint);
        int codePoint = pCodePoint.get(0);

        // get char width
        stbtt_GetCodepointHMetrics(fontInfo, codePoint, pAdvance, pLeftSideBearing);
        lineWidth += pAdvance.get(0) * scaleFactor;
      }
      return TextLineMetrics.builder()
          .height(fontSize * lineHeight)
          .width(lineWidth)
          .characters(text)
          .build();
    }
  }

  // obtains font info from the map or if map has no entry, creates it and adds it to the map
  private String getInfo(STBTTFontinfo stbttFontinfo, int i) {
    String info = "";
    ByteBuffer name =
        stbtt_GetFontNameString(
            stbttFontinfo,
            STBTT_PLATFORM_ID_MICROSOFT,
            STBTT_MS_EID_UNICODE_BMP,
            STBTT_MS_LANG_ENGLISH,
            i);
    if (name != null) {
      int capacity = name.capacity();
      byte[] bytes = new byte[capacity];
      name.get(bytes);
      info = new String(bytes, StandardCharsets.UTF_16);
    }
    return info;
  }

  private STBTTFontinfo getFontInfo(String fontPath) {
    return fontInfoMap.computeIfAbsent(fontPath, this::createFontInfo);
  }

  private STBTTFontinfo createFontInfo(String fontPath) {
    ByteBuffer fontData = IOUtil.resourceAsByteBuffer(fontPath);
    STBTTFontinfo stbttFontinfo = STBTTFontinfo.create();
    if (fontData == null || !STBTruetype.stbtt_InitFont(stbttFontinfo, fontData)) {
      throw new RuntimeException("Failed to load font from '%s'".formatted(fontPath));
    }

    for (int i = 0; i < 25; i++) {
      ByteBuffer name =
          stbtt_GetFontNameString(
              stbttFontinfo,
              STBTT_PLATFORM_ID_MICROSOFT,
              STBTT_MS_EID_UNICODE_BMP,
              STBTT_MS_LANG_ENGLISH,
              i);
      // bytebuffer to string
      if (name != null) {
        byte[] bytes = new byte[name.capacity()];
        name.get(bytes);
      }
    }

    dataMap.put(fontPath, fontData);
    return stbttFontinfo;
  }

  private int getCodePointSize(String text, int to, int i, IntBuffer cpOut) {
    char c1 = text.charAt(i);
    if (Character.isHighSurrogate(c1) && i + 1 < to) {
      char c2 = text.charAt(i + 1);
      if (Character.isLowSurrogate(c2)) {
        cpOut.put(0, Character.toCodePoint(c1, c2));
        return 2;
      }
    }
    cpOut.put(0, c1);
    return 1;
  }

  private interface Fic<T> {
    T apply(
        IntBuffer pCodePoint,
        IntBuffer pAdvance,
        IntBuffer pLeftSideBearing,
        STBTTFontinfo fontInfo,
        float scaleFactor,
        float lineHeight);
  }
}
