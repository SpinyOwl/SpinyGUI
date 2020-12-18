package com.spinyowl.spinygui.core.converter.css.model;

import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class RuleSet {
  private final @NonNull List<Selector> selectors;
  private final @NonNull List<Declaration<?>> declarations;

  public boolean test(Element element) {
    return selectors.stream().anyMatch(selector -> selector.test(element));
  }
}
