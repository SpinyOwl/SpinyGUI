package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.style.types.Display;

/**
 * Layout manager. Used to layout element and element's child components.
 */
public interface LayoutManager {

  /**
   * Used to register layout for specified display type.
   *
   * @param displayType display type.
   * @param layout      layout to register.
   */
  void registerLayout(Display displayType, Layout layout);

  /**
   * Used to layout element and all of it's child components.
   *
   * @param element element to lay out.
   */
  void layout(Element element);

}
