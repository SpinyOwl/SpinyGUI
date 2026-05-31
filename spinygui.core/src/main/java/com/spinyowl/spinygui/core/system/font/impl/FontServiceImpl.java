package com.spinyowl.spinygui.core.system.font.impl;

import static org.lwjgl.stb.STBTruetype.STBTT_MS_EID_UNICODE_BMP;
import static org.lwjgl.stb.STBTruetype.STBTT_MS_LANG_ENGLISH;
import static org.lwjgl.stb.STBTruetype.STBTT_PLATFORM_ID_MICROSOFT;
import static org.lwjgl.stb.STBTruetype.stbtt_FindGlyphIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontNameString;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForMappingEmToPixels;
import static org.slf4j.LoggerFactory.getLogger;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.FontLoadingException;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.FontStorage;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class FontServiceImpl implements FontService, TextMeasurer {
  private static final Logger LOG = getLogger(FontServiceImpl.class);

  private static final String SUBINDEX_SPLIT_REGEX = "\\s+";
  private static final String SUBFEATURE_SPLIT_REGEX = "(?=\\p{Upper})";
  private static final int FONT_FAMILY_INDEX = 1;
  private static final int FONT_SUBFAMILY_INDEX = 2;
  private static final int TYPOGRAPHIC_FONT_FAMILY_INDEX = 16;
  private static final int TYPOGRAPHIC_FONT_SUBFAMILY_INDEX = 17;

  @NonNull private final FontStorage fontStorage;
  private final boolean roundToPixel;
  private final Map<String, STBTTFontinfo> fontInfoMap = new ConcurrentHashMap<>();

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("squid:S3776")
  public Font loadFont(String path) throws FontLoadingException {
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

  @Override
  public TextMetrics measureText(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
    return measureText(text, 0, font, fontSize, lineHeight, Float.MAX_VALUE, false);
  }

  @Override
  public TextMetrics measureText(
      @NonNull String text,
      float offsetX,
      @NonNull Font font,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    if (maxWidth < 0.1) {
      return emptyTextMetrics(font, fontSize, lineHeight);
    }

    STBTTFontinfo fontInfo = getFontInfo(font.path());
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pCodePoint = stack.mallocInt(1);
      IntBuffer pAdvance = stack.mallocInt(1);
      float scaleFactor = stbtt_ScaleForMappingEmToPixels(fontInfo, fontSize);
      FontMetrics fontMetrics = measureFontMetrics(fontInfo, fontSize, lineHeight);
      int length = text.length();
      List<TextLineMetrics> lines = new ArrayList<>();
      float currentWidth = offsetX;
      float maxLineWidth = 0;
      int lineCount = 0;
      int lineStart = 0;
      int lastSpace = -1;
      int lastSpaceEnd = -1;
      float lastSpaceWidth = 0;
      int previousGlyphIndex = -1;
      int i = 0;
      while (i < length) {
        int charStart = i;
        int codePointSize = getCodePointSize(text, length, i, pCodePoint);
        int codePoint = pCodePoint.get(0);
        int charEnd = i + codePointSize;

        if (codePoint == '\n') {
          maxLineWidth =
              addLine(lines, text, lineStart, charStart, currentWidth, fontMetrics, maxLineWidth);
          lineCount++;
          lineStart = charEnd;
          currentWidth = 0;
          lastSpace = -1;
          lastSpaceEnd = -1;
          lastSpaceWidth = 0;
          previousGlyphIndex = -1;
          i = charEnd;
          continue;
        }

        int glyphIndex = stbtt_FindGlyphIndex(fontInfo, codePoint);
        float charWidth =
            measureNanoVgGlyphAdvance(
                fontInfo, glyphIndex, previousGlyphIndex, pAdvance, scaleFactor);

        if (currentWidth + charWidth > maxWidth && lineStart < charStart) {
          int lineEnd = charStart;
          int nextLineStart = charStart;
          float measuredWidth = currentWidth;
          if (wordWrap && lastSpace >= lineStart) {
            lineEnd = lastSpace;
            nextLineStart = lastSpaceEnd;
            measuredWidth = lastSpaceWidth;
          }
          maxLineWidth =
              addLine(lines, text, lineStart, lineEnd, measuredWidth, fontMetrics, maxLineWidth);
          lineCount++;
          lineStart = nextLineStart;
          currentWidth = 0;
          lastSpace = -1;
          lastSpaceEnd = -1;
          lastSpaceWidth = 0;
          previousGlyphIndex = -1;
          i = nextLineStart;
          continue;
        }

        if (Character.isSpaceChar(codePoint)) {
          lastSpace = charStart;
          lastSpaceEnd = charEnd;
          lastSpaceWidth = currentWidth;
        }
        currentWidth += charWidth;
        previousGlyphIndex = glyphIndex;
        i = charEnd;
      }
      maxLineWidth = addLine(lines, text, lineStart, length, currentWidth, fontMetrics, maxLineWidth);
      lineCount++;

      return TextMetrics.builder()
          .lines(lines)
          .width(maxLineWidth)
          .height(lineCount * fontMetrics.lineHeight())
          .lineHeight(fontMetrics.lineHeight())
          .fontMetrics(fontMetrics)
          .build();
    }
  }

  @Override
  public TextMetrics getTextMetrics(
      @NonNull String text,
      float offsetX,
      @NonNull Font font,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    return measureText(text, offsetX, font, fontSize, lineHeight, maxWidth, wordWrap);
  }

  @Override
  public FontMetrics getFontMetrics(@NonNull Font font, float fontSize, float lineHeight) {
    return measureText("", font, fontSize, lineHeight).fontMetrics();
  }

  @Override
  public TextLineMetrics getTextLineMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
    return measureText(text, font, fontSize, lineHeight).lines().get(0);
  }

  private TextMetrics emptyTextMetrics(Font font, float fontSize, float lineHeight) {
    FontMetrics fontMetrics = measureFontMetrics(getFontInfo(font.path()), fontSize, lineHeight);
    return TextMetrics.builder()
        .width(0)
        .height(0)
        .lineHeight(fontMetrics.lineHeight())
        .fontMetrics(fontMetrics)
        .build();
  }

  private FontMetrics measureFontMetrics(STBTTFontinfo fontInfo, float fontSize, float lineHeight) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer ascent = stack.mallocInt(1);
      IntBuffer descent = stack.mallocInt(1);
      IntBuffer lineGap = stack.mallocInt(1);
      stbtt_GetFontVMetrics(fontInfo, ascent, descent, lineGap);

      float scaleFactor = stbtt_ScaleForMappingEmToPixels(fontInfo, fontSize);
      float requestedLineHeight = fontSize * lineHeight;
      float metricsAscent = ascent.get(0) * scaleFactor;
      float metricsDescent = Math.abs(descent.get(0) * scaleFactor);
      float metricsLineGap = Math.max(0, lineGap.get(0) * scaleFactor);
      float measuredLineHeight = Math.max(requestedLineHeight, metricsAscent + metricsDescent + metricsLineGap);
      if (roundToPixel) {
        metricsAscent = Math.round(metricsAscent);
        metricsDescent = Math.round(metricsDescent);
        metricsLineGap = Math.round(metricsLineGap);
        measuredLineHeight = Math.round(measuredLineHeight);
      }
      return new FontMetrics(
          metricsAscent, metricsDescent, metricsLineGap, measuredLineHeight, metricsAscent);
    }
  }

  private float addLine(
      List<TextLineMetrics> lines,
      String text,
      int startIndex,
      int endIndex,
      float width,
      FontMetrics fontMetrics,
      float currentMaxWidth) {
    int safeStart = Math.max(0, Math.min(startIndex, text.length()));
    int safeEnd = Math.max(safeStart, Math.min(endIndex, text.length()));
    lines.add(
        TextLineMetrics.builder()
            .characters(text.subSequence(safeStart, safeEnd))
            .startIndex(safeStart)
            .endIndex(safeEnd)
            .charCount(safeEnd - safeStart)
            .width(width)
            .height(fontMetrics.lineHeight())
            .baseline(fontMetrics.baseline())
            .fontMetrics(fontMetrics)
            .build());
    return Math.max(currentMaxWidth, width);
  }

  private float measureNanoVgGlyphAdvance(
      STBTTFontinfo fontInfo,
      int glyphIndex,
      int previousGlyphIndex,
      IntBuffer pAdvance,
      float scaleFactor) {
    float width = 0;
    if (previousGlyphIndex != -1) {
      width += (int) (stbtt_GetGlyphKernAdvance(fontInfo, previousGlyphIndex, glyphIndex) * scaleFactor + 0.5f);
    }
    stbtt_GetGlyphHMetrics(fontInfo, glyphIndex, pAdvance, null);
    short xAdvance = (short) (scaleFactor * pAdvance.get(0) * 10.0f);
    return width + (int) (xAdvance / 10.0f + 0.5f);
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

  private STBTTFontinfo getFontInfo(String fontPath) throws FontLoadingException {
    return fontInfoMap.computeIfAbsent(fontPath, this::createFontInfo);
  }

  private STBTTFontinfo createFontInfo(String fontPath) throws FontLoadingException {
    ByteBuffer fontData = fontStorage.getFontData(fontPath);
    STBTTFontinfo stbttFontinfo = STBTTFontinfo.create();
    if (fontData == null || !STBTruetype.stbtt_InitFont(stbttFontinfo, fontData)) {
      throw new FontLoadingException("Failed to load font from '%s'".formatted(fontPath));
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
}
