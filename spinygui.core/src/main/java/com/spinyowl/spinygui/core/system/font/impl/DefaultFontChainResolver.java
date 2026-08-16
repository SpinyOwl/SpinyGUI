package com.spinyowl.spinygui.core.system.font.impl;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import java.util.List;
import lombok.NonNull;

/** Compatibility resolver that delegates every use to the explicitly installed semantic owner. */
public final class DefaultFontChainResolver implements FontChainResolver {

  @Override
  public List<Font> resolve(
      @NonNull List<String> fontFamilies,
      FontStyle style,
      FontWeight weight,
      FontStretch stretch) {
    return Font.semanticOwner().resolver().resolve(fontFamilies, style, weight, stretch);
  }
}
