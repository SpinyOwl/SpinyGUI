package com.spinyowl.spinygui.core.style.stylesheet.selector;

import com.spinyowl.spinygui.core.style.stylesheet.Specificity;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class CombinatorSelector implements Selector {

  @NonNull protected final Selector first;
  @NonNull protected final Selector second;

  @Override
  public Specificity specificity() {
    return first.specificity().add(second.specificity());
  }
}
