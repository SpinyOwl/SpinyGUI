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

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import com.spinyowl.spinygui.core.system.font.FontLoadingException;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.FontStorage;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

public class FontServiceImpl implements FontService, TextMeasurer {
  private static final Logger LOG = getLogger(FontServiceImpl.class);

  private static final String SUBINDEX_SPLIT_REGEX = "\\s+";
  private static final String SUBFEATURE_SPLIT_REGEX = "(?=\\p{Upper})";
  private static final int FONT_FAMILY_INDEX = 1;
  private static final int FONT_SUBFAMILY_INDEX = 2;
  private static final int TYPOGRAPHIC_FONT_FAMILY_INDEX = 16;
  private static final int TYPOGRAPHIC_FONT_SUBFAMILY_INDEX = 17;
  private static final int REPLACEMENT_CODE_POINT = 0xFFFD;

  @NonNull private final FontStorage fontStorage;
  private final boolean roundToPixel;
  @NonNull private final FontChainResolver fontChainResolver;
  @NonNull private final DiagnosticSession diagnostics;
  private final Map<String, STBTTFontinfo> fontInfoMap = new ConcurrentHashMap<>();

  public FontServiceImpl(@NonNull FontStorage fontStorage, boolean roundToPixel) {
    this(fontStorage, roundToPixel, FontChainResolver.DEFAULT, DiagnosticSession.disabled());
  }

  public FontServiceImpl(
      @NonNull FontStorage fontStorage,
      boolean roundToPixel,
      @NonNull FontChainResolver fontChainResolver) {
    this(fontStorage, roundToPixel, fontChainResolver, DiagnosticSession.disabled());
  }

  public FontServiceImpl(
      @NonNull FontStorage fontStorage,
      boolean roundToPixel,
      @NonNull FontChainResolver fontChainResolver,
      @NonNull DiagnosticSession diagnostics) {
    this.fontStorage = fontStorage;
    this.roundToPixel = roundToPixel;
    this.fontChainResolver = fontChainResolver;
    this.diagnostics = diagnostics;
  }

