package com.spinyowl.spinygui.core.style.stylesheet.selector.simple;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.Specificity;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The CSS ID selector matches an element based on the value of the element’s id attribute.
 *
 * <p>In order for the element to be selected, its id attribute must match exactly the value given
 * in the selector.
 */
@Getter
@RequiredArgsConstructor
public class IdAttributeSelector implements Selector {
  @NonNull private final String id;

  @Override
  public boolean test(Element element) {
    var idAttribute = element.getAttribute("id");
    if (idAttribute != null && !idAttribute.isBlank()) {
      return id.equals(idAttribute);
    }
    return false;
  }

  @Override
  public Specificity specificity() {
    return Specificity.ID;
  }

  @Override
  public String toString() {
    return "#" + id;
  }
}
