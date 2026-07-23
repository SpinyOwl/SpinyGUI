package com.spinyowl.spinygui.core.system.font.impl;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;

/** Default resolver backed by the explicitly registered {@link Font} families. */
public final class DefaultFontChainResolver implements FontChainResolver {

  @Override
  public List<Font> resolve(
      @NonNull List<String> fontFamilies,
      FontStyle style,
      FontWeight weight,
      FontStretch stretch) {
    return fontFamilies.stream()
        .filter(Font::hasFont)
        .flatMap(family -> Font.find(family).stream().sorted(faceOrder(style, weight, stretch)))
        .toList();
  }

  private static Comparator<Font> faceOrder(
      FontStyle style, FontWeight weight, FontStretch stretch) {
    return Comparator.comparingInt((Font font) -> matchingTraits(font, style, weight, stretch))
        .reversed()
        .thenComparingInt(font -> weightDistance(font, weight))
        .thenComparing(font -> font.style().name())
        .thenComparing(font -> font.stretch().name())
        .thenComparing(Font::path);
  }

  private static int matchingTraits(
      Font font, FontStyle style, FontWeight weight, FontStretch stretch) {
    int matches = 0;
    if (style != null && style.equals(font.style())) {
      matches++;
    }
    if (weight != null && weight.equals(font.weight())) {
      matches++;
    }
    if (stretch != null && stretch.equals(font.stretch())) {
      matches++;
    }
    return matches;
  }

  private static int weightDistance(Font font, FontWeight weight) {
    return weight == null ? 0 : Math.abs(font.weight().weight() - weight.weight());
  }
}