  @Override
  public DiagnosticSession diagnostics() {
    return diagnostics;
  }

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
    diagnostics.increment(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_ENTRIES);
    return measureText(text, 0, List.of(font), fontSize, lineHeight, Float.MAX_VALUE, false);
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
    diagnostics.increment(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES);
    return measureText(text, offsetX, List.of(font), fontSize, lineHeight, maxWidth, wordWrap);
  }

  @Override
  public TextMetrics measureText(
      @NonNull String text,
      float offsetX,
      @NonNull List<Font> fonts,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    diagnostics.increment(
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES);
    diagnostics.increment(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS);
    Font primaryFont = fonts.isEmpty() ? Font.DEFAULT : fonts.get(0);
    if (maxWidth < 0.1) {
      return emptyTextMetrics(primaryFont, fontSize, lineHeight);
    }

    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pCodePoint = stack.mallocInt(1);
      IntBuffer pAdvance = stack.mallocInt(1);
      FontMetrics fontMetrics = measureFontMetrics(getFontInfo(primaryFont.path()), fontSize, lineHeight);
      int length = text.length();
      List<TextLineMetrics> lines = new ArrayList<>();
      float currentWidth = offsetX;
      float maxLineWidth = 0;
      int lineCount = 0;
      int lineStart = 0;
      int lastSpace = -1;
      int lastSpaceEnd = -1;
      float lastSpaceWidth = 0;
      GlyphMeasurement previousGlyph = null;
      int i = 0;
      while (i < length) {
        diagnostics.increment(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED);
        int charStart = i;
        int codePointSize = getCodePointSize(text, length, i, pCodePoint);
        int codePoint = pCodePoint.get(0);
        int charEnd = i + codePointSize;

        if (codePoint == '\n') {
          maxLineWidth =
              addLine(lines, text, lineStart, charStart, currentWidth, fontMetrics, maxLineWidth, fonts, fontSize);
          lineCount++;
          lineStart = charEnd;
          currentWidth = 0;
          lastSpace = -1;
          lastSpaceEnd = -1;
          lastSpaceWidth = 0;
          previousGlyph = null;
          i = charEnd;
          continue;
        }

        GlyphMeasurement glyph = resolveGlyph(fonts, codePoint);
        float charWidth =
            measureNanoVgGlyphAdvance(glyph, previousGlyph, pAdvance, fontSize);

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
               addLine(lines, text, lineStart, lineEnd, measuredWidth, fontMetrics, maxLineWidth, fonts, fontSize);
          lineCount++;
          lineStart = nextLineStart;
          currentWidth = 0;
          lastSpace = -1;
          lastSpaceEnd = -1;
          lastSpaceWidth = 0;
          previousGlyph = null;
          i = nextLineStart;
          continue;
        }

        if (Character.isSpaceChar(codePoint)) {
          lastSpace = charStart;
          lastSpaceEnd = charEnd;
          lastSpaceWidth = currentWidth;
        }
        currentWidth += charWidth;
        previousGlyph = glyph;
        i = charEnd;
      }
      maxLineWidth = addLine(lines, text, lineStart, length, currentWidth, fontMetrics, maxLineWidth, fonts, fontSize);
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
    diagnostics.increment(TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_METRICS_FONT_ENTRIES);
    return measureText(text, offsetX, font, fontSize, lineHeight, maxWidth, wordWrap);
  }

  @Override
  public FontMetrics getFontMetrics(@NonNull Font font, float fontSize, float lineHeight) {
    return measureText("", font, fontSize, lineHeight).fontMetrics();
  }

  @Override
  public TextLineMetrics getTextLineMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
    diagnostics.increment(
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_ENTRIES);
    return measureText(text, font, fontSize, lineHeight).lines().get(0);
  }

  @Override
  public TextCaretMetrics getTextCaretMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float offsetX) {
    diagnostics.increment(
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES);
    return getTextCaretMetrics(text, List.of(font), fontSize, offsetX);
  }

  @Override
  public TextCaretMetrics getTextCaretMetrics(
      @NonNull String text, @NonNull List<Font> fonts, float fontSize, float offsetX) {
    diagnostics.increment(
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES);
    diagnostics.increment(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS);
    if (text.isEmpty() || offsetX <= 0) {
      return new TextCaretMetrics(0, 0);
    }

    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pCodePoint = stack.mallocInt(1);
      IntBuffer pAdvance = stack.mallocInt(1);
      int length = text.length();
      GlyphMeasurement previousGlyph = null;
      int i = 0;
      float currentX = 0;
      while (i < length) {
        diagnostics.increment(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED);
        int charStart = i;
        int codePointSize = getCodePointSize(text, length, i, pCodePoint);
        int codePoint = pCodePoint.get(0);
        int charEnd = i + codePointSize;
        if (codePoint == '\n') {
          return new TextCaretMetrics(charStart, currentX);
        }

        GlyphMeasurement glyph = resolveGlyph(fonts, codePoint);
        float glyphAdvance =
            measureNanoVgGlyphAdvance(glyph, previousGlyph, pAdvance, fontSize);
        if (offsetX < currentX + glyphAdvance / 2f) {
          return new TextCaretMetrics(charStart, currentX);
        }
        currentX += glyphAdvance;
        previousGlyph = glyph;
        i = charEnd;
      }
      return new TextCaretMetrics(length, currentX);
    }
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
      float currentMaxWidth,
      List<Font> fonts,
      float fontSize) {
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
            .runs(resolveRuns(text, safeStart, safeEnd, fonts, fontSize))
            .build());
    return Math.max(currentMaxWidth, width);
  }

  private GlyphMeasurement resolveGlyph(List<Font> fonts, int codePoint) {
    diagnostics.increment(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS);
    for (Font candidate : fonts) {
      STBTTFontinfo fontInfo = getFontInfo(candidate.path());
      diagnostics.increment(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES);
      int glyphIndex = stbtt_FindGlyphIndex(fontInfo, codePoint);
      if (glyphIndex != 0) {
        return new GlyphMeasurement(candidate, fontInfo, glyphIndex);
      }
    }

    // The replacement character is visible in the bundled fallback instead of becoming blank.
    for (Font candidate : fonts) {
      STBTTFontinfo fontInfo = getFontInfo(candidate.path());
      diagnostics.increment(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES);
      int glyphIndex = stbtt_FindGlyphIndex(fontInfo, REPLACEMENT_CODE_POINT);
      if (glyphIndex != 0) {
        return new GlyphMeasurement(candidate, fontInfo, glyphIndex);
      }
    }
    Font fallback = fonts.isEmpty() ? Font.DEFAULT : fonts.get(0);
    return new GlyphMeasurement(fallback, getFontInfo(fallback.path()), 0);
  }

  private float measureNanoVgGlyphAdvance(
      GlyphMeasurement glyph, GlyphMeasurement previousGlyph, IntBuffer pAdvance, float fontSize) {
    STBTTFontinfo fontInfo = glyph.fontInfo();
    float scaleFactor = stbtt_ScaleForMappingEmToPixels(fontInfo, fontSize);
    float width = 0;
    if (previousGlyph != null && previousGlyph.fontInfo() == fontInfo) {
      diagnostics.increment(TextDiagnosticCounter.NATIVE_KERNING_CALLS);
      width +=
          (int)
              (stbtt_GetGlyphKernAdvance(fontInfo, previousGlyph.glyphIndex(), glyph.glyphIndex())
                      * scaleFactor
                  + 0.5f);
    }
    diagnostics.increment(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS);
    stbtt_GetGlyphHMetrics(fontInfo, glyph.glyphIndex(), pAdvance, null);
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

  private List<ResolvedTextRun> resolveRuns(
      String text, int start, int end, List<Font> fonts, float fontSize) {
    if (start >= end || fonts.isEmpty()) return List.of();
    List<ResolvedTextRun> runs = new ArrayList<>();
    List<ResolvedGlyph> glyphs = new ArrayList<>();
    Font runFont = null;
    int runStart = start;
    float runAdvance = 0;
    GlyphMeasurement previous = null;
    for (int i = start; i < end; ) {
      diagnostics.increment(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED);
      int codePoint = text.codePointAt(i);
      int next = Math.min(end, i + Character.charCount(codePoint));
      Font selected = fonts.stream().filter(font -> hasGlyph(font, codePoint)).findFirst().orElse(fonts.get(0));
      boolean replacement = !hasGlyph(selected, codePoint);
      ResolvedGlyph glyph = new ResolvedGlyph(i, next, codePoint, replacement ? REPLACEMENT_CODE_POINT : codePoint, selected, replacement);
      GlyphMeasurement measurement = resolveGlyph(List.of(selected), replacement ? REPLACEMENT_CODE_POINT : codePoint);
      float advance = measureRunGlyphAdvance(measurement, previous, fontSize);
      if (previous == null || !previous.font().equals(selected)) {
        advance = measureRunGlyphAdvance(measurement, null, fontSize);
      }
      if (runFont != null && !runFont.equals(selected)) {
        addResolvedRun(runs, runStart, i, runFont, glyphs, runAdvance);
        glyphs = new ArrayList<>();
        runStart = i;
        runAdvance = 0;
      }
      runFont = selected;
      glyphs.add(glyph);
      diagnostics.increment(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS);
      runAdvance += advance;
      previous = measurement;
      i = next;
    }
    addResolvedRun(runs, runStart, end, runFont, glyphs, runAdvance);
    return runs;
  }

  private void addResolvedRun(
      List<ResolvedTextRun> runs,
      int start,
      int end,
      Font font,
      List<ResolvedGlyph> glyphs,
      float advance) {
    diagnostics.add(TextDiagnosticCounter.GLYPH_SLOTS_COPIED, glyphs.size());
    diagnostics.increment(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES);
    runs.add(new ResolvedTextRun(start, end, font, glyphs, advance));
    diagnostics.increment(TextDiagnosticCounter.RUN_BUILDER_APPENDS);
    diagnostics.increment(TextDiagnosticCounter.RUN_BUILDER_FREEZES);
  }

  private boolean hasGlyph(Font font, int codePoint) {
    diagnostics.increment(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES);
    return stbtt_FindGlyphIndex(getFontInfo(font.path()), codePoint) != 0;
  }

  private float measureRunGlyphAdvance(
      GlyphMeasurement glyph, GlyphMeasurement previousGlyph, float fontSize) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      return measureNanoVgGlyphAdvance(glyph, previousGlyph, stack.mallocInt(1), fontSize);
    }
  }

  private record GlyphMeasurement(Font font, STBTTFontinfo fontInfo, int glyphIndex) {}
}
