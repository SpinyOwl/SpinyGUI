package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.List;
import java.util.Queue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractStyleManager implements StyleManager {

  @NonNull private final Queue<Frame> frameQueue;

  public final void needRecalculate(Frame frame) {
    if (!frameQueue.contains(frame)) {
      frameQueue.add(frame);
    }
  }

  public final void recalculate() {
    if (!frameQueue.isEmpty()) {
      var frames = List.copyOf(frameQueue);
      for (Frame frame : frames) {
        recalculate(frame);
      }
      frameQueue.removeAll(frames);
    }
  }

  protected void recalculate(Frame frame) {
    frame.styleSheets().forEach(styleSheet -> updateStylesFromStyleSheet(frame, styleSheet));
  }

  private void updateStylesFromStyleSheet(Element element, StyleSheet styleSheet) {
    styleSheet
        .searchSpecificRules(element)
        .forEach(
            ruleSet -> ruleSet.declarations().forEach(p -> updateStylesFromStyleSheet(element, p)));
    element
        .children()
        .forEach(childElement -> updateStylesFromStyleSheet(childElement, styleSheet));
  }

  private void updateStylesFromStyleSheet(Element element, Declaration<?> declaration) {
    declaration.property().computeAndApply(element, declaration.value());
  }
}
