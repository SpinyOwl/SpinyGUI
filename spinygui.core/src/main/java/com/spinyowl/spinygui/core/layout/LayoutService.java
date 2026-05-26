package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import lombok.NonNull;

/** Layout service is an entry point to layout system. Used to layout provided element. */
public interface LayoutService {

  /**
   * Used to layout element tree.
   *
   * @param frame frame to lay out.
   */
  void layout(@NonNull Frame frame);

  /**
   * Used to layout node tree.<br>
   * For internal use by layout service.
   *
   * @param node node to layout.
   */
  void layoutNode(@NonNull Node node, @NonNull LayoutContext context);

  /**
   * Used to layout element tree.
   *
   * @param element element to layout.
   */
  void layoutChildNodes(@NonNull Element element, @NonNull LayoutContext context);
}
