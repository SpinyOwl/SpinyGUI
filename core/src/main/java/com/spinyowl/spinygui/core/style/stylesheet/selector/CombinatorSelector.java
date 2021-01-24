package com.spinyowl.spinygui.core.style.stylesheet.selector;

import com.spinyowl.spinygui.core.style.stylesheet.Specificity;
import lombok.Data;

@Data
public abstract class CombinatorSelector implements Selector {

  protected final Selector first;
  protected final Selector second;

  @Override
  public Specificity specificity() {
    return first.specificity().add(second.specificity());
  }
}
