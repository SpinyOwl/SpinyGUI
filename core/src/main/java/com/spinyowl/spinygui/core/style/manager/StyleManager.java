package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.layout.LayoutNode;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.List;

public interface StyleManager {

  /**
   * Recalculates all styles for provided element and its children recursively using provided
   * stylesheets.
   */
  LayoutNode recalculate(Element element, List<StyleSheet> styleSheets);
}
