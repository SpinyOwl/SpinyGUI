package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.StyledNode;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.tree.LayoutNode;
import com.spinyowl.spinygui.core.system.tree.LayoutTreeBuilder;
import com.spinyowl.spinygui.core.system.tree.StyleTreeBuilder;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StyleManagerImpl implements StyleManager {

  @NonNull private final StyleTreeBuilder styleTreeBuilder;
  @NonNull private final LayoutTreeBuilder layoutTreeBuilder;
  @NonNull private final SystemEventProcessor systemEventProcessor;

  @Override
  public LayoutNode recalculate(Element element, List<StyleSheet> styleSheets) {
    StyledNode styledNode = styleTreeBuilder.build(element, styleSheets);
    LayoutNode layoutNode = layoutTreeBuilder.build(styledNode);
    resolveStyles(layoutNode);
    return layoutNode;
  }

  private void resolveStyles(LayoutNode layoutNode) {
    if (layoutNode.isElement()) {
      Element element = layoutNode.element();
      List<Ruleset> rules = element.resolvedStyle().rules();
      rules.forEach(rs -> rs.declarations().forEach(declaration -> declaration.apply(element)));
      layoutNode.children().forEach(this::resolveStyles);
    }
  }
}
