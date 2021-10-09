package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.style.types.Display;

/** Layout provider. Used to store layout managers for different display types. */
public interface LayoutProvider {

  /**
   * Used to add layout manager for specified display type.
   *
   * @param displayType display type.
   * @param layout layout manager to add.
   */
  void addLayoutManager(Display displayType, Layout layout);

  /**
   * Used to remove layout manager for specified display type.
   *
   * @param displayType display type.
   */
  void removeLayoutManager(Display displayType);

  /**
   * Used to get layout manager by display type.
   *
   * @param displayType display type to get layout manager.
   * @return layout manager by display type.
   */
  Layout getLayoutManager(Display displayType);
}
