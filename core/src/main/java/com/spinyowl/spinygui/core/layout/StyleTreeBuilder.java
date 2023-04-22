package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.List;

/** Used to build style tree from provided element and style sheets. */
public interface StyleTreeBuilder {

  /**
   * Used to build style tree from provided element and style sheets.
   *
   * @param element element to build style tree from.
   * @param styleSheets style sheets to use.
   * @return style tree root.
   */
  StyledNode build(Element element, List<StyleSheet> styleSheets);
}
