package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;

/** Resolver whose complete font view is supplied by one semantic owner snapshot. */
final class SemanticFontChainResolver implements FontChainResolver {
  private final SemanticFontOwner owner;

  SemanticFontChainResolver(SemanticFontOwner owner) {
    this.owner = owner;
  }

  @Override
  public List<Font> resolve(
      @NonNull List<String> fontFamilies,
      FontStyle style,
      FontWeight weight,
      FontStretch stretch) {
    List<Font> registered = owner.registeredFonts();
    return fontFamilies.stream()
        .flatMap(
            family ->
                registered.stream()
                    .filter(font -> font.fontFamily().equalsIgnoreCase(family))
                    .sorted(faceOrder(style, weight, stretch)))
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
