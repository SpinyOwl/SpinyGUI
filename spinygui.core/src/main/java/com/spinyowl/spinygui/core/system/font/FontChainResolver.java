package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.impl.DefaultFontChainResolver;
import java.util.List;
import lombok.NonNull;

/** Resolves named CSS font families to registered faces in deterministic fallback order. */
public interface FontChainResolver {
  FontChainResolver DEFAULT = new DefaultFontChainResolver();

  /**
   * Resolves only the explicitly requested family names. Unavailable names are omitted. Within a
   * family, an exact style, weight, and stretch match comes first; otherwise the nearest bundled
   * face is selected using a deterministic style/weight/stretch/path ordering.
   *
   * @param fontFamilies ordered CSS family names
   * @param style requested font style
   * @param weight requested font weight
   * @param stretch requested font stretch
   * @return immutable ordered registered faces
   */
  List<Font> resolve(
      @NonNull List<String> fontFamilies,
      FontStyle style,
      FontWeight weight,
      FontStretch stretch);
}
