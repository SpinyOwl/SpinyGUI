package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRectStroke;
import com.spinyowl.spinygui.core.layout.LayoutNode;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class NvgBorderRenderer {

  public void initialize() {
    // currently no need to implement.
  }

  public void render(LayoutNode layoutNode, long nanovg) {
    Element element = layoutNode.node().asElement();

    createScissor(nanovg, layoutNode);
    var style = element.calculatedStyle();
    if (BorderStyle.NONE.equals(style.borderTopStyle())) return;
    float borderThickness = element.dimensions().border().top();
    drawRectStroke(
        nanovg,
        element.dimensions().borderBoxPosition().add(borderThickness / 2, borderThickness / 2),
        element.dimensions().borderBoxSize().sub(borderThickness, borderThickness),
        style.borderTopColor(),
        borderThickness);
    resetScissor(nanovg);
  }

  public void destroy() {
    // currently no need to implement.
  }
}
