package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.layout.LayoutNode;
import com.spinyowl.spinygui.core.layout.LayoutTreeBuilder;
import com.spinyowl.spinygui.core.layout.StyleTreeBuilder;
import com.spinyowl.spinygui.core.layout.StyledNode;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StyleManagerImpl implements StyleManager {

  @NonNull private final StyleTreeBuilder styleTreeBuilder;
  @NonNull private final LayoutTreeBuilder layoutTreeBuilder;

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
