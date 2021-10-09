package com.spinyowl.spinygui.core.layout;

import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visibleInParents;
import com.spinyowl.spinygui.core.node.Element;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Layout service is an entry point to layout system. Used to layout provided element. */
@RequiredArgsConstructor
public class LayoutService {
  @NonNull private final LayoutProvider layoutProvider;

  public void layout(Element element) {
    if (element != null && visible(element) && visibleInParents(element)) {
      var layoutManager = layoutProvider.getLayoutManager(element.calculatedStyle().display());
      if (layoutManager != null) {
        layoutManager.layout(element);
      }

      element.children().forEach(this::layout);
    }
  }
}
