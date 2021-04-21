package com.spinyowl.spinygui.core.style.stylesheet.selector.simple;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.Specificity;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import java.util.Arrays;
import lombok.Data;

/**
 * The class selector selects elements with a specific class attribute.
 *
 * <p>To select elements with a specific class, write a period (.) character, followed by the class
 * name.
 */
@Data
public class ClassAttributeSelector implements Selector {

  private final String className;

  @Override
  public boolean test(Element element) {
    var classAttributes = element.getAttribute("class");
    if (classAttributes != null) {
      var classList = classAttributes.split("\\s+");
      return Arrays.asList(classList).contains(className);
    }
    return false;
  }

  @Override
  public Specificity specificity() {
    return Specificity.of(0, 1, 0);
  }
}
