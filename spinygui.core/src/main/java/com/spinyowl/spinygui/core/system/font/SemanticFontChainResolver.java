package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import java.util.ArrayList;
import java.util.Collection;
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
    Collection<Font> registered = owner.registeredFontView();
    Comparator<Font> order = faceOrder(style, weight, stretch);
    List<Font> resolved = new ArrayList<>();
    for (String family : fontFamilies) {
      List<Font> matching = new ArrayList<>();
      for (Font font : registered) {
        if (font.fontFamily().equalsIgnoreCase(family)) {
          matching.add(font);
        }
      }
      matching.sort(order);
      resolved.addAll(matching);
    }
    return List.copyOf(resolved);
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
