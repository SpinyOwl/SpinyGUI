package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.withPresentedOpacity;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRectStroke;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import org.joml.Vector2f;

public class NvgBorderRenderer {

  public void render(Node node, long nanovg) {
    Element element = node.asElement();

    var style = element.resolvedStyle();
    if (BorderStyle.NONE.equals(style.borderTopStyle())) return;

    float borderThickness = element.box().border().top();
    if ((Display.INLINE.equals(style.display()) || Display.INLINE_BLOCK.equals(style.display()))
        && !element.inlineFragments().isEmpty()) {
      Vector2f offset = inlineFormattingOffset(element);
      element
          .inlineFragments()
          .forEach(
              fragment ->
                  drawRectStroke(
                      nanovg,
                      new Vector2f(
                          offset.x + fragment.x() + borderThickness / 2,
                          offset.y + fragment.y() + borderThickness / 2),
                      new Vector2f(
                          Math.max(0, fragment.width() - borderThickness),
                          Math.max(0, fragment.height() - borderThickness)),
                      withPresentedOpacity(element.presentedStyle().borderTopColor(), element),
                      borderThickness));
      return;
    }

    Vector2f position =
        element.layoutAbsolutePosition().add(borderThickness / 2, borderThickness / 2);
    Vector2f size = element.size().sub(borderThickness, borderThickness);

    drawRectStroke(
        nanovg,
        position,
        size,
        withPresentedOpacity(element.presentedStyle().borderTopColor(), element),
        borderThickness);
  }

  Vector2f inlineFormattingOffset(Element element) {
    Element parent = element.parent();
    while (parent != null && Display.INLINE.equals(parent.resolvedStyle().display())) {
      parent = parent.parent();
    }
    return parent == null
        ? new Vector2f()
        : parent.layoutAbsolutePosition();
  }
}
