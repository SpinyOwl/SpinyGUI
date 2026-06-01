package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement;

import com.spinyowl.spinygui.core.style.stylesheet.Specificity;
import com.spinyowl.spinygui.core.style.stylesheet.selector.PseudoElementSelector;
import com.spinyowl.spinygui.core.style.types.ScrollbarPart;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScrollbarSelector implements PseudoElementSelector {

  @NonNull private final ScrollbarPart part;

  public ScrollbarSelector() {
    this(ScrollbarPart.SCROLLBAR);
  }

  @Override
  public Specificity specificity() {
    return Specificity.TYPE;
  }

  @Override
  public String toString() {
    return part.canonicalSelector();
  }
}
