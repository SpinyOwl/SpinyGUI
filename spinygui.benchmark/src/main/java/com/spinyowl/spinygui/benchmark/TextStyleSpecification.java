package com.spinyowl.spinygui.benchmark;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import java.util.List;
import java.util.Objects;

/** Effective inline-text style shared by benchmark execution and declared-input identity. */
public record TextStyleSpecification(
    List<Font> orderedFonts,
    FontStyle fontStyle,
    FontWeight fontWeight,
    FontStretch effectiveFontStretch,
    float fontSizePx,
    float lineHeight,
    Color color,
    Display display,
    Position position,
    WhiteSpace whiteSpace,
    TextAlign textAlign,
    OverflowWrap overflowWrap,
    WordBreak wordBreak,
    int tabSize) {

  public TextStyleSpecification {
    orderedFonts = List.copyOf(orderedFonts);
    if (orderedFonts.isEmpty()) {
      throw new IllegalArgumentException("orderedFonts cannot be empty");
    }
    Objects.requireNonNull(fontStyle, "fontStyle");
    Objects.requireNonNull(fontWeight, "fontWeight");
    Objects.requireNonNull(effectiveFontStretch, "effectiveFontStretch");
    if (effectiveFontStretch != FontStretch.NORMAL) {
      throw new IllegalArgumentException(
          "Inline benchmark layout currently has fixed FontStretch.NORMAL treatment");
    }
    Objects.requireNonNull(color, "color");
    Objects.requireNonNull(display, "display");
    Objects.requireNonNull(position, "position");
    Objects.requireNonNull(whiteSpace, "whiteSpace");
    Objects.requireNonNull(textAlign, "textAlign");
    Objects.requireNonNull(overflowWrap, "overflowWrap");
    Objects.requireNonNull(wordBreak, "wordBreak");
    if (!(fontSizePx > 0) || !Float.isFinite(fontSizePx)) {
      throw new IllegalArgumentException("fontSizePx must be positive and finite");
    }
    if (!(lineHeight > 0) || !Float.isFinite(lineHeight)) {
      throw new IllegalArgumentException("lineHeight must be positive and finite");
    }
    if (tabSize <= 0) {
      throw new IllegalArgumentException("tabSize must be positive");
    }
  }

  public List<String> fontFamilies() {
    return orderedFonts.stream().map(Font::fontFamily).toList();
  }

  /** Includes every field that distinguishes the exact ordered {@link Font} objects. */
  public List<String> fontObjectIdentities() {
    return orderedFonts.stream().map(TextStyleSpecification::fontObjectIdentity).toList();
  }

  /** Exact faces selected by the production default resolver for this effective style. */
  public List<Font> resolvedFonts() {
    return FontChainResolver.DEFAULT.resolve(
        fontFamilies(), fontStyle, fontWeight, effectiveFontStretch);
  }

  public List<String> resolvedFontObjectIdentities() {
    return resolvedFonts().stream().map(TextStyleSpecification::fontObjectIdentity).toList();
  }

  public void apply(Element element) {
    ResolvedStyle style = element.resolvedStyle();
    style.display(display);
    style.position(position);
    style.fontFamilies(fontFamilies());
    style.fontStyle(fontStyle);
    style.fontWeight(fontWeight);
    style.fontSize(Length.pixel(fontSizePx));
    style.lineHeight(lineHeight);
    style.color(color);
    style.whiteSpace(whiteSpace);
    style.textAlign(textAlign);
    style.overflowWrap(overflowWrap);
    style.wordBreak(wordBreak);
    style.tabSize(tabSize);
  }

  public static String fontObjectIdentity(Font font) {
    Objects.requireNonNull(font, "font");
    return String.join(
        "|",
        font.fontFamily(),
        font.style().name(),
        font.stretch().name(),
        font.weight().name(),
        font.path());
  }
}
