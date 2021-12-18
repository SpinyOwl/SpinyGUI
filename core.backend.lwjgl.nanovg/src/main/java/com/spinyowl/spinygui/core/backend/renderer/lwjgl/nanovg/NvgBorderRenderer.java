package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.inScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRectStroke;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class NvgBorderRenderer {

  public void initialize() {
    // currently no need to implement.
  }

  public void render(Element element, long nanovg) {
    inScissor(
        nanovg,
        element,
        () -> {
          var style = element.calculatedStyle();
          if (BorderStyle.NONE.equals(style.borderTopStyle())) return;
          float borderThickness = element.dimensions().border().top();
          drawRectStroke(
              nanovg,
              element.absolutePosition().add(borderThickness / 2, borderThickness / 2),
              element.dimensions().borderBoxSize().sub(borderThickness, borderThickness),
              style.borderTopColor(),
              borderThickness);
        });
  }

  public void destroy() {
    // currently no need to implement.
  }
}
